package malva.java.awt.dnd;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;

import malva.TestCase;

public class DropTargetDropEventTest extends TestCase {

  public static void testConstructorActions() {
    final DropTargetContext dtc = new DropTarget().getDropTargetContext();
    final Point p = new Point(1, 1);

    // Valid drop actions and source action masks are accepted
    new DropTargetDropEvent(dtc, p, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY_OR_MOVE);
    new DropTargetDropEvent(dtc, p, DnDConstants.ACTION_LINK, DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);

    // The drop action must be a single DnDConstants action
    assertThrows(new Block() {
      @Override public void run() {
        new DropTargetDropEvent(dtc, p, 0x100, DnDConstants.ACTION_COPY);
      }
    }, IllegalArgumentException.class);

    // The source actions must not have bits outside the DnDConstants mask
    assertThrows(new Block() {
      @Override public void run() {
        new DropTargetDropEvent(dtc, p, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY | 0x100);
      }
    }, IllegalArgumentException.class);

    assertThrows(new Block() {
      @Override public void run() {
        new DropTargetDropEvent(dtc, p, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY | 0x100, true);
      }
    }, IllegalArgumentException.class);
  }

  public static void testGetActions() {
    // getDropAction() and getSourceActions() return the actions the
    // event was created with
    DropTargetContext dtc = new DropTarget().getDropTargetContext();
    DropTargetDropEvent e = new DropTargetDropEvent(dtc, new Point(1, 1),
        DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY_OR_MOVE);
    assertEquals(DnDConstants.ACTION_MOVE, e.getDropAction());
    assertEquals(DnDConstants.ACTION_COPY_OR_MOVE, e.getSourceActions());
  }

  public static void testIsLocalTransfer() {
    DropTargetContext dtc = new DropTarget().getDropTargetContext();
    Point p = new Point(1, 1);
    int copy = DnDConstants.ACTION_COPY;

    DropTargetDropEvent localDefault = new DropTargetDropEvent(dtc, p, copy, copy);
    DropTargetDropEvent localFalse = new DropTargetDropEvent(dtc, p, copy, copy, false);
    DropTargetDropEvent localTrue = new DropTargetDropEvent(dtc, p, copy, copy, true);

    assertFalse(localDefault.isLocalTransfer());
    assertFalse(localFalse.isLocalTransfer());
    assertTrue(localTrue.isLocalTransfer());
  }

  public static void main(String[] args) {
    // The DropTarget constructor throws HeadlessException when AWT is
    // headless; no display or toolkit is needed otherwise.
    if (GraphicsEnvironment.isHeadless()) {
      logSkip("DropTargetDropEventTest: AWT is headless");
      return;
    }

    testConstructorActions();
    testGetActions();
    testIsLocalTransfer();
  }
}
