package malva.java.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 32443: Calendar.set(HOUR_OF_DAY, 0) after setting a date via
 * set(y,m,d,h,m,s) also changes the date.
 */
public class Bug32443 extends TestCase {
  public static void testSetHourPreservesDate() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.set(2005, Calendar.JUNE, 23, 8, 4, 0);

    // Force computation
    cal.getTime();

    // Now change only the hour
    cal.set(Calendar.HOUR_OF_DAY, 0);

    assertEquals(2005, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(23, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(4, cal.get(Calendar.MINUTE));
  }

  public static void testSetMinutePreservesDate() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.set(2005, Calendar.JUNE, 23, 8, 4, 0);
    cal.getTime();

    cal.set(Calendar.MINUTE, 30);

    assertEquals(2005, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(23, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(30, cal.get(Calendar.MINUTE));
  }

  public static void testSetDayPreservesTime() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.set(2005, Calendar.JUNE, 23, 8, 4, 30);
    cal.getTime();

    cal.set(Calendar.DAY_OF_MONTH, 15);

    assertEquals(2005, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(4, cal.get(Calendar.MINUTE));
    assertEquals(30, cal.get(Calendar.SECOND));
  }

  public static void main(String[] args) {
    testSetHourPreservesDate();
    testSetMinutePreservesDate();
    testSetDayPreservesTime();
  }
}
