package malva.java.lang;

import java.io.IOException;

import malva.TestCase;

public class ProcessTest extends TestCase {

  public static void testDestroy() {
    ProcessBuilder processBuilder = new ProcessBuilder("echo", "test");
    try {
      Process process = processBuilder.start();
      process.destroy();
    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
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
    testerThread.join(joinTimeMs);
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

  public static void main(String[] args) {
    testDestroy();
//    testExitValue();
    testGetErrorStream();
    testGetInputStream();
    testGetOutputStream();
    testInterrupted();
    testWaitFor();
  }
}
