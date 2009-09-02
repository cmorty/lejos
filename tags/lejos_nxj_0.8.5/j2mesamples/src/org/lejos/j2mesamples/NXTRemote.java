package org.lejos.j2mesamples;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * This MIDlet will attempt to connect to an NXT brick and allow
 * the user to control the robot through a mobile device.
 */
public class NXTRemote extends MIDlet implements CommandListener {
	private Display display;
	private RemoteForm remoteForm = new RemoteForm(this);
	
	//Commands
	Command cmdHelp = new Command("About", Command.HELP, 10);
	Command cmdQuit = new Command("Quit", Command.EXIT, 10);
	
	protected void destroyApp(boolean arg0) {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		remoteForm.addCommand(cmdHelp);
		remoteForm.addCommand(cmdQuit);
		remoteForm.setCommandListener(this);		
		display.setCurrent(remoteForm);
		remoteForm.connect();
	}
	
	public void commandAction (Command c, Displayable s  ) {
		if (c == cmdQuit){
			destroyApp(true);
			notifyDestroyed();
		} else if(c == cmdHelp){
			display.setCurrent(new Alert("leJOS Sample"), s);
		}
	}
}