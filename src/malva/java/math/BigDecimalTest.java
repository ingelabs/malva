package malva.java.math;

import java.math.BigDecimal;

import malva.TestCase;

public class BigDecimalTest extends TestCase {
  public static void testStripTrailingZeros() {
    assertEquals(new BigDecimal("0.1234"), new BigDecimal("0.1234000").stripTrailingZeros());
    assertEquals(BigDecimal.ZERO, new BigDecimal("0e2").stripTrailingZeros());
    assertEquals(new BigDecimal("1e5"), new BigDecimal("100000").stripTrailingZeros());
  }

  public static void testToPlainString() {
    // Positive scale
    BigDecimal bd = new BigDecimal(-10);
    assertEquals("-10", bd.toPlainString());

    // Negative scale
    bd = bd.stripTrailingZeros();
    assertEquals("-10", bd.toPlainString());
  }

  public static void main(String[] args) {
    testStripTrailingZeros();
    testToPlainString();
  }
}
