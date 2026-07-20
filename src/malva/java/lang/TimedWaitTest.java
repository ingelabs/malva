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

    /** If running on CI, widen timing tolerances */
    private static final boolean ON_CI = System.getenv("CI") != null;

    private static final long NOMINAL_WAIT_MS = ON_CI ? 1000 : 500;
    private static final long WAIT_TOL_LOWER_MS = 50;
    private static final long WAIT_TOL_UPPER_MS = ON_CI ? 200 : 50;
    private static final long JOIN_EXTRA_MS = ON_CI ? 300 : 100;

    private static final long NOMINAL_JUMP_TESTS_WAIT_MS = 3000;
    private static final long JUMP_TEST_SETTLE_MS = 50;
    private static final long FORWARD_JUMP_WAKE_TIMEOUT_MS = 1000;
    private static final long PROCESS_WAIT_TIMEOUT_MS = 5000;

    private static final int TIMED_WAIT = 0;
    private static final int TIMED_SLEEP = 1;
    private static final int TIMED_PARK = 2;

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    private static long nowNanos() {
        return System.nanoTime();
    }

    private static void assertTimeInRangeMs(long time, long target, long lowerTol, long upperTol) {
        if (time < target - lowerTol || time > target + upperTol) {
            fail("Time out of range, expected: " + target + " -" + lowerTol + "/+" + upperTol + "ms, actual: " + time + "ms");
        }
    }

    private static void assertThreadFinishes(Thread t, long timeout) throws InterruptedException {
        t.join(timeout);
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread did not finish within timeout");
        }
    }

    private static void waitMonotonic(long ms) {
        long deadline = nowNanos() + ms * 1000000;
        while (nowNanos() < deadline)
            Thread.yield();
    }

    private static void assertThreadFinishesMonotonic(Thread t, long timeout) {
        long deadline = nowNanos() + timeout * 1000000;
        while (t.isAlive() && nowNanos() < deadline)
            Thread.yield();
        if (t.isAlive()) {
            t.interrupt();
            fail("Thread did not finish within timeout");
        }
    }

    private static int waitForProcessMonotonic(Process p, long timeout) {
        long deadline = nowNanos() + timeout * 1000000;
        while (nowNanos() < deadline) {
            try {
                return p.exitValue();
            } catch (IllegalThreadStateException e) {
                Thread.yield();
            }
        }
        p.destroy();
        fail("Process did not finish within timeout");
        return -1;
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
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_LOWER_MS, WAIT_TOL_UPPER_MS);
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
        assertThreadFinishes(t, NOMINAL_WAIT_MS + WAIT_TOL_UPPER_MS + JOIN_EXTRA_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_LOWER_MS, WAIT_TOL_UPPER_MS);
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
        assertThreadFinishes(t, NOMINAL_WAIT_MS + WAIT_TOL_UPPER_MS + JOIN_EXTRA_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_WAIT_MS, WAIT_TOL_LOWER_MS, WAIT_TOL_UPPER_MS);
    }

    private static void setSystemTimeSeconds(long epochSeconds) throws IOException {
        String[] cmd = new String[] { "date", "-s", "@" + epochSeconds };
        Process p = Runtime.getRuntime().exec(cmd);
        // Read until EOF to make sure command has run
        // Avoid calling p.waitFor since it uses Object.wait internally in GNU Classpath
        p.getErrorStream().close();
        InputStream in = p.getInputStream();
        while (in.read() != -1) {}
        in.close();

        if (waitForProcessMonotonic(p, PROCESS_WAIT_TIMEOUT_MS) != 0)
            fail("Unable to change system time");

        long actualSeconds = System.currentTimeMillis() / 1000;
        if (Math.abs(actualSeconds - epochSeconds) > 2)
            fail("System time did not change as requested");
    }

    private static String timedOperationName(int operation) {
        switch (operation) {
            case TIMED_WAIT:  return "wait";
            case TIMED_SLEEP: return "sleep";
            case TIMED_PARK:  return "parkNanos";
            default: throw new IllegalArgumentException();
        }
    }

    private static void testTimedOperationWithTimeJump(final int operation) throws Exception {
        final long[] elapsedHolder = new long[1];
        final Object readyLock = new Object();
        final boolean[] readyHolder = new boolean[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long t1 = nowNanos();

                synchronized (readyLock) {
                    readyHolder[0] = true;
                    readyLock.notify();
                }

                switch (operation) {
                    case TIMED_WAIT:
                        Object lock = TimedWaitTest.class;
                        synchronized (lock) {
                            try { lock.wait(NOMINAL_JUMP_TESTS_WAIT_MS); } catch (InterruptedException e) {}
                        }
                        break;
                    case TIMED_SLEEP:
                        try { Thread.sleep(NOMINAL_JUMP_TESTS_WAIT_MS); } catch (InterruptedException e) {}
                        break;
                    case TIMED_PARK:
                        LockSupport.parkNanos(1000000 * NOMINAL_JUMP_TESTS_WAIT_MS);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                elapsedHolder[0] = (nowNanos() - t1) / 1000000;
            }
        });
        t.start();

        synchronized (readyLock) {
            while (!readyHolder[0])
                readyLock.wait();
        }
        waitMonotonic(JUMP_TEST_SETTLE_MS);

        String operationName = timedOperationName(operation);
        long nowSeconds = System.currentTimeMillis() / 1000;

        // Move the system time forward, check that the thread didn't exit early
        setSystemTimeSeconds(nowSeconds + 60);
        waitMonotonic(JOIN_EXTRA_MS);
        if (!t.isAlive())
            fail(operationName + "() finished early when system time jumped forward");

        // Move the system time backward, check that the thread finishes in time
        setSystemTimeSeconds(nowSeconds - 60);
        assertThreadFinishesMonotonic(t, JOIN_EXTRA_MS + NOMINAL_JUMP_TESTS_WAIT_MS);
        assertTimeInRangeMs(elapsedHolder[0], NOMINAL_JUMP_TESTS_WAIT_MS, WAIT_TOL_LOWER_MS, WAIT_TOL_UPPER_MS);
    }

    public static void testSleepWithTimeJump() throws Exception {
        testTimedOperationWithTimeJump(TIMED_SLEEP);
    }

    public static void testWaitWithTimeJump() throws Exception {
        testTimedOperationWithTimeJump(TIMED_WAIT);
    }

    public static void testRelativeTimedParkWithTimeJump() throws Exception {
        testTimedOperationWithTimeJump(TIMED_PARK);
    }

    public static void testAbsoluteTimedParkWithTimeJump() throws Exception {
        final long[] deadlineHolder = new long[1];
        final Object readyLock = new Object();
        final boolean[] readyHolder = new boolean[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                long deadline = System.currentTimeMillis() + NOMINAL_JUMP_TESTS_WAIT_MS;
                deadlineHolder[0] = deadline;

                synchronized (readyLock) {
                    readyHolder[0] = true;
                    readyLock.notify();
                }

                LockSupport.parkUntil(deadline);
            }
        });
        t.start();

        synchronized (readyLock) {
            while (!readyHolder[0])
                readyLock.wait();
        }
        waitMonotonic(JUMP_TEST_SETTLE_MS);

        // Move clock forward so that the deadline is in the past,
        // and check that the thread wakes up "immediately"
        setSystemTimeSeconds(deadlineHolder[0] / 1000 + 60);
        assertThreadFinishesMonotonic(t, FORWARD_JUMP_WAKE_TIMEOUT_MS);
    }

    public static void main(String args[]) throws Exception {
        testUntimedPark();
        testRelativeTimedPark();
        testAbsoluteTimedPark();
        if (ENABLE_TIME_JUMP_TESTS) {
            testSleepWithTimeJump();
            testWaitWithTimeJump();
            testRelativeTimedParkWithTimeJump();
            testAbsoluteTimedParkWithTimeJump();
        }
    }
}
