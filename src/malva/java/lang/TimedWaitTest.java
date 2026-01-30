package malva.java.lang;

import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

import malva.TestCase;

public class TimedWaitTest extends TestCase {
    /**
     * Disabled by default since these tests need to change the system time,
     * which requires privileges.
     */
    private static final boolean ENABLE_TIME_JUMP_TESTS = false;

    private static final long NOMINAL_WAIT_MS = 500;
    private static final long WAIT_TOL_MS = 50;
    private static final long JOIN_EXTRA_MS = 100;

    private static final long NOMINAL_JUMP_TESTS_WAIT_MS = 3000;

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    private static long nowNanos() {
        return System.nanoTime();
    }

    private static void assertTimeInRangeMs(long time, long target, long tol) {
        if (Math.abs(target - time) > tol) {
            fail("Time out of range, expected: " + target + "+/-" + tol + "ms, actual: " + time + "ms");
        }
    }

    private static void assertThreadFinishes(Thread t, long timeout) throws InterruptedException {
        t.join(timeout);
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread did not finish within timeout");
        }
    }

    private static void testUntimedPark() throws InterruptedException {
        final long[] elapsedHolder = new long[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long t1 = nowNanos();
                LockSupport.park();
                elapsedHolder[0] = (nowNanos() - t1) / 1000000;
            }
        });
        t.start();
        sleep(NOMINAL_WAIT_MS);
        LockSupport.unpark(t);
        assertThreadFinishes(t, JOIN_EXTRA_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_MS);
    }

    private static void testRelativeTimedPark() throws InterruptedException {
        final long[] elapsedHolder = new long[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long t1 = nowNanos();
                LockSupport.parkNanos(1000000 * NOMINAL_WAIT_MS);
                elapsedHolder[0] = (nowNanos() - t1) / 1000000;
            }
        });        
        t.start();
        assertThreadFinishes(t, NOMINAL_WAIT_MS + WAIT_TOL_MS + JOIN_EXTRA_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_MS);
    }

    private static void testAbsoluteTimedPark() throws InterruptedException {
        final long[] elapsedHolder = new long[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long start = System.currentTimeMillis();
                long deadline = start + NOMINAL_WAIT_MS;
                LockSupport.parkUntil(deadline);
                elapsedHolder[0] = System.currentTimeMillis() - start;
            }
        });
        t.start();
        assertThreadFinishes(t, NOMINAL_WAIT_MS + WAIT_TOL_MS + JOIN_EXTRA_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_MS);
    }

    private static void setSystemTimeSeconds(long epochSeconds) throws IOException, InterruptedException {
        String[] cmd = new String[] { "date", "-s", "@" + epochSeconds };
        Process p = Runtime.getRuntime().exec(cmd);
        // Read until EOF to make sure command has run
        // Avoid calling p.waitFor since it uses Object.wait internally in GNU Classpath
        p.getErrorStream().close();
        InputStream in = p.getInputStream();
        while (in.read() != -1) {}
        in.close();
    }

    private static void testWaitOrSleepWithTimeJump(final boolean useSleep) throws Exception {
        final long[] elapsedHolder = new long[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long t1 = nowNanos();
                if (useSleep) {
                    try { Thread.sleep(NOMINAL_JUMP_TESTS_WAIT_MS); } catch (InterruptedException e) {}
                } else {
                    Object lock = TimedWaitTest.class;
                    synchronized (lock) {
                        try { lock.wait(NOMINAL_JUMP_TESTS_WAIT_MS); } catch (InterruptedException e) {}
                    }
                }
                elapsedHolder[0] = (nowNanos() - t1) / 1000000;
            }
        });
        t.start();

        long nowSeconds = System.currentTimeMillis() / 1000;

        // Move the system time forward, check that the thread didn't exit early
        setSystemTimeSeconds(nowSeconds + 60);
        t.join(JOIN_EXTRA_MS);
        if (!t.isAlive())
            fail((useSleep ? "sleep" : "wait") + "() finished early when system time jumped forward");

        // Move the system time backward, check that the thread finishes in time
        setSystemTimeSeconds(nowSeconds - 60);
        assertThreadFinishes(t, JOIN_EXTRA_MS + NOMINAL_JUMP_TESTS_WAIT_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_JUMP_TESTS_WAIT_MS, WAIT_TOL_MS);

        // Restore system time (approximation)
        setSystemTimeSeconds(nowSeconds + NOMINAL_JUMP_TESTS_WAIT_MS / 1000);
    }

    public static void testSleepWithTimeJump() throws Exception {
        testWaitOrSleepWithTimeJump(true);
    }

    public static void testWaitWithTimeJump() throws Exception {
        testWaitOrSleepWithTimeJump(false);
    }

    public static void main(String args[]) throws Exception {
        testUntimedPark();
        testRelativeTimedPark();
        testAbsoluteTimedPark();
        if (ENABLE_TIME_JUMP_TESTS) {
            testSleepWithTimeJump();
            testWaitWithTimeJump();
        }
    }
}
