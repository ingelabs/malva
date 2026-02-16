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

  public static void main(String[] args) {
    testNegativeDayOfWeekInMonth();
  }
}
