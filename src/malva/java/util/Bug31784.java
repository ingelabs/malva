package malva.java.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import malva.TestCase;

/**
 * Bug 31784: GregorianCalendar.add(YEAR, 1) incorrectly changes the day
 * of month (e.g., July 5 becomes July 4).
 */
public class Bug31784 extends TestCase {
  public static void testAddYear() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.clear();
    cal.set(2000, Calendar.JULY, 5, 4, 3, 2);

    assertEquals(5, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(2000, cal.get(Calendar.YEAR));

    cal.add(Calendar.YEAR, 1);

    assertEquals(2001, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JULY, cal.get(Calendar.MONTH));
    assertEquals(5, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(4, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(3, cal.get(Calendar.MINUTE));
    assertEquals(2, cal.get(Calendar.SECOND));
  }

  public static void testAddYearMultiple() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.clear();
    cal.set(2000, Calendar.MARCH, 15);

    for (int i = 0; i < 10; i++) {
      cal.add(Calendar.YEAR, 1);
    }

    assertEquals(2010, cal.get(Calendar.YEAR));
    assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
    assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
  }

  public static void testAddMonth() {
    // Ensure add(MONTH) also preserves day of month
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
    cal.clear();
    cal.set(2000, Calendar.JANUARY, 15);

    cal.add(Calendar.MONTH, 3);

    assertEquals(2000, cal.get(Calendar.YEAR));
    assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
    assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
  }

  public static void main(String[] args) {
    testAddYear();
    testAddYearMultiple();
    testAddMonth();
  }
}
