package malva.java.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import malva.TestCase;

public class URLConnectionTest extends TestCase {
  private static void testConnect() throws Exception {
    // Connect with unresolvable hostname
    URL url = new URL("http", "anunresolvablehost", 80, "index.html");
    final URLConnection connection = url.openConnection();
    connection.setUseCaches(false);
    assertThrows(new Block() {
      @Override
      public void run() throws Throwable {
        connection.connect();
      }
    }, IOException.class);
  }

  public static void main(String[] args) throws Exception {
    testConnect();
  }
}
