package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 87590: SimpleDateFormat.parse() returns incorrect date when the
 * format pattern includes a weekday (EEE). The weekday overrides the
 * day-of-month during field resolution.
 */
public class Bug87590 extends TestCase {
  public static void testParseWithWeekday() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEE", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = sdf.parse("2010/10/27 10:00:00 Wed");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(2010, cal.get(Calendar.YEAR));
    assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
    assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
  }

  public static void testParseWithoutWeekday() throws ParseException {
    // Verify that without the weekday field, parsing is correct
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = sdf.parse("2010/10/27 10:00:00");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(2010, cal.get(Calendar.YEAR));
    assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
    assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
  }

  public static void main(String[] args) throws ParseException {
    testParseWithWeekday();
    testParseWithoutWeekday();
  }
}
