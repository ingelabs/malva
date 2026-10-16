package malva.java.net;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import malva.TestCase;

public class NetworkInterfaceTest extends TestCase {
  public static void testGetName() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      assertNotNull(iface.getName());
    }
  }

  public static void testGetInetAddresses() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      for (InetAddress addr : getInetAddresses(iface)) {
        assertNotNull(addr);
      }
    }
  }

  public static void testGetInterfaceAddresses() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
        assertNotNull(addr);
      }
    }
  }

  public static void testGetSubInterfacesAndGetParent() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      for (NetworkInterface subIface : Collections.list(iface.getSubInterfaces())) {
        assertNotNull(subIface);
        assertEquals(subIface.getParent(), iface);
      }
    }
  }

  public static void testGetByName() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      assertEquals(iface, NetworkInterface.getByName(iface.getName()));
    }

    // An unknown name returns null rather than throwing
    assertNull(NetworkInterface.getByName("malvanosuchiface0"));

    assertThrows(new Block() {
      @Override public void run() throws Throwable {
        NetworkInterface.getByName(null);
      }
    }, NullPointerException.class);
  }

  public static void testGetByInetAddress() throws Exception {
    Map<InetAddress, Set<NetworkInterface>> map = mapInetAddressesToNetworkInterfaces(getNetworkInterfaces());

    // Multiple network interfaces can be bound to the same address. Which one in the set is returned by
    // getByInetAddress() is not defined.
    for (InetAddress addr : map.keySet()) {
      assertTrue(map.get(addr).contains(NetworkInterface.getByInetAddress(addr)));
    }

    // An address not bound to any interface returns null rather than
    // throwing. Pick one IP address from TEST-NET (RFC 5737) and verify
    // that it is not bound before the actual test.
    String[] candidates = { "192.0.2.1", "198.51.100.1", "203.0.113.1" };
    boolean sawUnbound = false;
    for (String candidate : candidates) {
      InetAddress addr = InetAddress.getByName(candidate);
      if (!map.containsKey(addr)) {
        assertNull(NetworkInterface.getByInetAddress(addr));
        sawUnbound = true;
        break;
      }
    }
    assertTrue(sawUnbound);

    assertThrows(new Block() {
      @Override public void run() throws Throwable {
        NetworkInterface.getByInetAddress(null);
      }
    }, NullPointerException.class);
  }


  public static void testGetMTU() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      assertTrue(iface.getMTU() > 0);
    }
  }

  public static void testIsLoopback() throws Exception {
    // The interface carrying 127.0.0.1 must be a loopback interface.
    NetworkInterface lo = getLoopbackInterface();
    assertNotNull(lo);
    assertTrue(lo.isLoopback());

    // Any interface bound to a loopback address must report isLoopback().
    // Not the other way around: on some systems the loopback interface also
    // carries non-loopback addresses, e.g. fe80::1 on macOS.
    for (NetworkInterface iface : getNetworkInterfaces()) {
      for (InetAddress addr : getInetAddresses(iface)) {
        if (addr.isLoopbackAddress()) {
          assertTrue(iface.isLoopback());
        }
      }
    }
  }

  public static void testIsUp() throws Exception {
    NetworkInterface lo = getLoopbackInterface();
    boolean sawLoopback = false;
    for (NetworkInterface iface : getNetworkInterfaces()) {
      boolean isUp = iface.isUp();
      if (iface.equals(lo)) {
        // The loopback interface is always up.
        assertTrue(isUp);
        sawLoopback = true;
      }
    }
    assertTrue(sawLoopback);
  }

  public static void testIsPointToPoint() throws Exception {
    NetworkInterface lo = getLoopbackInterface();
    boolean sawLoopback = false;
    for (NetworkInterface iface : getNetworkInterfaces()) {
      boolean isPointToPoint = iface.isPointToPoint();
      if (iface.equals(lo)) {
        // A loopback interface is not a point-to-point link.
        assertFalse(isPointToPoint);
        sawLoopback = true;
      }
    }
    assertTrue(sawLoopback);
  }

  public static void testSupportsMulticast() throws Exception {
    // No portable expected value, not even for loopback (Linux does not set
    // IFF_MULTICAST on lo): just check that the call does not fail.
    for (NetworkInterface iface : getNetworkInterfaces()) {
      iface.supportsMulticast();
    }
  }

  public static void testToString() throws Exception {
    for (NetworkInterface iface : getNetworkInterfaces()) {
      assertNotNull(iface.toString());
    }
  }

  private static List<NetworkInterface> getNetworkInterfaces() throws Exception {
    return Collections.list(NetworkInterface.getNetworkInterfaces());
  }

  private static NetworkInterface getLoopbackInterface() throws Exception {
    return NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
  }

  private static List<InetAddress> getInetAddresses(NetworkInterface iface) {
    return Collections.list(iface.getInetAddresses());
  }

  private static Map<InetAddress, Set<NetworkInterface>> mapInetAddressesToNetworkInterfaces(List<NetworkInterface> ifaces) {
    Map<InetAddress, Set<NetworkInterface>> map = new HashMap<InetAddress, Set<NetworkInterface>>();

    for (NetworkInterface iface : ifaces) {
      for (InetAddress addr : getInetAddresses(iface)) {
        Set<NetworkInterface> set = map.get(addr);
        if (set == null) {
          set = new HashSet<NetworkInterface>();
          map.put(addr, set);
        }
        set.add(iface);
      }
    }

    return map;
  }

  public static void main(String[] args) throws Exception {
    testGetName();
    testGetInetAddresses();
    testGetByName();
    testGetByInetAddress();
    testIsLoopback();
    testIsPointToPoint();
    testIsUp();
    testSupportsMulticast();
    testToString();
/* @since 1.6
    testGetInterfaceAddresses();
    testGetSubInterfacesAndGetParent();
    testGetMTU();
*/
  }
}
