package malva.java.lang;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import malva.TestCase;

/* Tests that process spawning works in a JVM that runs with its
 * standard input closed. Closing stdin cannot be undone and would
 * affect every test running afterwards in the same JVM, so these
 * tests live in their own class.
 */
public class ProcessClosedStdioTest extends TestCase {

  public static void testChildFds() throws Exception {
    // Check that the child has exactly fds 0, 1, 2 open
    Process process = Runtime.getRuntime().exec(
        new String[] {nativeHelper("fdreport").getAbsolutePath()});
    String output = readAll(process.getInputStream()).trim();
    String error = readAll(process.getErrorStream()).trim();
    int status = process.waitFor();
    if (status != 0)
      fail("fdreport exited with status " + status + ": " + error);

    assertEquals("0 1 2", output.replaceAll("\\s+", " "));
  }

  public static void testStdinStdoutWork() throws Exception {
    Process process = Runtime.getRuntime().exec(new String[] {"cat"});
    try {
      process.getOutputStream().write('X');
      process.getOutputStream().flush();
      assertEquals((int) 'X', process.getInputStream().read());
    } finally {
      process.destroy();
    }
  }

  public static void testStderrWorks() throws Exception {
    Process process = Runtime.getRuntime().exec(
        new String[] {"sh", "-c", "echo X >&2"});
    assertEquals("X", readAll(process.getErrorStream()).trim());
    assertEquals(0, process.waitFor());
  }

  private static String readAll(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    int c;
    while ((c = in.read()) != -1)
      sb.append((char) c);
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {
    // OpenJDK does not actually close stdin internally; attempting to close
    // it from Java redirects it to /dev/null instead, so the following tests
    // are not applicable.
    if (isOpenJdk()) {
      logSkip("ProcessClosedStdioTest: JVM redirects fd 0 instead of closing it");
      return;
    }

    new FileInputStream(FileDescriptor.in).close();

    // Do not open files after this point, as they would reuse fd 0

    testChildFds();
    testStdinStdoutWork();
    testStderrWorks();
  }
}
