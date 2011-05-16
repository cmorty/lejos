package org.lejos.j2mesamples;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author juanantonio.breña
 *
 */
public class BTConnectTest extends MIDlet implements CommandListener{
	private Display display;
	private Form1 form;

	//Commands
	Command cmdHelp = new Command("About", Command.HELP, 10);
	Command cmdOk = new Command("Find", Command.OK, 10);
	Command cmdQuit = new Command("Quit", Command.EXIT, 10);

	/**
	 * 
	 */
	public BTConnectTest() {
	}

	protected void destroyApp(boolean arg0) {
	}

	protected void pauseApp() {
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		form = new Form1(this);
		form.addCommand(cmdHelp);
		form.addCommand(cmdOk);
		form.addCommand(cmdQuit);
		form.setCommandListener(this);

		display = Display.getDisplay(this);
		display.setCurrent(form);
	}

	public void commandAction (  Command c, Displayable s  ){
		if(c == cmdOk ){
			form.connect();
			form.sendDemoData();
		}else if(c == cmdQuit){
			destroyApp(true);
			notifyDestroyed();
		}else if(c == cmdHelp){
		}
	}
}
