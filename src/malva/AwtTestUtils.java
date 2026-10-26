package malva;

import java.awt.AWTError;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public class AwtTestUtils {
  /**
   * Returns whether AWT has a usable display, logging a skip line with
   * the given prefix when it does not.  Initializes the AWT toolkit as
   * a side effect, so only call this from tests that use AWT anyway.
   */
  public static boolean checkDisplay(String prefix) {
    // DISPLAY must exist on X11 platforms; skip on macOS and Windows
    String os = System.getProperty("os.name");
    if (!os.startsWith("Mac") && !os.startsWith("Windows")) {
      String display = System.getenv("DISPLAY");
      if (display == null || display.length() == 0) {
        TestCase.logSkip(prefix + ": DISPLAY is not set");
        return false;
      }
    }
    if (GraphicsEnvironment.isHeadless()) {
      TestCase.logSkip(prefix + ": AWT is headless");
      return false;
    }
    try {
      Toolkit.getDefaultToolkit();
    } catch (AWTError e) {
      TestCase.logSkip(prefix + ": AWT toolkit not available (" + e.getMessage() + ")");
      return false;
    }
    return true;
  }
}
