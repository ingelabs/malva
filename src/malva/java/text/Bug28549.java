package malva.java.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 28549: SimpleDateFormat with "zzz" pattern produces long timezone
 * names or GMT-offset strings instead of standard abbreviations.
 */
public class Bug28549 extends TestCase {
  private static void testTimezoneAbbreviation(String tzId, String expectedStd, String expectedDst) {
    TimeZone tz = TimeZone.getTimeZone(tzId);
    SimpleDateFormat sdf = new SimpleDateFormat("zzz", Locale.US);
    sdf.setTimeZone(tz);

    // Winter date (standard time) -- Jan 15 2007
    Date winter = new Date(107, 0, 15, 12, 0, 0);
    String winterResult = sdf.format(winter);
    assertEquals(expectedStd, winterResult);

    if (expectedDst != null) {
      // Summer date (daylight time) -- Jul 15 2007
      Date summer = new Date(107, 6, 15, 12, 0, 0);
      String summerResult = sdf.format(summer);
      assertEquals(expectedDst, summerResult);
    }
  }

  public static void testNorthAmerican() {
    testTimezoneAbbreviation("America/New_York", "EST", "EDT");
    testTimezoneAbbreviation("America/Chicago", "CST", "CDT");
    testTimezoneAbbreviation("America/Los_Angeles", "PST", "PDT");
  }

  public static void testEuropean() {
    testTimezoneAbbreviation("Europe/London", "GMT", "BST");
    testTimezoneAbbreviation("Europe/Paris", "CET", "CEST");
  }

  public static void testGMT() {
    testTimezoneAbbreviation("GMT", "GMT", null);
  }

  public static void main(String[] args) {
    testNorthAmerican();
    testEuropean();
    testGMT();
  }
}
