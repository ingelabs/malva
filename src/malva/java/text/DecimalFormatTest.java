package malva.java.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.NumberFormat.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import malva.TestCase;

public class DecimalFormatTest extends TestCase {
  public static void testFormat() {
    // Test localization
    Locale.setDefault(new Locale("es", "ES"));
    DecimalFormat df = new DecimalFormat("0.0 unit");
    assertEquals("4,3 unit", df.format(4.3));
    assertEquals("-4,3 unit", df.format(-4.3));

    Locale.setDefault(Locale.US);
    df = new DecimalFormat("0.0 unit");
    assertEquals("4.3 unit", df.format(4.3));
    assertEquals("-4.3 unit", df.format(-4.3));

    // format(long|double)
    df.applyPattern("0.##");
    assertEquals("0", df.format(0d));
    assertEquals("0.12", df.format(0.12));
    assertEquals("12.34", df.format(12.34));
    assertEquals("-12.34", df.format(-12.34));
    assertEquals("123.4", df.format(123.40));
    assertEquals("0", df.format(0));
    assertEquals("1234", df.format(1234));
    assertEquals("-1234", df.format(-1234));

    df.applyPattern("#,##0.###");
    assertEquals("1,234", df.format(1234d));
    assertEquals("1,234.56", df.format(1234.560));
    assertEquals("-1,234.567", df.format(-1234.567));
    assertEquals("-1,234.568", df.format(-1234.5678));
    assertEquals("1,234,567", df.format(1234567));
    assertEquals("-1,234", df.format(-1234));

    // format(Object)
    assertEquals("1,234,567,890", df.format(BigInteger.valueOf(1234567890)));
    assertEquals("0", df.format(BigDecimal.ZERO));
    assertEquals("12,345", df.format(Long.valueOf("12345")));
    assertEquals("1,234.568", df.format(Double.valueOf("1234.5678")));
    assertEquals("1.2", df.format(Float.valueOf("001.2")));
    assertEquals("-1", df.format(Byte.valueOf((byte) 255)));
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
    Set<Attribute> attrs;
    attrs = formatAndGetAttrs(df, 0);
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.INTEGER)));

    attrs = formatAndGetAttrs(df, Double.valueOf(1.23));
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, Double.valueOf(-1.23));
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.SIGN, Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, new BigDecimal(Math.PI));
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.INTEGER, Field.DECIMAL_SEPARATOR, Field.FRACTION)));

    attrs = formatAndGetAttrs(df, new BigDecimal("123"));
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.INTEGER)));

    attrs = formatAndGetAttrs(df, Long.valueOf("12345"));
    assertTrue(checkEqualAttrs(attrs, Arrays.asList(Field.INTEGER)));
  }

  private static boolean checkEqualAttrs(Set<Attribute> actual, Collection<? extends Attribute> expected) {
    return actual.size() == expected.size() && actual.containsAll(expected);
  }

  private static Set<Attribute> formatAndGetAttrs(DecimalFormat df, Object obj) {
    return df.formatToCharacterIterator(obj).getAllAttributeKeys();
  }

  public static void main(String[] args) {
    testFormat();
    testFormatToCharacterIterator();
  }
}
