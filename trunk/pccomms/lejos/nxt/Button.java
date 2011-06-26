package lejos.nxt;

/**
 * PC emulation of the Button class
 * 
 * @author Lawrie Griffiths
 */
public class Button {
	public static final int ID_ENTER = 0x1;
	public static final int ID_LEFT = 0x2;
	public static final int ID_RIGHT = 0x4;
	public static final int ID_ESCAPE = 0x8;
	// Start NXT Frame
	static NXTFrame frame = NXTFrame.getSingleton();
	
	private int iCode;
	/**
	 * The Enter button.
	 */
	public static final Button ENTER = new Button (ID_ENTER);
	
	/**
	 *    * The Left button.
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

	private Button (int aCode)
	{
	  iCode = aCode;
	}
	  
	public boolean isPressed() {
		return NXTFrame.isPressed(iCode);
	}
	
	public void waitForPressAndRelease() {
		for(;;) { // Loop until a press of this button is detected
			int buttons = NXTFrame.waitForButtons();
			//System.out.println("Waitfor:" + buttons);
			if ((buttons & iCode) != 0) {
				// Call isPressed to claim the button press
				NXTFrame.isPressed(iCode);
				break;
			}	
		}
	}
	
	public static int waitForPress() {
		int buttons = NXTFrame.waitForButtons();
		// Call isPressed to clear all buttons
		NXTFrame.isPressed(buttons);
		//System.out.println("Buttons: " + NXTFrame.getButtons());
		return buttons;
	}
	
	public static int waitForPress(int timeout) {
		//TODO implement
		throw new UnsupportedOperationException();
	}
	
	public static int readButtons() {
		int buttons = NXTFrame.getButtons();
		// Call isPressed to clear all buttons
		NXTFrame.isPressed(buttons);
		return buttons;
	}
}
