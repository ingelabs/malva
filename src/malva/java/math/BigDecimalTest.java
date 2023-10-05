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

  public static void testToEngineeringString() {
    // Class constants
    assertEquals("0", BigDecimal.ZERO.toEngineeringString());
    assertEquals("1", BigDecimal.ONE.toEngineeringString());
    assertEquals("10", BigDecimal.TEN.toEngineeringString());

    // Positive values
    BigInteger positiveInt = new BigInteger("123");
    assertEquals("100", new BigDecimal(100).toEngineeringString());
    assertEquals("10000", new BigDecimal("10000").toEngineeringString());
    assertEquals("10", new BigDecimal(BigInteger.ONE, -1).toEngineeringString());
    assertEquals("100E-12", new BigDecimal(BigInteger.ONE, 10).toEngineeringString());
    assertEquals("123", new BigDecimal(positiveInt, 0).toEngineeringString());
    assertEquals("1.23E+3", new BigDecimal(positiveInt, -1).toEngineeringString());
    assertEquals("123E+3", new BigDecimal(positiveInt, -3).toEngineeringString());
    assertEquals("12.3", new BigDecimal(positiveInt, 1).toEngineeringString());
    assertEquals("0.00123", new BigDecimal(positiveInt, 5).toEngineeringString());
    assertEquals("12.3E-9", new BigDecimal(positiveInt, 10).toEngineeringString());

    // Negative values
    BigInteger negativeInt = new BigInteger("-123");
    assertEquals("-100", new BigDecimal(-100).toEngineeringString());
    assertEquals("-10000", new BigDecimal("-10000").toEngineeringString());
    assertEquals("-10", new BigDecimal(new BigInteger("-1"), -1).toEngineeringString());
    assertEquals("-100E-12", new BigDecimal(new BigInteger("-1"), 10).toEngineeringString());
    assertEquals("-123E+3", new BigDecimal(negativeInt, -3).toEngineeringString());
    assertEquals("-123", new BigDecimal(negativeInt, 0).toEngineeringString());
    assertEquals("-123E-12", new BigDecimal(negativeInt, 12).toEngineeringString());
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

  public static void testToString() {
    // Class constants
    assertEquals("0", BigDecimal.ZERO.toString());
    assertEquals("1", BigDecimal.ONE.toString());
    assertEquals("10", BigDecimal.TEN.toString());

    // Positive values
    BigInteger positiveInt = new BigInteger("123");
    assertEquals("100", new BigDecimal(100).toString());
    assertEquals("10000", new BigDecimal("10000").toString());
    assertEquals("1E+1", new BigDecimal(BigInteger.ONE, -1).toString());
    assertEquals("1E-10", new BigDecimal(BigInteger.ONE, 10).toString());
    assertEquals("123", new BigDecimal(positiveInt, 0).toString());
    assertEquals("1.23E+3", new BigDecimal(positiveInt, -1).toString());
    assertEquals("1.23E+5", new BigDecimal(positiveInt, -3).toString());
    assertEquals("12.3", new BigDecimal(positiveInt, 1).toString());
    assertEquals("0.00123", new BigDecimal(positiveInt, 5).toString());
    assertEquals("1.23E-8", new BigDecimal(positiveInt, 10).toString());

    // Negative values
    BigInteger negativeInt = new BigInteger("-123");
    assertEquals("-100", new BigDecimal(-100).toString());
    assertEquals("-10000", new BigDecimal("-10000").toString());
    assertEquals("-1E+1", new BigDecimal(new BigInteger("-1"), -1).toString());
    assertEquals("-1E-10", new BigDecimal(new BigInteger("-1"), 10).toString());
    assertEquals("-1.23E+5", new BigDecimal(negativeInt, -3).toString());
    assertEquals("-123", new BigDecimal(negativeInt, 0).toString());
    assertEquals("-1.23E-10", new BigDecimal(negativeInt, 12).toString());
  }

  public static void main(String[] args) {
    testStripTrailingZeros();
    testToEngineeringString();
    testToPlainString();
    testToString();
  }
}
