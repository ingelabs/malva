package malva.java.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import malva.TestCase;

public class ProcessTest extends TestCase {
  /** If running on CI, widen timing tolerances */
  private static final boolean ON_CI = System.getenv("CI") != null;

  private static final long JOIN_EXTRA_MS = ON_CI ? 200 : 0;

  private static void testDestroy(String[] cmd, boolean readOutput, int joinTimeMs) throws Exception {
    final Process process = Runtime.getRuntime().exec(cmd);
    if (readOutput) {
      // Read all output after which the process will complete
      while (process.getInputStream().read() != -1);
    }

    Thread testerThread = new Thread(new Runnable() {
      @Override public void run() {
        process.destroy();
      }
    });

    testerThread.start();
    testerThread.join(joinTimeMs + JOIN_EXTRA_MS);
    // Check that the thread exited
    assertFalse(testerThread.isAlive());
  }

  public static void testDestroy() {
    try {
      // Test calling destroy() after the process has already finished
      testDestroy(new String[] {"env"}, true, 100);
      // Test calling destroy() before the process finishes
      testDestroy(new String[] {"sleep", "1"}, false, 150);
    } catch (Exception e) {
      fail("Test failed: "  + e);
    }
  }

  public static void testExecFailure() {
    // Trying to run a non-existing binary must fail with an IOException
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        Runtime.getRuntime().exec("malva-no-such-binary");
      }
    }, IOException.class);

    assertThrows(new Block() {
      @Override public void run() throws IOException {
        Runtime.getRuntime().exec(new String[] {"malva-no-such-binary"});
      }
    }, IOException.class);

    // Same when a non-null environment is passed
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        Runtime.getRuntime().exec("malva-no-such-binary", new String[] { "MALVA_TEST=1" });
      }
    }, IOException.class);
  }

  public static void testFailedExecDoesNotCloseUnrelatedFds() {
    /*
     * GNU Classpath had a bug where an exec() that fails early (due to e.g.
     * NULL command) could close unrelated fds.
     *
     * We reproduced this on Linux by successfully spawning a child first,
     * then trying an invalid spawn. After the successful nativeSpawn()
     * returns, its parent-side pipe fd values remain in the released native
     * stack space. The subsequent nativeSpawn() may reuse that stack space
     * and, because its fds array was never initialized, close those stale
     * pipe fds during cleanup. The test keeps the first child running so
     * that the unintended close can be observed easily.
     *
     * Stack reuse is not guaranteed, but we managed to reliably reproduce
     * the problem when running this sequence twice. Hence, we make three
     * attempts.
     */
    for (int attempt = 0; attempt < 3; attempt++) {
      try {
        final Process process = Runtime.getRuntime().exec(new String[] { "cat" });
        try {
          assertThrows(new Block() {
            @Override
            public void run() throws IOException {
              Runtime.getRuntime().exec(new String[] { null });
            }
          }, NullPointerException.class);

          process.getOutputStream().write('X');
          process.getOutputStream().flush();
          assertEquals((int) 'X', process.getInputStream().read());
        } finally {
          process.destroy();
        }
      } catch (IOException e) {
        fail("Failed exec closed another process pipe: " + e);
      }
    }
  }

  public static void testExecDoesNotLeakProcessPipes() throws IOException, InterruptedException {
    Process first = null;
    Process second = null;

    try {
      first = Runtime.getRuntime().exec(new String[] {"cat"});

      // Start another process while the write end of the first process's
      // stdin pipe is open. The new process must not keep a copy after exec.
      second = Runtime.getRuntime().exec(new String[] {"sleep", "10"});

      // Close the write end of the first process's stdin; cat should see
      // an EOF and exit.
      first.getOutputStream().close();

      final Process p1 = first;
      Thread waiter = new Thread(new Runnable() {
        @Override public void run() {
          try {
            p1.waitFor();
          } catch (InterruptedException ignored) {
          }
        }
      });
      waiter.start();
      waiter.join(1000 + JOIN_EXTRA_MS);

      assertFalse(waiter.isAlive());
      assertEquals(0, first.exitValue());

      // The second process must still be running: the first process should
      // have observed EOF without waiting for the second one to exit.
      final Process p2 = second;
      assertThrows(new Block() {
        @Override public void run() {
          p2.exitValue();
        }
      }, IllegalThreadStateException.class);
    } finally {
      if (first != null)
        first.destroy();
      if (second != null)
        second.destroy();
    }
  }

  public static void testExecDoesNotLeakFds() throws IOException, InterruptedException {
    // Hold a set of open fds that must not be visible in the child
    FileInputStream[] held = new FileInputStream[8];
    for (int i = 0; i < held.length; i++)
      held[i] = new FileInputStream("/etc/hosts");

    try {
      // fdreport prints the number of every fd open in the child, one
      // per line; only stdio (0-2) may be inherited
      Process process = Runtime.getRuntime().exec(
          new String[] {nativeHelper("fdreport").getAbsolutePath()});
      String output = readAll(process.getInputStream()).trim();
      String error = readAll(process.getErrorStream()).trim();
      int status = process.waitFor();
      if (status != 0)
        fail("fdreport exited with status " + status + ": " + error);
      assertEquals("0 1 2", output.replaceAll("\\s+", " "));
    } finally {
      for (FileInputStream f : held)
        f.close();
    }
  }

  public static void testExecClearsSignalMask() throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec(
        new String[] {nativeHelper("sigreport").getAbsolutePath()});
    String output = readAll(process.getInputStream()).trim();
    String error = readAll(process.getErrorStream()).trim();
    int status = process.waitFor();
    if (status != 0)
      fail("sigreport exited with status " + status + ": " + error);
    if (output.length() == 0)
      fail("Empty signal report");

    assertNoBlockedSignals(output);
  }

  private static void assertNoBlockedSignals(String report) {
    for (String line : report.split("\n"))
      if (line.contains("blocked")) {
        // OpenJDK used to start children with SIGQUIT blocked (JDK-8234262).
        // This was fixed in OpenJDK 20 (for the default posix_spawn mechanism).
        if (line.startsWith("SIGQUIT:") && isOpenJdk() && javaFeatureVersion() < 20)
          continue;

        fail("Signal is blocked in spawned process: " + line);
      }
  }

  public static void testExecPathSearch() {
    try {
      // PATH must be searched if command does not contain a slash
      Process process = Runtime.getRuntime().exec("env",
          new String[] {"MALVA_TEST=1"});
      readAll(process.getInputStream());
      assertEquals(0, process.waitFor());

      // Commands containing a slash must be executed directly
      File script = File.createTempFile("malva", ".sh");
      try {
        FileOutputStream out = new FileOutputStream(script);
        out.write("#!/bin/sh\nexit 0\n".getBytes("US-ASCII"));
        out.close();
        script.setExecutable(true);
        process = Runtime.getRuntime().exec(script.getPath(),
            new String[] {"MALVA_TEST=1"});
        assertEquals(0, process.waitFor());
      } finally {
        script.delete();
      }

      // ...and the PATH must not be searched for them.
      // Running "./env" from an empty directory must fail, even though
      // "env" is found in the PATH. If the PATH were (wrongly) searched,
      // "./env" would resolve against a PATH entry (e.g. "/usr/bin" +
      // "/./env") and succeed.
      final File emptyDir = File.createTempFile("malva", null);
      emptyDir.delete();
      emptyDir.mkdir();
      try {
        assertThrows(new Block() {
          @Override public void run() throws IOException {
            Runtime.getRuntime().exec(new String[] {"./env"},
                                      new String[] {"MALVA_TEST=1"}, emptyDir);
          }
        }, IOException.class);
      } finally {
        emptyDir.delete();
      }
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  public static void testExecEnvironment() {
    try {
      // The passed environment must reach the child process
      Process process = Runtime.getRuntime().exec(new String[] {"env"},
          new String[] {"MALVA_TEST=1"});
      String output = readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
      assertTrue(output.contains("MALVA_TEST=1"));
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  public static void testExecInDir() {
    try {
      File dir = new File("/tmp");
      Process process = Runtime.getRuntime().exec(new String[] {"pwd"}, null, dir);
      String output = readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
      assertEquals(dir.getCanonicalPath(), output.trim());
    } catch (Exception e) {
      fail("Test failed: " + e);
    }

    // A non-existing working directory must fail with an IOException
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        Runtime.getRuntime().exec(new String[] {"pwd"}, null,
                                  new File("/no/such/dir"));
      }
    }, IOException.class);
  }

  public static void testExitValue() {

    try {
      ProcessBuilder processBuilder = new ProcessBuilder("env");
      Process process = processBuilder.start();
      // Read all output after which the process will complete
      while (process.getInputStream().read() != -1);
      process.waitFor();
      assertEquals(0, process.exitValue());
    } catch (Exception e) {
      fail("Test failed: " + e.getMessage());
    }

    try {
      // A non-zero exit value must be reported as-is
      Process process = Runtime.getRuntime().exec(new String[] {"sh", "-c", "exit 7"});
      assertEquals(7, process.waitFor());
    } catch (Exception e) {
      fail("Test failed: " + e);
    }

    try {
      // Exit values are in the range 0-255 and must not be truncated
      // to a signed byte (e.g. 250 must not become -6)
      int[] codes = {127, 128, 200, 250, 255};
      for (int code : codes) {
        Process process = Runtime.getRuntime().exec(
            new String[] {"sh", "-c", "exit " + code});
        assertEquals(code, process.waitFor());
      }
    } catch (Exception e) {
      fail("Test failed: " + e);
    }

    try {
      ProcessBuilder processBuilder = new ProcessBuilder("sleep", "10");
      final Process process = processBuilder.start();

      assertThrows(new Block() {
        @Override public void run() {
          process.exitValue();
        }
      }, IllegalThreadStateException.class);

      process.destroy();
      process.waitFor();
    } catch (Exception e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  public static void testGetErrorStream() {
    ProcessBuilder processBuilder = new ProcessBuilder("echo", "test");
    try {
      Process process = processBuilder.start();
      assertNotNull(process.getErrorStream());
      process.destroy();
    } catch (IOException e) {
      fail("Test failed: "  + e.getMessage());
    }
  }

  public static void testGetInputStream() {
    ProcessBuilder processBuilder = new ProcessBuilder("echo", "test");
    try {
      Process process = processBuilder.start();
      assertNotNull(process.getInputStream());
      process.destroy();
    } catch (IOException e) {
      fail("Test failed: "  + e.getMessage());
    }
  }

  public static void testGetOutputStream(){
    ProcessBuilder processBuilder = new ProcessBuilder("echo", "test");
    try {
      Process process = processBuilder.start();
      assertNotNull(process.getOutputStream());
      process.destroy();
    } catch (IOException e) {
      fail("Test failed: "  + e.getMessage());
    }
  }

  private static void testWaitFor(String[] cmd, boolean readOutput, int joinTimeMs) throws Exception {
    final Process process = Runtime.getRuntime().exec(cmd);
    if (readOutput) {
      // Read all output after which the process will complete
      while (process.getInputStream().read() != -1);
    }

    Thread testerThread = new Thread(new Runnable() {
      @Override public void run() {
        try {
          process.waitFor();
        } catch (InterruptedException e) {
          // ignored
        }
      }
    });

    testerThread.start();
    testerThread.join(joinTimeMs + JOIN_EXTRA_MS);
    // Check that the thread exited, and that the process finished successfully
    assertFalse(testerThread.isAlive());
    assertEquals(0, process.exitValue());
  }

  public static void testWaitFor() {
    try {
      // Test calling waitFor() after the process has already finished
      testWaitFor(new String[] {"env"}, true, 100);
      // Test calling waitFor() before the process finishes
      testWaitFor(new String[] {"sleep", "0.1"}, false, 250);
    } catch (Exception e) {
      fail("Test failed: "  + e);
    }
  }

  public static void testInterrupted()
  {
    try {
      // Check interrupted status is not cleared when process is started
      Thread.currentThread().interrupt();
      Process process = Runtime.getRuntime().exec("sleep 5");
      assertTrue(Thread.interrupted());

      // Check interrupted status is not cleared when destroy is called
      assertFalse(Thread.interrupted());
      Thread.currentThread().interrupt();
      process.destroy();
      assertTrue(Thread.interrupted());
    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  private static String readAll(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    int c;
    while ((c = in.read()) != -1)
      sb.append((char) c);
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {
    testDestroy();
    testExecEnvironment();
    testExecFailure();
    testFailedExecDoesNotCloseUnrelatedFds();
    testExecDoesNotLeakProcessPipes();
    testExecDoesNotLeakFds();
    testExecClearsSignalMask();
    testExecInDir();
    testExecPathSearch();
    testExitValue();
    testGetErrorStream();
    testGetInputStream();
    testGetOutputStream();
    testInterrupted();
    testWaitFor();
  }
}
