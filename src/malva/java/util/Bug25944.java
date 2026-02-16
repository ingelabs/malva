package malva.java.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 25944: Calendar.add(DAY_OF_MONTH, 1) across the autumn DST transition
 * produces wrong results -- dates repeat and times drift by one hour.
 *
 * Bug 29149 is a duplicate (same root cause with DAY_OF_YEAR).
 */
public class Bug25944 extends TestCase {
  public static void testAddDayAcrossAutumnDST() {
    TimeZone tz = TimeZone.getTimeZone("Europe/Oslo");
    Calendar cal = new GregorianCalendar(tz);
    cal.clear();
    cal.set(2006, Calendar.OCTOBER, 28, 0, 0, 0);

    // Day 0: Sat Oct 28 00:00
    assertEquals(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
    assertEquals(28, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));

    cal.add(Calendar.DAY_OF_MONTH, 1);
    // Day 1: Sun Oct 29 00:00
    assertEquals(29, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));

    cal.add(Calendar.DAY_OF_MONTH, 1);
    // Day 2: Mon Oct 30 00:00 (DST fall-back happened)
    assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));

    cal.add(Calendar.DAY_OF_MONTH, 1);
    // Day 3: Tue Oct 31 00:00
    assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
  }

  public static void testAddDayOfYearAcrossAutumnDST() {
    // Bug 29149: same issue with DAY_OF_YEAR
    TimeZone tz = TimeZone.getTimeZone("Europe/Oslo");
    Calendar cal = new GregorianCalendar(tz);
    cal.clear();
    cal.set(2005, Calendar.OCTOBER, 30, 0, 0, 0);

    assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));

    cal.add(Calendar.DAY_OF_YEAR, 1);
    assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));

    cal.add(Calendar.DAY_OF_YEAR, 1);
    assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(Calendar.NOVEMBER, cal.get(Calendar.MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
  }

  public static void testAddDayAcrossSpringDST() {
    // Also test spring forward (day is 23 hours)
    TimeZone tz = TimeZone.getTimeZone("Europe/Oslo");
    Calendar cal = new GregorianCalendar(tz);
    cal.clear();
    cal.set(2006, Calendar.MARCH, 25, 0, 0, 0);

    assertEquals(25, cal.get(Calendar.DAY_OF_MONTH));

    cal.add(Calendar.DAY_OF_MONTH, 1);
    // Mar 26: spring forward
    assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));

    cal.add(Calendar.DAY_OF_MONTH, 1);
    assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
  }

  public static void main(String[] args) {
    testAddDayAcrossAutumnDST();
    testAddDayOfYearAcrossAutumnDST();
    testAddDayAcrossSpringDST();
  }
}
