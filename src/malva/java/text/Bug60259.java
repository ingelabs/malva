package malva.java.text;

import java.text.DateFormat;
import java.util.Locale;

import malva.TestCase;

/**
 * Bug 60259: DateFormat.getDateInstance() and getDateTimeInstance() hang
 * (infinite loop) for unsupported locales. The infinite loop is fixed,
 * but a fall-through bug remains in computeDefault(): case SHORT falls
 * through to default: throw IllegalArgumentException.
 */
public class Bug60259 extends TestCase {
  public static void testGetDateInstanceShortRoot() {
    // Should not throw IllegalArgumentException or loop forever
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ROOT);
    assertNotNull(df);
  }

  public static void testGetDateInstanceMediumRoot() {
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ROOT);
    assertNotNull(df);
  }

  public static void testGetDateInstanceLongRoot() {
    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ROOT);
    assertNotNull(df);
  }

  public static void testGetDateTimeInstanceShortRoot() {
    DateFormat df = DateFormat.getDateTimeInstance(
        DateFormat.SHORT, DateFormat.SHORT, Locale.ROOT);
    assertNotNull(df);
  }

  public static void testGetTimeInstanceShortRoot() {
    DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ROOT);
    assertNotNull(df);
  }

  public static void main(String[] args) {
    testGetDateInstanceShortRoot();
    testGetDateInstanceMediumRoot();
    testGetDateInstanceLongRoot();
    testGetDateTimeInstanceShortRoot();
    testGetTimeInstanceShortRoot();
  }
}
