package malva.java.text;

import java.text.DateFormatSymbols;
import java.util.Locale;

import malva.TestCase;

public class DateFormatSymbolsTest extends TestCase {
    public static void testSymbols() {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);

        String[] amPm = dfs.getAmPmStrings();
        assertEquals("AM", amPm[0]);
        assertEquals("PM", amPm[1]);

        String[] eras = dfs.getEras();
        assertEquals("BC", eras[0]);
        assertEquals("AD", eras[1]);
    }
}
