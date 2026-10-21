package malva.java.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import malva.TestCase;

/* The first host name lookup reads the networkaddress.cache.* security
 * properties, which initializes java.security.Security. If Security's own
 * configuration comes from a non-local URL, opening that URL resolves a
 * host name again, re-entering the half-initialized resolver machinery.
 * Check that the first lookup still works.
 */
public class ResolverCacheInitTest extends TestCase {
  public static void main(String[] args) throws UnknownHostException {
    /* Set the security URL to http://localhost:1/none :
     * - The URL must use a host name rather than an IP literal, so that
     *   opening it triggers the re-entrant lookup
     * - Port 1 is normally closed, so the connection attempt will be
     *   refused immediately, instead of waiting on a timeout
     *
     * Each class library needs a different property:
     * - GNU Classpath reads its provider files from gnu.classpath.home.url
     * - OpenJDK reads additional properties from java.security.properties,
     *   if security.overridePropertiesFile=true in the java.security file
     *   (which is the default)
     */
    System.setProperty("gnu.classpath.home.url", "http://localhost:1/none");
    System.setProperty("java.security.properties", "http://localhost:1/none");

    assertTrue(InetAddress.getByName("localhost").isLoopbackAddress());
  }
}
