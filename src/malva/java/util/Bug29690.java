package malva.java.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import malva.TestCase;

/**
 * Bug 29690: GregorianCalendar in non-lenient mode throws
 * IllegalArgumentException("Illegal WEEK_OF_YEAR") for valid dates.
 *
 * Bug 64174 is the same root cause (December 31 in non-lenient mode).
 */
public class Bug29690 extends TestCase {
  public static void testNonLenientDec30_2001() {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setLenient(false);
    cal.set(Calendar.YEAR, 2001);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DATE, 30);
    cal.clear(Calendar.DST_OFFSET);
    cal.clear(Calendar.ZONE_OFFSET);

    // Should not throw IllegalArgumentException
    Date d = cal.getTime();
    assertNotNull(d);
  }

  public static void testNonLenientDec31_2001() {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setLenient(false);
    cal.set(Calendar.YEAR, 2001);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DATE, 31);
    cal.clear(Calendar.DST_OFFSET);
    cal.clear(Calendar.ZONE_OFFSET);

    Date d = cal.getTime();
    assertNotNull(d);
  }

  public static void testNonLenientDec31_2014() {
    // Bug 64174 test case
    GregorianCalendar cal = new GregorianCalendar();
    cal.setLenient(false);
    cal.set(Calendar.YEAR, 2014);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.DATE, 31);
    cal.clear(Calendar.DST_OFFSET);
    cal.clear(Calendar.ZONE_OFFSET);

    Date d = cal.getTime();
    assertNotNull(d);
  }

  public static void main(String[] args) {
    testNonLenientDec30_2001();
    testNonLenientDec31_2001();
    testNonLenientDec31_2014();
  }
}
