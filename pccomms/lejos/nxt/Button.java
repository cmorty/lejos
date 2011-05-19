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
	private int iCode;
	private NXTFrame frame = NXTFrame.getSingleton();
	
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
		synchronized(NXTFrame.anyButton) {
			for(;;) {
				try {
					NXTFrame.anyButton.wait();
				} catch (InterruptedException e) {
					// Ignore
				}
				System.out.println("Buttons:" + NXTFrame.getButtons());
				if ((NXTFrame.getButtons() & iCode) != 0) {
					// Call isPressed to claim the button press
					NXTFrame.isPressed(iCode);
					break;
				}
			
			}
		}
	}
	
	public static int waitForPress() {
		int buttons = 0;
		synchronized(NXTFrame.anyButton) { 
			try {
				NXTFrame.anyButton.wait();
				buttons = NXTFrame.getButtons();
				// Call isPressed to clear all buttons
				NXTFrame.isPressed(buttons);
			} catch (InterruptedException e) {
				// Ignore
			}
			System.out.println("Buttons: " + NXTFrame.getButtons());
			return buttons;
		}
	}
	
	public static int readButtons() {
		return NXTFrame.getButtons();
	}
}
