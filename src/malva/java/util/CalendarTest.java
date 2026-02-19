package malva.java.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

public class CalendarTest extends TestCase {
  private static Calendar newCalendar() {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    c.clear();
    return c;
  }

  private static void testNegativeDayOfWeekInMonth() {
    /*
      DAY_OF_WEEK_IN_MONTH can be negative. A value of -1 means the last
      occurrence of that day-of-week in the month, -2 means the second-to-last,
      and so on.
    */
    Calendar c;

    // Last Friday of January 2018 -> 2018-01-26
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
    c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    assertEquals(new Date(1516924800000L), c.getTime()); // 2018-01-26

    // Last Wednesday of February 2018 -> 2018-02-28
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.FEBRUARY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
    c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
    assertEquals(new Date(1519776000000L), c.getTime()); // 2018-02-28

    // Last Saturday of March 2018 -> 2018-03-31
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.MARCH);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
    c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
    assertEquals(new Date(1522454400000L), c.getTime()); // 2018-03-31

    // Second-to-last Friday of January 2018 -> 2018-01-19
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, -2);
    c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    assertEquals(new Date(1516320000000L), c.getTime()); // 2018-01-19
  }

  private static void testExplicitDSTOffset() {
    /*
      When DST_OFFSET is explicitly set, it should be honored for the
      immediate computation.  However, after a getTime()/getTimeInMillis()
      cycle, the explicit value should revert to "computed" status.
      A subsequent set() of another field followed by getTime() should
      recompute DST_OFFSET from the timezone, not reuse the stale value.
    */
    TimeZone madrid = TimeZone.getTimeZone("Europe/Madrid");

    // Test A: explicit DST_OFFSET=0 is honored immediately.
    // July in Madrid is CEST (DST_OFFSET=3600000), but we force CET
    // (DST_OFFSET=0).  12:00 CET = 11:00 UTC.
    Calendar c = Calendar.getInstance(madrid, Locale.US);
    c.clear();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JULY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR_OF_DAY, 12);
    c.set(Calendar.DST_OFFSET, 0);
    assertEquals(1531652400000L, c.getTimeInMillis());

    // Test B: after getTime() + set(other field) + getTime(), DST_OFFSET
    // should be recomputed from the timezone (CEST = 3600000).
    // 12:00 CEST = 10:00 UTC on July 20.
    c = Calendar.getInstance(madrid, Locale.US);
    c.clear();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JULY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR_OF_DAY, 12);
    c.set(Calendar.DST_OFFSET, 0);
    c.getTimeInMillis();
    c.set(Calendar.DAY_OF_MONTH, 20);
    assertEquals(1532080800000L, c.getTimeInMillis());

    // Test C: explicitly re-setting DST_OFFSET after the cycle should
    // be honored again.  12:00 CET = 11:00 UTC on July 20.
    c = Calendar.getInstance(madrid, Locale.US);
    c.clear();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JULY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR_OF_DAY, 12);
    c.getTimeInMillis();
    c.set(Calendar.DAY_OF_MONTH, 20);
    c.set(Calendar.DST_OFFSET, 0);
    assertEquals(1532084400000L, c.getTimeInMillis());
  }

  public static void main(String[] args) {
    testNegativeDayOfWeekInMonth();
    testExplicitDSTOffset();
  }
}
