package malva.java.lang.reflect;

import java.lang.reflect.Array;
import malva.TestCase;

public class ArrayTest extends TestCase {
  private static void assertNewInstancePrimitive(Class<?> componentType,
                                                 Class<?> expectedType) {
    Object array = Array.newInstance(componentType, 3);

    assertEquals(expectedType, array.getClass());
    assertEquals(3, Array.getLength(array));
  }

  private static void assertNewInstancePrimitiveMultiDim(Class<?> componentType,
                                                         Class<?> expectedType) {
    Object array = Array.newInstance(componentType, new int[] { 2, 3 });

    assertEquals(expectedType, array.getClass());
    assertEquals(2, Array.getLength(array));
    assertEquals(3, Array.getLength(Array.get(array, 0)));
  }

  public static void testNewInstancePrimitive() {
    assertNewInstancePrimitive(Boolean.TYPE, boolean[].class);
    assertNewInstancePrimitive(Byte.TYPE, byte[].class);
    assertNewInstancePrimitive(Character.TYPE, char[].class);
    assertNewInstancePrimitive(Short.TYPE, short[].class);
    assertNewInstancePrimitive(Integer.TYPE, int[].class);
    assertNewInstancePrimitive(Float.TYPE, float[].class);
    assertNewInstancePrimitive(Long.TYPE, long[].class);
    assertNewInstancePrimitive(Double.TYPE, double[].class);
  }

  public static void testNewInstancePrimitiveMultiDim() {
    assertNewInstancePrimitiveMultiDim(Boolean.TYPE, boolean[][].class);
    assertNewInstancePrimitiveMultiDim(Byte.TYPE, byte[][].class);
    assertNewInstancePrimitiveMultiDim(Character.TYPE, char[][].class);
    assertNewInstancePrimitiveMultiDim(Short.TYPE, short[][].class);
    assertNewInstancePrimitiveMultiDim(Integer.TYPE, int[][].class);
    assertNewInstancePrimitiveMultiDim(Float.TYPE, float[][].class);
    assertNewInstancePrimitiveMultiDim(Long.TYPE, long[][].class);
    assertNewInstancePrimitiveMultiDim(Double.TYPE, double[][].class);
  }

  public static void main(String[] args) {
    testNewInstancePrimitive();
    testNewInstancePrimitiveMultiDim();
  }
}
