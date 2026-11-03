package malva.java.lang;

import malva.TestCase;

/**
 * Checks that the stack trace of a running thread with a very deep
 * stack can be sampled through Thread.getStackTrace().
 *
 * This class deliberately does not end in "Test": the suite must not
 * run it directly. It is launched by thread-deep-stack-trace-test.sh,
 * which passes the required stack size on the command line as well,
 * for VMs that ignore the Thread constructor's stack size hint.
 */
public class ThreadDeepStackTrace extends TestCase {
  private static final int RECURSION_DEPTH = 100000;

  // Stack size for the target thread (64 MB), large enough to recurse
  // RECURSION_DEPTH times.
  private static final long STACK_SIZE = 64 * 1024 * 1024;
  private static volatile boolean ready;
  private static volatile Throwable targetFailure;
  private static volatile Throwable readerFailure;
  private static volatile StackTraceElement[] stackTrace;

  private static void recurse(int depth) throws InterruptedException {
    if (depth == 0) {
      ready = true;
      Thread.sleep(60000);
      return;
    }

    recurse(depth - 1);
  }

  public static void testDeepStackTrace() throws Exception {
    final Thread target = new Thread(null, new Runnable() {
      public void run() {
        try {
          recurse(RECURSION_DEPTH);
        } catch (Throwable t) {
          targetFailure = t;
          ready = true;
        }
      }
    }, "deep-stack-target", STACK_SIZE);
    target.setDaemon(true);
    target.start();

    while (!ready)
      Thread.yield();

    assertNull(targetFailure);

    // Sample the stack trace from a separate daemon thread: if
    // getStackTrace() hangs on the deep stack, the join() call still
    // times out and the VM can exit, instead of stalling the suite.
    Thread reader = new Thread(new Runnable() {
      public void run() {
        try {
          stackTrace = target.getStackTrace();
        } catch (Throwable t) {
          readerFailure = t;
        }
      }
    }, "stack-trace-reader");
    reader.setDaemon(true);

    reader.start();
    reader.join(30000);
    assertFalse(reader.isAlive());

    // The reader only records results; all checks happen here
    assertNull(readerFailure);
    assertNotNull(stackTrace);
    assertTrue(stackTrace.length > 0);
  }

  public static void main(String[] args) throws Exception {
    testDeepStackTrace();
  }
}
