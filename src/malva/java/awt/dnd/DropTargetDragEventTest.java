package malva.java.awt.dnd;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;

import malva.TestCase;

public class DropTargetDragEventTest extends TestCase {

  public static void testConstructorActions() {
    final DropTargetContext dtc = new DropTarget().getDropTargetContext();
    final Point p = new Point(1, 1);

    // Valid drop actions and source action masks are accepted
    new DropTargetDragEvent(dtc, p, DnDConstants.ACTION_NONE, DnDConstants.ACTION_NONE);
    new DropTargetDragEvent(dtc, p, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY);
    new DropTargetDragEvent(dtc, p, DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY_OR_MOVE);
    new DropTargetDragEvent(dtc, p, DnDConstants.ACTION_LINK, DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);

    // The drop action must be a single DnDConstants action
    assertThrows(new Block() {
      @Override public void run() {
        new DropTargetDragEvent(dtc, p, 0x100, DnDConstants.ACTION_COPY);
      }
    }, IllegalArgumentException.class);

    // The source actions must not have bits outside the DnDConstants mask
    assertThrows(new Block() {
      @Override public void run() {
        new DropTargetDragEvent(dtc, p, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY | 0x100);
      }
    }, IllegalArgumentException.class);
  }

  public static void testGetActions() {
    // getDropAction() and getSourceActions() return the actions the
    // event was created with
    DropTargetContext dtc = new DropTarget().getDropTargetContext();
    DropTargetDragEvent e = new DropTargetDragEvent(dtc, new Point(1, 1),
        DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY_OR_MOVE);
    assertEquals(DnDConstants.ACTION_MOVE, e.getDropAction());
    assertEquals(DnDConstants.ACTION_COPY_OR_MOVE, e.getSourceActions());
  }

  public static void main(String[] args) {
    // The DropTarget constructor throws HeadlessException when AWT is
    // headless; no display or toolkit is needed otherwise.
    if (GraphicsEnvironment.isHeadless()) {
      logSkip("DropTargetDragEventTest: AWT is headless");
      return;
    }

    testConstructorActions();
    testGetActions();
  }
}
