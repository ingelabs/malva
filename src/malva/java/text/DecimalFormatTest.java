package malva.java.text;

import java.text.DecimalFormat;

import malva.TestCase;

public class DecimalFormatTest extends TestCase {
  public static void testFormat() {
    // Format with suffix
    DecimalFormat df = new DecimalFormat("0.0 unit");
    assertEquals("4.3 unit", df.format(4.3));
    assertEquals("-4.3 unit", df.format(-4.3));
  }

  public static void main(String[] args) {
    testFormat();
  }
}
