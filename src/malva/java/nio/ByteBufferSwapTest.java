package malva.java.nio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import malva.TestCase;

/**
 * Tests bulk transfers on direct buffers using the byte order opposite
 * to the platform's native order.
 */
public class ByteBufferSwapTest extends TestCase {

  private static final int N = 4096;

  private static ByteOrder swappedOrder() {
    return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
        ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
  }

  public static void testShort() {
    short[] a = new short[N];
    for (int i = 0; i < N; i++)
      a[i] = (short) (0x1234 + i * 31);
    ShortBuffer b = ByteBuffer.allocateDirect(2 * N).order(swappedOrder()).asShortBuffer();
    b.put(a);
    for (int i = 0; i < N; i++)
      assertEquals(a[i], b.get(i));
  }

  public static void testShortBulkGet() {
    short[] a = new short[N];
    ShortBuffer b = ByteBuffer.allocateDirect(2 * N).order(swappedOrder()).asShortBuffer();
    for (int i = 0; i < N; i++) {
      a[i] = (short) (0x5678 - i * 19);
      b.put(i, a[i]);
    }
    short[] result = new short[N];
    b.get(result);
    for (int i = 0; i < N; i++)
      assertEquals(a[i], result[i]);
  }

  // Use a view whose first element begins at an odd byte offset.
  public static void testUnalignedShort() {
    short[] a = new short[N];
    for (int i = 0; i < N; i++)
      a[i] = (short) (0x4321 - i * 17);
    ByteBuffer bb = ByteBuffer.allocateDirect(2 * N + 1);
    bb.position(1);
    ShortBuffer b = bb.slice().order(swappedOrder()).asShortBuffer();
    b.put(a);
    for (int i = 0; i < N; i++)
      assertEquals(a[i], b.get(i));
  }

  public static void testInt() {
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = 0x12345678 + i * 65537;
    IntBuffer b = ByteBuffer.allocateDirect(4 * N).order(swappedOrder()).asIntBuffer();
    b.put(a);
    for (int i = 0; i < N; i++)
      assertEquals(a[i], b.get(i));
  }

  public static void testLong() {
    long[] a = new long[N];
    for (int i = 0; i < N; i++)
      a[i] = 0x123456789abcdef0L + i * 0x100000001L;
    LongBuffer b = ByteBuffer.allocateDirect(8 * N).order(swappedOrder()).asLongBuffer();
    b.put(a);
    for (int i = 0; i < N; i++)
      assertEquals(a[i], b.get(i));
  }

  public static void main(String[] args) {
    testShort();
    testShortBulkGet();
    testUnalignedShort();
    testInt();
    testLong();
  }
}
