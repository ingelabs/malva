package malva.java.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

  public static void main(String[] args) {
    testFormat();
  }
}
