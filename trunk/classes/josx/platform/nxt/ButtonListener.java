package josx.platform.nxt;

/**
 * Abstraction for receiver of button events.
 * @see josx.platform.rcx.Button#addButtonListener
 */
public interface ButtonListener
{
  public void buttonPressed (Button b);
  public void buttonReleased (Button b);
}
