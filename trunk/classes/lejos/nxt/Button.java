package lejos.nxt;

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

  private static final int ID_ALL = 0xf;
  private int iCode;
  private ButtonListener[] iListeners;
  private int iNumListeners;

  private static int [] clickFreq = new int[16];
  private static int clickVol;
  private static int clickLen;
  // initialize with state of buttons at program start to avoid false press events
  private static int curButtons = getButtons();
  
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

  private static final int PRESS_EVENT_SHIFT = 0;
  private static final int RELEASE_EVENT_SHIFT = 8;
  
  private Button (int aCode)
  {
    iCode = aCode;
  }

  /**
   * Return the ID of the button. One of 1, 2, 4 or 8.
   * @return the button Id
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
    NXTEvent event = NXTEvent.allocate(NXTEvent.BUTTONS, 0, 10);
    try{
        event.waitEvent(iCode << PRESS_EVENT_SHIFT, NXTEvent.WAIT_FOREVER);
        readButtons();
        event.waitEvent(iCode << RELEASE_EVENT_SHIFT, NXTEvent.WAIT_FOREVER);
        readButtons();
    }
    catch(InterruptedException e)
    {
        // TODO: need to work out how to do this properly
        // preserve state of interrupt flag
        Thread.currentThread().interrupt();
        return;
    }
    finally
    {
        event.free();
    }
  }

  /**
   * Wait for all of the buttons to have been released or for the end time.
   * Do not beep if new buttons are pressed.
   * @param event event to use for waiting
   * @param end end time used for timeout
   */
  private static void waitAllReleased(NXTEvent event, long end) throws InterruptedException
  {
    for(;;)
    {
      curButtons = getButtons();
      if (curButtons == 0) return;
      if (event.waitEvent(curButtons << RELEASE_EVENT_SHIFT, end - System.currentTimeMillis()) < 0) return;
    }
  }

	/**
	 * Waits for some button to be pressed. If a button is already pressed, it
	 * must be released and pressed again.
	 * 
	 * @param timeout The maximum number of milliseconds to wait
	 * @return the ID of the button that has been pressed or in rare cases a bitmask of button IDs,
	 *         0 if the given timeout is reached 
	 */
	public static int waitForAnyPress(int timeout) {
		long end = (timeout == 0 ? 0x7fffffffffffffffL : System.currentTimeMillis() + timeout);
		NXTEvent event = NXTEvent.allocate(NXTEvent.BUTTONS, 0, 10);
		try
		{
			int oldDown = curButtons;
			while (true)
			{
				long curTime = System.currentTimeMillis();
				if (curTime >= end)
					return 0;
				
				event.waitEvent((oldDown << RELEASE_EVENT_SHIFT) | ((ID_ALL ^ oldDown) << PRESS_EVENT_SHIFT),
						end - curTime);
				int newDown = readButtons();
				int pressed = newDown & (~oldDown);
				if (pressed != 0)
					return pressed;
				
				oldDown = newDown;
			}
		}
		catch(InterruptedException e)
		{
		    // TODO: Need to decide how to handle this properly
            // preserve state of interrupt flag
            Thread.currentThread().interrupt();
            return 0;
		}
		finally
		{
			event.free();
		}
	}

	/**
	 * Waits for some button to be pressed. If a button is already pressed, it
	 * must be released and pressed again.
	 * 
	 * @return the ID of the button that has been pressed or in rare cases a bitmask of button IDs
	 */
	public static int waitForAnyPress() {
		return waitForAnyPress(0);
	}
  
  /**
   * Adds a listener of button events. Each button can serve at most
   * 4 listeners.
   * @param aListener The new listener
   */
  public synchronized void addButtonListener (ButtonListener aListener)
  {
    if (iListeners == null)
    {
      iListeners = new ButtonListener[4];
      ListenerThread.get().addListener(NXTEvent.BUTTONS, iCode << (isPressed() ? RELEASE_EVENT_SHIFT : PRESS_EVENT_SHIFT), 10, this);
      
    }
    iListeners[iNumListeners++] = aListener;
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
	 * 
	 * @return An integer with possibly some bits set: 0x01 (ENTER button pressed)
	 * 0x02 (LEFT button pressed), 0x04 (RIGHT button pressed), 0x08 (ESCAPE button pressed).
	 * If all buttons are released, this method returns 0.
	 */
	public static synchronized int readButtons()
	{
		int newButtons = getButtons();
		int pressed = newButtons & (~curButtons);
		curButtons = newButtons;
		if (pressed != 0 && clickVol != 0)
		{
			int tone = clickFreq[pressed];
			if (tone != 0)
				Sound.playTone(tone, clickLen, -clickVol);
		}
		return newButtons;
	}

  /**
   * Call Button Listeners. Used by ListenerThread.
   * @return New event filter
   */
  public synchronized int callListeners()
  {
    boolean pressed = isPressed();
    for( int i = 0; i < iNumListeners; i++) {
      if(pressed)
        iListeners[i].buttonPressed( this);
      else
        iListeners[i].buttonReleased( this);
    }
    return iCode << (pressed ? RELEASE_EVENT_SHIFT : PRESS_EVENT_SHIFT);
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
   * @param key The key to obtain the tone for
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
      clickFreq[ID_ENTER] = 209 + 697;
      clickFreq[ID_LEFT] = 209 + 770;
      clickFreq[ID_RIGHT] = 209 + 852;
      clickFreq[ID_ESCAPE] = 209 + 941;
      clickFreq[ID_ENTER | ID_LEFT] = 633 + 770;
      clickFreq[ID_ENTER | ID_RIGHT] = 633 + 852;
      clickFreq[ID_ENTER | ID_ESCAPE] = 633 + 941;
  }
  
}
  


