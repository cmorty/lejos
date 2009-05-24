import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * This MIDlet will attempt to connect to an NXT brick and allow
 * the user to control the robot through a mobile device.
 */
public class NXTRemote extends MIDlet {

	private Display display;
	private NXTLocator locator;
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		
		// !! One button for connect, one for exit?
		// !! NOTE: Need to add a command listener to the exit command.
		// Could simply implement commandlistener in this class. 
		//Command exit = new Command("Exit", Command.STOP, 1);
		// need to add exit to the app.
		
		locator = new NXTLocator(display);
		locator.findNXT();
	}

}