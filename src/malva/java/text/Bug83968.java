package malva.java.text;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import malva.TestCase;

/**
 * Bug 83968: Trailing \u00AE (registered sign) characters in
 * DateFormatSymbols parsed strings. The FIELD_SEP delimiter leaks
 * into the last symbol of each group (PM, AD, December, etc.).
 */
public class Bug83968 extends TestCase {
  public static void testAmPmNoTrailingGarbage() {
    Locale.setDefault(Locale.ENGLISH);
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] ampms = dfs.getAmPmStrings();
    assertEquals("AM", ampms[0]);
    assertEquals("PM", ampms[1]);
  }

  public static void testErasNoTrailingGarbage() {
    Locale.setDefault(Locale.ENGLISH);
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] eras = dfs.getEras();
    assertEquals("BC", eras[0]);
    assertEquals("AD", eras[1]);
  }

  public static void testMonthsNoTrailingGarbage() {
    Locale.setDefault(Locale.ENGLISH);
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] months = dfs.getMonths();
    assertEquals("December", months[11]);
  }

  public static void testFormattedOutputClean() {
    Locale.setDefault(Locale.ENGLISH);
    Date date = new Date(118, 0, 22, 18, 20, 0);
    SimpleDateFormat df = new SimpleDateFormat("h:mm aa");
    String formatted = df.format(date);
    assertEquals("6:20 PM", formatted);
  }

  public static void main(String[] args) {
    testAmPmNoTrailingGarbage();
    testErasNoTrailingGarbage();
    testMonthsNoTrailingGarbage();
    testFormattedOutputClean();
  }
}
