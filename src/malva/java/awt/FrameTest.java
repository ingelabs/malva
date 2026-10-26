package malva.java.awt;

import java.awt.Frame;
import java.awt.Toolkit;

import malva.AwtTestUtils;
import malva.TestCase;

public class FrameTest extends TestCase {

  public static void testSetState() {
    if (!Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.ICONIFIED)) {
      logSkip("FrameTest.testSetState: toolkit does not support ICONIFIED");
      return;
    }

    // The frame is never shown, so no peer is created and
    // all state changes are synchronous.
    Frame f = new Frame("FrameTest");
    try {
      f.setState(Frame.ICONIFIED);
      assertEquals(Frame.ICONIFIED, f.getState());

      f.setState(Frame.NORMAL);
      assertEquals(Frame.NORMAL, f.getState());
    } finally {
      f.dispose();
    }
  }

  public static void main(String[] args) {
    if (!AwtTestUtils.checkDisplay("FrameTest"))
      return;

    testSetState();
  }
}
