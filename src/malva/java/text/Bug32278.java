package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 32278: DateFormat.parse() fails for iCalendar-format date strings
 * like "19700329T020000" and "20070527T163830Z".
 */
public class Bug32278 extends TestCase {
  public static void testParseLocalDateTime() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = sdf.parse("19700329T020000");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(1970, cal.get(Calendar.YEAR));
    assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
    assertEquals(29, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(2, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(0, cal.get(Calendar.MINUTE));
    assertEquals(0, cal.get(Calendar.SECOND));
  }

  public static void testParseUTCDateTime() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = sdf.parse("20070527T163830Z");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(2007, cal.get(Calendar.YEAR));
    assertEquals(Calendar.MAY, cal.get(Calendar.MONTH));
    assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(16, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(38, cal.get(Calendar.MINUTE));
    assertEquals(30, cal.get(Calendar.SECOND));
  }

  public static void main(String[] args) throws ParseException {
    testParseLocalDateTime();
    testParseUTCDateTime();
  }
}
