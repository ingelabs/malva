package malva.java.util;

import java.util.Calendar;
import java.util.Locale;

import malva.TestCase;

/**
 * Bug 22785: Calendar.getMinimalDaysInFirstWeek() returns 1 for Locale.UK
 * but should return 4 per CLDR/ISO 8601 data.
 */
public class Bug22785 extends TestCase {
  public static void testMinimalDaysInFirstWeekUK() {
    Calendar cal = Calendar.getInstance(Locale.UK);
    assertEquals(4, cal.getMinimalDaysInFirstWeek());
  }

  public static void testMinimalDaysInFirstWeekFrance() {
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    assertEquals(4, cal.getMinimalDaysInFirstWeek());
  }

  public static void testMinimalDaysInFirstWeekGermany() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    assertEquals(4, cal.getMinimalDaysInFirstWeek());
  }

  public static void main(String[] args) {
    testMinimalDaysInFirstWeekUK();
    testMinimalDaysInFirstWeekFrance();
    testMinimalDaysInFirstWeekGermany();
  }
}
