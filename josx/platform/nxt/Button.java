package josx.platform.nxt;

/**
 * Abstraction for an NXT button.
 * Example:<p>
 * <code><pre>
 *    Button.ENTER.waitForPressAndRelease();
 *    Sound.playTone (1000, 1);
 * </pre></code>
 */
public class Button implements ListenerCaller
{
  /**
   * The Enter button.
   */
  public static final Button ENTER = new Button (0x01);
  /**
   * The Left button.
   */
  public static final Button LEFT = new Button (0x02);
  /**
   * The Right button.
   */
  public static final Button RIGHT = new Button (0x04);
  /**
   * The Escape button.
   */
  public static final Button ESCAPE = new Button (0x08);
  
	
  /**
   * Array containing ENTER, LEFT, RIGHT, ESCAPE, in that order.
   */
  public static final Button[] BUTTONS = { Button.ENTER, Button.LEFT, Button.RIGHT, Button.ESCAPE };
  
  private int iCode;
  private ButtonListener[] iListeners = new ButtonListener[4];
  private int iNumListeners;
  
  private Button (int aCode)
  {
    iCode = aCode;
  }

  /**
   * Return the ID of the button. One of 1, 2 or 4.
   */
  public final int getId()
  {
    return iCode;
  }
    
  /**
   * @return <code>true</code> if button is pressed, <code>false</code> otherwise.
   */
  public final boolean isPressed()
  {
    return (readButtons() & iCode) != 0;
  }

  static Poll poller = new Poll();

  /**
   * Wait until the button is released.
   */
  public final void waitForPressAndRelease() throws InterruptedException
  {
    do {
        poller.poll(iCode << Poll.BUTTON_MASK_SHIFT, 0);
    } while (isPressed());
  }

  /**
   * Adds a listener of button events. Each button can serve at most
   * 4 listeners.
   */
  public synchronized void addButtonListener (ButtonListener aListener)
  {
    if (iListeners == null)
    {
      iListeners = new ButtonListener[4];
    }
    iListeners[iNumListeners++] = aListener;
    ListenerThread.get().addButtonToMask(iCode, this);
  }

  /**
   * <i>Low-level API</i> that reads status of buttons.
   * @return An integer with possibly some bits set: 0x01 (ENTER button pressed)
   * 0x02 (LEFT button pressed), 0x04 (RIGHT button pressed), 0x08 (ESCAPE button pressed).
   * If all buttons 
   * are released, this method returns 0.
   */
  public static native int readButtons();

  public synchronized void callListeners()
  {
    for( int i = 0; i < iNumListeners; i++) {
      if( isPressed())
        iListeners[i].buttonPressed( this);
      else
        iListeners[i].buttonReleased( this);
    }
  }
}

