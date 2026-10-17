package malva.java.net;

import java.net.InetAddress;
import java.security.Security;
import malva.TestCase;

public class InetAddressTest extends TestCase {
  private static final boolean ALLOW_EXTERNAL_DNS_TESTS = true;

  // A host name that never resolves. ".invalid" is reserved by RFC
  // 2606, and the trailing dot makes the name absolute, so that the
  // resolver does not append the search domains to it.
  private static final String INVALID_HOST_NAME = "nonexistent.invalid.";

  public static void testEquals() throws Exception {
    assertTrue(InetAddress.getLocalHost().equals(InetAddress.getLocalHost()));
    assertFalse(InetAddress.getLocalHost().equals(null));
  }

  public static void testGetAddress() throws Exception {
    assertNotNull(InetAddress.getLocalHost().getAddress());
  }

  public static void testGetAllByName() throws Exception {
    assertThrows(new Block() {
      @Override public void run() throws Throwable {
        InetAddress.getAllByName(INVALID_HOST_NAME);
      }
    }, java.net.UnknownHostException.class);

    assertTrue(InetAddress.getAllByName(null)[0].isLoopbackAddress());
    assertTrue(InetAddress.getAllByName("")[0].isLoopbackAddress());

    // "localhost" should be in the static host table
    InetAddress[] addresses = InetAddress.getAllByName("localhost");
    assertTrue(addresses.length >= 1);
    for (int i = 0; i < addresses.length; i++)
      assertTrue(addresses[i].isLoopbackAddress());

    if (ALLOW_EXTERNAL_DNS_TESTS) {
      // External host name, expected to be resolved through DNS
      addresses = InetAddress.getAllByName("dns.google");
      assertTrue(addresses.length >= 2);

      boolean found8888 = false;
      boolean found8844 = false;
      for (int i = 0; i < addresses.length; i++) {
        String ha = addresses[i].getHostAddress();
        if (ha.equals("8.8.8.8"))
          found8888 = true;
        if (ha.equals("8.8.4.4"))
          found8844 = true;
      }
      assertTrue(found8888);
      assertTrue(found8844);
    } else {
      logSkip("InetAddressTest.testGetAllByName: external DNS tests are disabled");
    }
  }

  public static void testGetByAddress() throws Exception {
    assertTrue(InetAddress.getByAddress(InetAddress.getLocalHost().getAddress()).equals(InetAddress.getLocalHost()));
    InetAddress.getByAddress("anyhost", new byte[] {0, 0, 0, 0});

    assertThrows(new Block() {
      @Override public void run() throws Throwable {
        InetAddress.getByAddress(new byte[] {0, 0, 0});
      }
    }, java.net.UnknownHostException.class);
  }

  public static void testGetByName() throws Exception {
    assertThrows(new Block() {
      @Override public void run() throws Throwable {
        InetAddress.getByName(INVALID_HOST_NAME);
      }
    }, java.net.UnknownHostException.class);

    assertTrue(InetAddress.getByName(null).isLoopbackAddress());
    assertTrue(InetAddress.getByName("").isLoopbackAddress());

    assertTrue(InetAddress.getByName("localhost").isLoopbackAddress());
  }

  public static void testGetCanonicalHostName() throws Exception {
    // Just check that call does not fail
    InetAddress.getLocalHost().getCanonicalHostName();

    // Should be resolved through the static host table.  The textual
    // address is returned when the lookup finds no name, so a numeric
    // result means that the reverse lookup did not produce one.
    String name =
      InetAddress.getByAddress(new byte[] {127, 0, 0, 1}).getCanonicalHostName();
    assertNotNull(name);
    assertFalse(name.isEmpty());
    assertFalse(name.equals("127.0.0.1"));

    if (ALLOW_EXTERNAL_DNS_TESTS) {
      // External address, expected to be resolved through DNS
      assertEquals("dns.google",
        InetAddress.getByAddress(new byte[] {8, 8, 8, 8}).getCanonicalHostName());

      // 192.0.2.0/24 is reserved for documentation by RFC 5737 and is
      // expected to have no reverse mapping.  The textual address is
      // returned when the lookup finds no name.
      assertEquals("192.0.2.1",
        InetAddress.getByAddress(new byte[] {(byte) 192, 0, 2, 1}).getCanonicalHostName());
    } else {
      logSkip("InetAddressTest.testGetCanonicalHostName: external DNS tests are disabled");
    }
  }

  public static void testGetHostAddress() throws Exception {
    assertEquals("0.0.0.0", InetAddress.getByAddress(new byte[]{0, 0, 0, 0}).getHostAddress());
  }

  public static void testGetHostName() throws Exception {
    // If the InetAddress is created with a host name, the host name
    // is returned as is, and no reverse lookup is performed.
    assertEquals("testhost", InetAddress.getByAddress("testhost", new byte[] {0, 0, 0, 0}).getHostName());

    // No host name is supplied here, so the loopback address has to be
    // resolved.  getHostName() falls back to the textual address when
    // that fails, so a numeric result means the reverse lookup did not
    // produce a name.
    String name = InetAddress.getByAddress(new byte[] {127, 0, 0, 1}).getHostName();
    assertNotNull(name);
    assertFalse(name.isEmpty());
    assertFalse(name.equals("127.0.0.1"));
  }

  public static void testGetLocalHost() throws Exception {
    // Just test that the call does not fail
    InetAddress.getLocalHost();
  }

  public static void testHashCode() {
  }

  public static void testIsAnyLocalAddress() throws Exception {
    assertFalse(InetAddress.getLocalHost().isAnyLocalAddress());
  }

  public static void testIsLinkLocalAddress() throws Exception {
    assertFalse(InetAddress.getLocalHost().isLinkLocalAddress());
  }

  public static void testIsLoopback() throws Exception {
    assertTrue (InetAddress.getByName("127.0.0.1").isLoopbackAddress());
    assertFalse(InetAddress.getByName("192.168.0.1").isLoopbackAddress());
  }

  public static void testIsLoopbackAddress() throws Exception {
/*  FIXME
    assertFalse(InetAddress.getLocalHost().isLoopbackAddress());
*/
  }

  public static void testIsMCGlobal() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMCGlobal());
  }

  public static void testIsMCLinkLocal() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMCLinkLocal());
  }

  public static void testIsMCNodeLocal() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMCNodeLocal());
  }


  public static void testIsMCOrgLocal() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMCOrgLocal());
  }

  public static void testIsMCSiteLocal() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMCSiteLocal());
  }

  public static void testIsMulticastAddress() throws Exception {
    assertFalse(InetAddress.getLocalHost().isMulticastAddress());
  }

  public static void testIsReachable() throws Exception {
/* @since 1.5
    assertTrue(InetAddress.getLocalHost().isReachable(100));
*/
  }

  public static void testIsSiteLocalAddress() throws Exception {
/*  FIXME
    assertTrue(InetAddress.getLocalHost().isSiteLocalAddress());
*/
  }

  public void testToString() {
  }

  public static void main(String[] args) throws Exception {
    // Disable the resolver cache so that each lookup is done through
    // the name service.
    Security.setProperty("networkaddress.cache.ttl", "0");
    Security.setProperty("networkaddress.cache.negative.ttl", "0");

    testEquals();
    testGetAddress();
    testGetAllByName();
    testGetByAddress();
    testGetByName();
    testGetCanonicalHostName();
    testGetHostAddress();
    testGetHostName();
    testGetLocalHost();
    //testHashCode();
    testIsAnyLocalAddress();
    testIsLinkLocalAddress();
    testIsLoopback();
    testIsLoopbackAddress();
    testIsMCGlobal();
    testIsMCLinkLocal();
    testIsMCNodeLocal();
    testIsMCOrgLocal();
    testIsMCSiteLocal();
    testIsMulticastAddress();
    testIsReachable();
    testIsSiteLocalAddress();
    //testToString();
  }
}
