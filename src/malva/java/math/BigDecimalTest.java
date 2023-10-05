package malva.java.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import malva.TestCase;

public class BigDecimalTest extends TestCase {
  public static void testStripTrailingZeros() {
    assertEquals(new BigDecimal("0.1234"), new BigDecimal("0.1234000").stripTrailingZeros());
    assertEquals(BigDecimal.ZERO, new BigDecimal("0e2").stripTrailingZeros());
    assertEquals(new BigDecimal("1e5"), new BigDecimal("100000").stripTrailingZeros());
  }

  public static void testToPlainString() {
    // Class constants
    assertEquals("0", BigDecimal.ZERO.toPlainString());
    assertEquals("1", BigDecimal.ONE.toPlainString());
    assertEquals("10", BigDecimal.TEN.toPlainString());

    // Positive values
    BigInteger positiveInt = new BigInteger("123");
    assertEquals("100", new BigDecimal(100).toPlainString());
    assertEquals("10000", new BigDecimal("10000").toPlainString());
    assertEquals("10", new BigDecimal(BigInteger.ONE, -1).toPlainString());
    assertEquals("0.0000000001", new BigDecimal(BigInteger.ONE, 10).toPlainString());
    assertEquals("123", new BigDecimal(positiveInt, 0).toPlainString());
    assertEquals("1230", new BigDecimal(positiveInt, -1).toPlainString());
    assertEquals("123000", new BigDecimal(positiveInt, -3).toPlainString());
    assertEquals("12.3", new BigDecimal(positiveInt, 1).toPlainString());
    assertEquals("0.00123", new BigDecimal(positiveInt, 5).toPlainString());
    assertEquals("0.0000000123", new BigDecimal(positiveInt, 10).toPlainString());
    
    // Negative values
    BigInteger negativeInt = new BigInteger("-123");
    assertEquals("-100", new BigDecimal(-100).toPlainString());
    assertEquals("-10000", new BigDecimal("-10000").toPlainString());
    assertEquals("-10", new BigDecimal(new BigInteger("-1"), -1).toPlainString());
    assertEquals("-0.0000000001", new BigDecimal(new BigInteger("-1"), 10).toPlainString());
    assertEquals("-123000", new BigDecimal(negativeInt, -3).toPlainString());
    assertEquals("-123", new BigDecimal(negativeInt, 0).toPlainString());
    assertEquals("-0.000000000123", new BigDecimal(negativeInt, 12).toPlainString());
  }

  public static void main(String[] args) {
    testStripTrailingZeros();
    testToPlainString();
  }
}
