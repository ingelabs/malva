package malva.java.lang;

import java.util.concurrent.locks.LockSupport;

import malva.TestCase;

public class ThreadParkTest extends TestCase {
    private static final long NOMINAL_WAIT_MS = 500;
    private static final long WAIT_TOL_MS = 50;
    private static final long JOIN_EXTRA_MS = 100;

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

    public static void main(String args[]) throws InterruptedException {
        testUntimedPark();
        testRelativeTimedPark();
        testAbsoluteTimedPark();
    }
}
