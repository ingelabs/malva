package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 64174: Parsing December 31 dates in non-lenient mode fails with
 * IllegalArgumentException("Illegal WEEK_OF_YEAR"). Same root cause as
 * Bug 29690.
 */
public class Bug64174 extends TestCase {
  public static void testParseDec31NonLenient() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdf.setLenient(false);

    Date d = sdf.parse("2014-12-31-22-00-00");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(2014, cal.get(Calendar.YEAR));
    assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
    assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(22, cal.get(Calendar.HOUR_OF_DAY));
  }

  public static void testParseDec31MultipleYears() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdf.setLenient(false);

    // Test several years to catch inconsistencies
    int[] years = {2001, 2004, 2007, 2010, 2013, 2014, 2017, 2020, 2023};
    for (int year : years) {
      Date d = sdf.parse(year + "-12-31");
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(d);
      assertEquals(year, cal.get(Calendar.YEAR));
      assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
      assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
    }
  }

  public static void main(String[] args) throws ParseException {
    testParseDec31NonLenient();
    testParseDec31MultipleYears();
  }
}
