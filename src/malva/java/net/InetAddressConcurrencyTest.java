package malva.java.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Arrays;
import malva.TestCase;

/**
 * Checks that forward and reverse name resolution gives the same
 * answers when used from several threads at once.
 */
public class InetAddressConcurrencyTest extends TestCase {
  private static final boolean ALLOW_EXTERNAL_DNS_TESTS = true;

  private static final int THREADS = 12;
  private static final int LOOKUPS = 50;
  private static final int EXT_LOOKUPS = 10;

  // Time allowed for all threads, in milliseconds.
  private static final long TIMEOUT = 60000;

  // Resolved through the static host table, so no DNS is needed
  private static final String HOST_NAME = "localhost";
  private static final byte[] HOST_ADDRESS = new byte[] {127, 0, 0, 1};

  // Resolved through DNS
  private static final String EXT_HOST_NAME = "dns.google";
  private static final byte[] EXT_HOST_ADDRESS = new byte[] {8, 8, 8, 8};

  private static String firstMismatch;
  private static int mismatchCount;

  private static synchronized void setMismatch(String s) {
    if (firstMismatch == null)
      firstMismatch = s;
    mismatchCount++;
  }

  private static synchronized int getMismatchCount() {
    return mismatchCount;
  }

  private static String sortedAddressesOf(String hostName) throws UnknownHostException {
    InetAddress[] addresses = InetAddress.getAllByName(hostName);
    String[] text = new String[addresses.length];
    for (int i = 0; i < addresses.length; i++)
      text[i] = addresses[i].getHostAddress();

    Arrays.sort(text);
    return Arrays.toString(text);
  }

  public static void testConcurrentLookups() throws UnknownHostException, InterruptedException {
    // Single thread lookups
    final String forward = sortedAddressesOf(HOST_NAME);
    final String reverse = InetAddress.getByAddress(HOST_ADDRESS).getCanonicalHostName();

    // getCanonicalHostName() falls back to the textual address when the
    // reverse lookup fails. Check that we have a valid result here; a numeric
    // baseline would let consistently failing reverse lookups pass.
    assertFalse(reverse.equals("127.0.0.1"));

    final String extForward;
    final String extReverse;
    if (ALLOW_EXTERNAL_DNS_TESTS) {
      extForward = sortedAddressesOf(EXT_HOST_NAME);
      extReverse = InetAddress.getByAddress(EXT_HOST_ADDRESS).getCanonicalHostName();
      assertFalse(extReverse.equals("8.8.8.8"));
    } else {
      extForward = null;
      extReverse = null;
      logSkip("InetAddressConcurrencyTest: external DNS tests are disabled");
    }

    // Concurrent threads must see the same results
    Thread[] threads = new Thread[THREADS];
    for (int i = 0; i < THREADS; i++) {
      threads[i] = new Thread() {
        @Override public void run() {
          try {
            for (int j = 0; j < LOOKUPS; j++) {
              String f = sortedAddressesOf(HOST_NAME);
              String r = InetAddress.getByAddress(HOST_ADDRESS).getCanonicalHostName();

              if (!forward.equals(f))
                setMismatch(HOST_NAME + " = " + f + ", expected " + forward);
              if (!reverse.equals(r))
                setMismatch("reverse of 127.0.0.1 = " + r + ", expected " + reverse);

              // Alternate the local lookups with an external host
              if (ALLOW_EXTERNAL_DNS_TESTS && j % (LOOKUPS / EXT_LOOKUPS) == 0) {
                f = sortedAddressesOf(EXT_HOST_NAME);
                r = InetAddress.getByAddress(EXT_HOST_ADDRESS).getCanonicalHostName();

                if (!extForward.equals(f))
                  setMismatch(EXT_HOST_NAME + " = " + f + ", expected " + extForward);
                if (!extReverse.equals(r))
                  setMismatch("reverse of 8.8.8.8 = " + r + ", expected " + extReverse);
              }
            }
          } catch (Throwable t) {
            setMismatch(t.toString());
          }
        }
      };
    }

    for (int i = 0; i < THREADS; i++) {
      // A thread that hangs must not keep the VM alive
      threads[i].setDaemon(true);
      threads[i].start();
    }

    long deadline = System.currentTimeMillis() + TIMEOUT;
    for (int i = 0; i < THREADS; i++) {
      long left = deadline - System.currentTimeMillis();
      if (left > 0)
        threads[i].join(left);
    }

    for (int i = 0; i < THREADS; i++) {
      if (threads[i].isAlive())
        fail("Thread " + i + " did not finish in " + TIMEOUT + " ms");
    }

    int checks =
      THREADS * 2 * (LOOKUPS + (ALLOW_EXTERNAL_DNS_TESTS ? EXT_LOOKUPS : 0));
    if (firstMismatch != null)
      fail(getMismatchCount() + " mismatches in " + checks
           + " checks, first mismatch: " + firstMismatch);
  }

  public static void main(String[] args) throws Exception {
    // Disable the resolver cache so that each lookup is done through
    // the name service.
    Security.setProperty("networkaddress.cache.ttl", "0");
    Security.setProperty("networkaddress.cache.negative.ttl", "0");

    testConcurrentLookups();
  }
}
