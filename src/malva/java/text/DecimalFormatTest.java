package malva.java.text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import malva.TestCase;

public class DecimalFormatTest extends TestCase {
  public static void testFormat() {
    // Format with suffix
    DecimalFormat df = new DecimalFormat("0.0 unit", new DecimalFormatSymbols(Locale.US));
    assertEquals("4.3 unit", df.format(4.3));
    assertEquals("-4.3 unit", df.format(-4.3));

    DecimalFormatSymbols spanishSymbols = new DecimalFormatSymbols(new Locale("es", "ES"));
    df.setDecimalFormatSymbols(spanishSymbols);
    assertEquals("4,3 unit", df.format(4.3));
    assertEquals("-4,3 unit", df.format(-4.3));
  }

  public static void testFormatToCharacterIterator() {
    final DecimalFormat df = new DecimalFormat("0.##");

    // Check first for throwables
    assertThrows(new Block() {
      @Override public void run() {
        df.formatToCharacterIterator(null);
      }
    }, NullPointerException.class);

    assertThrows(new Block() {
      @Override public void run() {
        df.formatToCharacterIterator(new Object());
      }
    }, IllegalArgumentException.class);

    // Attributes check
    Set<?> attrs;
    attrs = formatAndGetAttrs(df, 0);
    assertTrue(attrs.containsAll(Arrays.asList(Field.INTEGER)));

    attrs = formatAndGetAttrs(df, Double.valueOf(1.23));
    assertTrue(attrs.containsAll(Arrays.asList(Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, Double.valueOf(-1.23));
    assertTrue(attrs.containsAll(Arrays.asList(Field.SIGN, Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, new BigDecimal(Math.PI));
    assertTrue(attrs.containsAll(Arrays.asList(Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, new BigDecimal("123"));
    assertTrue(attrs.containsAll(Arrays.asList(Field.INTEGER)));

    attrs = formatAndGetAttrs(df, Long.valueOf("12345"));
    assertTrue(attrs.containsAll(Arrays.asList(Field.INTEGER)));
  }

  private static Set<?> formatAndGetAttrs(DecimalFormat df, Object obj) {
    return df.formatToCharacterIterator(obj).getAllAttributeKeys();
  }

  public static void main(String[] args) {
    testFormat();
    testFormatToCharacterIterator();
  }
}
