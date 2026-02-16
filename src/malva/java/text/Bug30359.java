package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 30359: SimpleDateFormat.parse() throws ParseException for valid
 * date strings.
 */
public class Bug30359 extends TestCase {
  public static void testParseYearOnly() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    Date d = sdf.parse("2006");

    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    assertEquals(2006, cal.get(Calendar.YEAR));
  }

  public static void testParseDateTime() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = sdf.parse("2007/04/13 18:10:20");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(2007, cal.get(Calendar.YEAR));
    assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
    assertEquals(13, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(18, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(10, cal.get(Calendar.MINUTE));
    assertEquals(20, cal.get(Calendar.SECOND));
  }

  public static void testParseDec31NonLenient() throws ParseException {
    // Year-end dates in non-lenient mode (linked to bug 29690/64174)
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdf.setLenient(false);
    Date d = sdf.parse("31/12/2007");

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(d);
    assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
    assertEquals(2007, cal.get(Calendar.YEAR));
  }

  public static void main(String[] args) throws ParseException {
    testParseYearOnly();
    testParseDateTime();
    testParseDec31NonLenient();
  }
}
