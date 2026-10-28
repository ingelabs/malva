package malva.java.awt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;

import malva.AwtTestUtils;
import malva.TestCase;

public class ComponentTest extends TestCase {

  private static class ActionComponent extends Component {
    boolean processEventCalled;

    ActionComponent(boolean enableActionEvents) {
      if (enableActionEvents)
        enableEvents(AWTEvent.ACTION_EVENT_MASK);
    }

    @Override protected void processEvent(AWTEvent e) {
      processEventCalled = true;
      super.processEvent(e);
    }

    void dispatchActionEvent() {
      dispatchEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test"));
    }
  }

  public static void testDispatchActionEvent() {
    // dispatchEvent() calls processEvent() before returning for
    // 1.1-style events that have been enabled for the component.
    ActionComponent enabled = new ActionComponent(true);
    enabled.dispatchActionEvent();
    assertTrue(enabled.processEventCalled);

    // Events that have not been enabled are not delivered
    ActionComponent disabled = new ActionComponent(false);
    disabled.dispatchActionEvent();
    assertFalse(disabled.processEventCalled);
  }

  public static void main(String[] args) {
    // Dispatching an event initializes the default toolkit
    if (!AwtTestUtils.checkDisplay("ComponentTest"))
      return;

    testDispatchActionEvent();
  }
}
