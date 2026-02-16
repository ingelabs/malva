package malva.java.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import malva.TestCase;

public class SimpleDateFormatTest extends TestCase {
  public static void testParse() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEE", Locale.US);
    assertEquals(new Date(1288166400000L), sdf.parse("2010/10/27 10:00:00 Wed"));
  }

  public static void testParseWithTimeZone() throws Exception {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    // No timezone -- should throw   
    assertThrows(new Block() {
      @Override
      public void run() throws Exception {
        sdf.parse("2009-04-14T20:18:10Z");
      }
    }, ParseException.class);
    // With timezone
    assertEquals(new Date(1239761890000L), sdf.parse("2009-04-14T20:18:10-0600"));
  }

  public static void main(String[] args) throws Exception {
    testParse();
    //testParseWithTimeZone();
  }
}