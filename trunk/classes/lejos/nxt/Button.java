package lejos.nxt;
import lejos.util.Delay;

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
  public static final int ID_ENTER = 0x1;
  public static final int ID_LEFT = 0x2;
  public static final int ID_RIGHT = 0x4;
  public static final int ID_ESCAPE = 0x8;
  
  private int iCode;
  private ButtonListener[] iListeners;
  private int iNumListeners;

  private static int [] clickFreq = new int[16];
  private static int clickVol;
  private static int clickLen;
  private static int curButtons = 0;
  
  public static final String VOL_SETTING = "lejos.keyclick_volume";
  
  /**
   * Static constructor to force loading of system settings.
   */
  static
  {
        loadSettings();
  }

  /**
   * The Enter button.
   */
  public static final Button ENTER = new Button (ID_ENTER);
  /**
   * The Left button.
   */
  public static final Button LEFT = new Button (ID_LEFT);
  /**
   * The Right button.
   */
  public static final Button RIGHT = new Button (ID_RIGHT);
  /**
   * The Escape button.
   */
  public static final Button ESCAPE = new Button (ID_ESCAPE);
  
	
  /**
   * Array containing ENTER, LEFT, RIGHT, ESCAPE, in that order.
   */
  public static final Button[] BUTTONS = { Button.ENTER, Button.LEFT, Button.RIGHT, Button.ESCAPE };
  
  
  private Button (int aCode)
  {
    iCode = aCode;
  }

  /**
   * Return the ID of the button. One of 1, 2, 4 or 8.
   */
  public final int getId()
  {
    return iCode;
  }
    
  /**
   * Check if the button is pressed.
   * @return <code>true</code> if button is pressed, <code>false</code> otherwise.
   */
  public final boolean isPressed()
  {
    return (readButtons() & iCode) != 0;
  }

  /**
   * Wait until the button is released.
   */
  public final void waitForPressAndRelease()
  {
    while(!isPressed())
        Delay.msDelay(50);
    while(isPressed())
        Thread.yield();
  }

  /**
   * wait for some button to be pressed (and released). 
   * @param timeout The number of milliseconds to wait.
   * @return the ID of that button, the same as readButtons(); 0 if timeout
   */ 
  public static int waitForPress(int timeout)
  {
     long end = (timeout == 0 ? 0x7fffffffffffffffL : System.currentTimeMillis() + timeout);
     int button = 0;
     // Wait for the button to be up
     while(0 < readButtons())
     {
         Delay.msDelay(50);
         if (System.currentTimeMillis() > end) return 0;
     }
     // Wait for it to be pressed
     while(0 == (button = readButtons()))
     {
        Delay.msDelay(50);
        if (System.currentTimeMillis() > end) return 0;
     }
     // and wait for it to be released
     while(0 < readButtons())
         Thread.yield();
     return button;             
  }

  /**
   * wait for some button to be pressed (and released). 
   * @return the ID of that button, the same as readButtons(); 
   */ 
  public static int waitForPress()
  {
      return waitForPress(0);
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
   * If all buttons are released, this method returns 0.
   */
  static native int getButtons();

  /**
   * <i>Low-level API</i> that reads status of buttons.
   * @return An integer with possibly some bits set: 0x01 (ENTER button pressed)
   * 0x02 (LEFT button pressed), 0x04 (RIGHT button pressed), 0x08 (ESCAPE button pressed).
   * If all buttons are released, this method returns 0.
   */
  public static int readButtons()
  {
      int newButtons = getButtons();
      if (newButtons != curButtons && clickVol != 0)
      {
          int tone = clickFreq[newButtons];
          if (tone != 0) Sound.playTone(tone, clickLen, -clickVol);
      }
      curButtons = newButtons;
      return newButtons;
  }

  /**
   * Call Button Listeners. Used by ListenerThread.
   */
  public synchronized void callListeners()
  {
    for( int i = 0; i < iNumListeners; i++) {
      if( isPressed())
        iListeners[i].buttonPressed( this);
      else
        iListeners[i].buttonReleased( this);
    }
  }
  
  /**
   * Set the volume used for key clicks
   * @param vol
   */
  public static void setKeyClickVolume(int vol)
  {
      clickVol = vol;
  }
  
  /**
   * Return the current key click volume.
   * @return current click volume
   */
  public static int getKeyClickVolume()
  {
      return clickVol;
  }
  
  /**
   * Set the len used for key clicks
   * @param len the click duration
   */
  public static void setKeyClickLength(int len)
  {
      clickLen = len;
  }
  
  /**
   * Return the current key click length.
   * @return key click duration
   */
  public static int getKeyClickLength()
  {
      return clickLen;
  }
  
  /**
   * Set the frequency used for a particular key. Setting this to 0 disables
   * the click. Note that key may also be a corded set of keys.
   * @param key the NXT key
   * @param freq the frequency
   */
  public static void setKeyClickTone(int key, int freq)
  {
      clickFreq[key] = freq;
  }
  
  /**
   * Return the click freq for a particular key.
   * @return key click duration
   */
  public static int getKeyClickTone(int key)
  {
      return clickFreq[key];
  }
  

  /**
   * Load the current system settings associated with this class. Called
   * automatically to initialize the class. May be called if it is required
   * to reload any settings.
  */
  public static void loadSettings()
  {
      clickVol = SystemSettings.getIntSetting(VOL_SETTING, 20);
      clickLen = 50;
      // setup default tones for the keys and enter+key chords
      clickFreq[1] = 209 + 697;
      clickFreq[2] = 209 + 770;
      clickFreq[4] = 209 + 852;
      clickFreq[8] = 209 + 941;
      clickFreq[1+2] = 633 + 770;
      clickFreq[1+4] = 633 + 852;
      clickFreq[1+8] = 633 + 941;
  }
  
}
  


