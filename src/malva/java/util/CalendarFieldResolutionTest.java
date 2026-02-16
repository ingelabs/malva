package malva.java.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

public class CalendarFieldResolutionTest extends TestCase {
  private static Calendar newCalendar() {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    c.clear();
    return c;
  }

  private static void testInconsistentData() {
    /*
      If there is any conflict in calendar field values, Calendar gives
      priorities to calendar fields that have been set more recently. The
      following are the default combinations of the calendar fields. The most
      recent combination, as determined by the most recently set single field,
      will be used.

      For the date fields:

      YEAR + MONTH + DAY_OF_MONTH
      YEAR + MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
      YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
      YEAR + DAY_OF_YEAR
      YEAR + DAY_OF_WEEK + WEEK_OF_YEAR

      For the time of day fields:

      HOUR_OF_DAY
      AM_PM + HOUR
    */
    Calendar c;

    // YEAR + DAY_OF_WEEK + WEEK_OF_YEAR should be used, as WEEK_OF_YEAR is set
    // in the last place
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_MONTH, 1);
    c.set(Calendar.DAY_OF_YEAR, 10);
    c.set(Calendar.WEEK_OF_MONTH, 1);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    assertEquals(new Date(1516060800000L), c.getTime()); // 2018-01-16

    // YEAR + MONTH + WEEK_OF_MONTH + DAY_OF_WEEK should be used, as
    // WEEK_OF_MONTH is set in the last place
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.FEBRUARY);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    c.set(Calendar.WEEK_OF_MONTH, 2);
    assertEquals(new Date(1517875200000L), c.getTime()); // 2018-02-06

    // YEAR + MONTH + DAY_OF_MONTH should be used, as DAY_OF_MONTH is set in the
    // last place
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    c.set(Calendar.WEEK_OF_MONTH, 1);
    c.set(Calendar.DAY_OF_MONTH, 3);
    assertEquals(new Date(1514937600000L), c.getTime()); // 2018-01-03

    // YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK should be used.
    // DAY_OF_WEEK matches more than one pattern, so the field set before that
    // should be used to determine the pattern to use
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.WEEK_OF_MONTH, 1);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    assertEquals(new Date(1515456000000L), c.getTime()); // 2018-01-09

    // MONTH, as opposed to DAY_OF_WEEK, is ignored in inconsistency resolution,
    // so YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.WEEK_OF_MONTH, 1);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    c.set(Calendar.DAY_OF_MONTH, 5);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    assertEquals(new Date(1515456000000L), c.getTime()); // 2018-01-09

    // YEAR + DAY_OF_YEAR, because complete patterns have priority
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.DAY_OF_YEAR, 50);
    c.set(Calendar.WEEK_OF_YEAR, 3);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
    assertEquals(new Date(1518998400000L), c.getTime()); // 2018-02-19

    // HOUR_OF_DAY over HOUR + AM_PM
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.FEBRUARY);
    c.set(Calendar.DAY_OF_MONTH, 5);
    c.set(Calendar.HOUR, 7);
    c.set(Calendar.AM_PM, 1);
    c.set(Calendar.HOUR_OF_DAY, 3);
    assertEquals(new Date(1517799600000L), c.getTime()); // 2018-02-05T03:00Z

    // HOUR + AM_PM over HOUR_OF_DAY
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.FEBRUARY);
    c.set(Calendar.DAY_OF_MONTH, 5);
    c.set(Calendar.HOUR, 7);
    c.set(Calendar.HOUR_OF_DAY, 3);
    c.set(Calendar.AM_PM, 1);
    assertEquals(new Date(1517857200000L), c.getTime()); // 2018-02-05T19:00Z
  }

  private static void testIncompletePattern() {
    /*
      If there are any calendar fields whose values haven't been set in the
      selected field combination, Calendar uses their default values. The
      default value of each field may vary by concrete calendar systems. For
      example, in GregorianCalendar, the default of a field is the same as that
      of the start of the Epoch: i.e., YEAR = 1970, MONTH = JANUARY,
      DAY_OF_MONTH = 1, etc.
    */
    Calendar c;

    // Default YEAR=1970 when not set
    c = newCalendar();
    c.set(Calendar.MONTH, Calendar.OCTOBER);
    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
    assertEquals(new Date(25056000000L), c.getTime()); // 1970-10-18

    // WEEK_OF_YEAR with defaults
    c = newCalendar();
    c.set(Calendar.WEEK_OF_YEAR, 2);
    assertEquals(new Date(259200000L), c.getTime()); // 1970-01-04

    // DAY_OF_MONTH overflow rolls into next month
    c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.DATE, 35);
    assertEquals(new Date(1517702400000L), c.getTime()); // 2018-02-04

    // WEEK_OF_MONTH with first day of week set to Monday
    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.NOVEMBER);
    c.set(Calendar.WEEK_OF_MONTH, 3);
    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    assertEquals(new Date(1541980800000L), c.getTime()); // 2018-11-12

    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.NOVEMBER);
    c.set(Calendar.WEEK_OF_MONTH, 3);
    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    assertEquals(new Date(1542499200000L), c.getTime()); // 2018-11-18

    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.NOVEMBER);
    c.set(Calendar.WEEK_OF_MONTH, 3);
    c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
    assertEquals(new Date(1542067200000L), c.getTime()); // 2018-11-13

    // Large DAY_OF_YEAR overflows across years
    c = newCalendar();
    c.set(Calendar.YEAR, 2030);
    c.set(Calendar.DAY_OF_YEAR, 3650);
    assertEquals(new Date(2208729600000L), c.getTime()); // 2039-12-29

    // WEEK_OF_YEAR using Calendar.NOVEMBER (=10) as numeric value
    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.WEEK_OF_YEAR, Calendar.NOVEMBER);
    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    assertEquals(new Date(1520726400000L), c.getTime()); // 2018-03-11

    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.WEEK_OF_YEAR, Calendar.NOVEMBER);
    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    assertEquals(new Date(1520208000000L), c.getTime()); // 2018-03-05

    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.WEEK_OF_YEAR, Calendar.NOVEMBER);
    c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
    assertEquals(new Date(1520467200000L), c.getTime()); // 2018-03-08

    // Minimal days in first week
    c = newCalendar();
    c.setFirstDayOfWeek(Calendar.MONDAY);
    c.setMinimalDaysInFirstWeek(3);
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
    c.set(Calendar.WEEK_OF_MONTH, 1);
    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    assertEquals(new Date(1536451200000L), c.getTime()); // 2018-09-09
  }

  private static void testCloneFieldPriorities() {
    // Both pattern 1 (DAY_OF_MONTH) and pattern 2 (WEEK_OF_MONTH + DOW) are
    // complete.  DAY_OF_MONTH was set last, so pattern 1 should win.
    // After cloning, set() on the clone must not alter the original's
    // resolution.
    Calendar c = newCalendar();
    c.set(Calendar.YEAR, 2018);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.WEEK_OF_MONTH, 2);
    c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    c.set(Calendar.DAY_OF_MONTH, 15);

    Calendar clone = (Calendar) c.clone();
    clone.set(Calendar.WEEK_OF_MONTH, 3);
    clone.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

    // Original must still resolve via pattern 1 -> Jan 15
    assertEquals(new Date(1515974400000L), c.getTime()); // 2018-01-15
  }

  private static void testMultiArgSetPriorities() {
    // set(year, month, date) should record priorities just like single-field
    // set() calls.  Re-setting DAY_OF_WEEK after the multi-arg set should
    // make pattern 5 the winner because its determining field (DOW) is the
    // most recently set.
    Calendar c = newCalendar();
    c.set(Calendar.WEEK_OF_YEAR, 10);
    c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    c.set(2018, Calendar.JANUARY, 15);
    c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
    assertEquals(new Date(1520553600000L), c.getTime()); // 2018-03-09
  }

  private static void testHourAmPmPreservedAfterSet() {
    // When time is set via HOUR + AM_PM, then getTime() is called,
    // then set() is called on a date field, the time pattern resolution
    // must still pick HOUR + AM_PM on the next getTime().
    Calendar c;

    // Test 1: HOUR+AM_PM preserved after getTime() + set(MONTH)
    c = newCalendar();
    c.set(Calendar.YEAR, 2024);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR, 2);
    c.set(Calendar.AM_PM, Calendar.PM);
    c.set(Calendar.MINUTE, 30);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.getTime();
    c.set(Calendar.MONTH, Calendar.MARCH);
    assertEquals(new Date(1710513000000L), c.getTime()); // 2024-03-15 14:30 UTC

    // Test 2: HOUR+AM_PM preserved after getTime() + set(DAY_OF_MONTH)
    c = newCalendar();
    c.set(Calendar.YEAR, 2024);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR, 2);
    c.set(Calendar.AM_PM, Calendar.PM);
    c.set(Calendar.MINUTE, 30);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.getTime();
    c.set(Calendar.DAY_OF_MONTH, 20);
    assertEquals(new Date(1705761000000L), c.getTime()); // 2024-01-20 14:30 UTC

    // Test 3: HOUR+AM_PM preserved after getTime() + set(YEAR)
    c = newCalendar();
    c.set(Calendar.YEAR, 2024);
    c.set(Calendar.MONTH, Calendar.JANUARY);
    c.set(Calendar.DAY_OF_MONTH, 15);
    c.set(Calendar.HOUR, 2);
    c.set(Calendar.AM_PM, Calendar.PM);
    c.set(Calendar.MINUTE, 30);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.getTime();
    c.set(Calendar.YEAR, 2025);
    assertEquals(new Date(1736951400000L), c.getTime()); // 2025-01-15 14:30 UTC
  }

  public static void main(String[] args) {
    testInconsistentData();
    testIncompletePattern();
    testCloneFieldPriorities();
    testMultiArgSetPriorities();
    testHourAmPmPreservedAfterSet();
  }
}
