package malva.java.nio.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import malva.TestCase;

public class FileChannelTest extends TestCase {
  public static void testMap() throws IOException {
    final byte[] CONTENTS = { 1, 2, 3, 42 };
    
    File tempf = File.createTempFile("malva", ".bin");
    try {
      FileOutputStream out = new FileOutputStream(tempf);
      out.write(CONTENTS);
      out.close();

      FileInputStream in = new FileInputStream(tempf);
      MappedByteBuffer buffer = in.getChannel().map(
          FileChannel.MapMode.READ_ONLY, 0, CONTENTS.length);
      assertEquals(CONTENTS.length, buffer.capacity());
      for (int i = 0; i < CONTENTS.length; i++)
        assertEquals(CONTENTS[i], buffer.get(i));
      in.close();
    } finally {
      tempf.delete();
    }
  }

  public static void main(String[] args) throws Exception {
    testMap();
  }
}
