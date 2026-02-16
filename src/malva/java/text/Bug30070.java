package malva.java.text;

import java.text.DateFormatSymbols;
import java.util.Locale;

import malva.TestCase;

/**
 * Bug 30070: DateFormatSymbols.getMonths() 13th element (index 12) is null
 * instead of empty string.
 */
public class Bug30070 extends TestCase {
  public static void testMonthsArrayLength() {
    DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
    String[] months = dfs.getMonths();
    assertEquals(13, months.length);
  }

  public static void testThirteenthMonthNotNull() {
    DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
    String[] months = dfs.getMonths();
    assertNotNull(months[12]);
    assertEquals("", months[12]);
  }

  public static void testWeekdaysArrayLength() {
    DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
    String[] weekdays = dfs.getWeekdays();
    assertEquals(8, weekdays.length);
  }

  public static void main(String[] args) {
    testMonthsArrayLength();
    testThirteenthMonthNotNull();
    testWeekdaysArrayLength();
  }
}
