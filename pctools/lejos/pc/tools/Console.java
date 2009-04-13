package lejos.pc.tools;

import lejos.pc.comm.*;

/**
 * Console output monitor class.
 * This class provides access to console output from a NXT program. The program
 * simply writes strings using the NXT RConsole class. These are sent to the
 * PC via the USB (or Bluetooth) connection.
 *
 */ 
public class Console implements ConsoleViewerUI {
	public static void main(String[] args) throws Exception {
		(new Console()).run();
	}
	
	private void run() {
		ConsoleViewComms comm = new ConsoleViewComms(this, false);
		comm.connectTo(null, null, NXTCommFactory.ALL_PROTOCOLS);
	}

	public void append(String value) {
		System.out.print(value);
	}

	public void connectedTo(String name, String address) {
	}

	public void logMessage(String msg) {
		System.out.println(msg);		
	}

	public void setStatus(String msg) {
	}
}