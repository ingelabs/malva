package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import malva.TestCase;

/**
 * Bug 39789: SimpleDateFormat.parse() succeeds when it should fail -- when
 * the input string is missing a required timezone field ("Z" pattern).
 *
 * Root cause: empty zone strings in DateFormatSymbols match via
 * String.startsWith("", index) which always returns true.
 */
public class Bug39789 extends TestCase {
  public static void testMissingTimezoneThrows() {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    // Input has no timezone, but format requires one -- should throw
    assertThrows(new Block() {
      public void run() throws Throwable {
        sdf.parse("2009-04-14T20:18:10");
      }
    }, ParseException.class);
  }

  public static void testPresentTimezoneSucceeds() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    Date d = sdf.parse("2009-04-14T20:18:10-0600");
    assertNotNull(d);
  }

  public static void main(String[] args) throws ParseException {
    testMissingTimezoneThrows();
    testPresentTimezoneSucceeds();
  }
}
