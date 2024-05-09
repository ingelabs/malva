package malva.java.nio.channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SocketChannel;

import malva.TestCase;

public class SocketChannelTest extends TestCase {
  private static InetSocketAddress CONN_ADDR;
  static {
    try {
      CONN_ADDR = new InetSocketAddress(InetAddress.getLocalHost(), 8484);
    } catch (IOException ioe) {
      throw new RuntimeException();
    }
  }

  private static void testLifecycle() throws Exception {
    ServerSocket ss = new ServerSocket(8484);
    SocketChannel ch = SocketChannel.open();
    ch.configureBlocking(true);
    assertTrue(ch.isOpen());
    assertFalse(ch.isConnected());
    assertFalse(ch.isConnectionPending());
    ch.connect(CONN_ADDR);
    assertTrue(ch.isOpen());
    assertTrue(ch.isConnected());
    assertFalse(ch.isConnectionPending());
    ss.close();
    assertTrue(ch.isOpen());
    assertTrue(ch.isConnected());
    assertFalse(ch.isConnectionPending());
    ch.close();
    assertFalse(ch.isOpen());
    assertFalse(ch.isConnected());
    assertFalse(ch.isConnectionPending());
  }

  public static void main(String[] args) throws Exception {
    testLifecycle();
  }
}
