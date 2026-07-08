package malva.java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import malva.TestCase;

public class ProcessBuilderTest extends TestCase {

  public static void testStartFailure() {
    // Trying to run a non-existing binary must fail with an IOException
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        new ProcessBuilder("malva-no-such-binary").start();
      }
    }, IOException.class);

    // An empty command must also fail with an IOException
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        new ProcessBuilder("").start();
      }
    }, IOException.class);
  }

  public static void testPathSearch() {
    try {
      // Commands without a slash must be looked up in the PATH
      Process process = new ProcessBuilder("env").start();
      readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  public static void testEnvironment() {
    try {
      ProcessBuilder pb = new ProcessBuilder("env");
      pb.environment().put("MALVA_TEST", "1");
      Process process = pb.start();
      String output = readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
      assertTrue(output.contains("MALVA_TEST=1"));
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  public static void testDirectory() {
    try {
      File dir = new File("/tmp");
      Process process = new ProcessBuilder("pwd").directory(dir).start();
      String output = readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
      assertEquals(dir.getCanonicalPath(), output.trim());
    } catch (Exception e) {
      fail("Test failed: " + e);
    }

    // A non-existing working directory must fail with an IOException
    assertThrows(new Block() {
      @Override public void run() throws IOException {
        new ProcessBuilder("pwd").directory(new File("/no/such/dir")).start();
      }
    }, IOException.class);
  }

  public static void testRedirectErrorStream() {
    try {
      ProcessBuilder pb = new ProcessBuilder("sh", "-c", "echo stderr-marker 1>&2");
      assertFalse(pb.redirectErrorStream());
      pb.redirectErrorStream(true);
      assertTrue(pb.redirectErrorStream());
      Process process = pb.start();
      String output = readAll(process.getInputStream());
      assertEquals(0, process.waitFor());
      assertTrue(output.contains("stderr-marker"));
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  private static String readAll(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    int c;
    while ((c = in.read()) != -1)
      sb.append((char) c);
    return sb.toString();
  }

  public static void main(String[] args) {
    testDirectory();
    testEnvironment();
    testPathSearch();
    testRedirectErrorStream();
    testStartFailure();
  }
}
