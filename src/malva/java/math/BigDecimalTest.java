package malva.java.math;

import java.math.BigDecimal;

import malva.TestCase;

public class BigDecimalTest extends TestCase {
  public static void testStrippingZeros() {
    // Array as { test, result }
    final BigDecimal[][] testCases = {
      {new BigDecimal("0.1234000"), new BigDecimal("0.1234")},
      {new BigDecimal("0e2"), BigDecimal.ZERO},
      {new BigDecimal("100000"), new BigDecimal("1e5")}
    };

    for (BigDecimal[] testCase : testCases) {
      assertEquals(testCase[1], testCase[0].stripTrailingZeros());
    }
  }

  public static void main(String[] args) {
    testStrippingZeros();
  }
}
