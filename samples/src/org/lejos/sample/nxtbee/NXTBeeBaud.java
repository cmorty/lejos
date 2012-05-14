package org.lejos.sample.nxtbee;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.addon.NXTBee;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

/**
 * Change the baud rate of the Dexter Industries NXTBee attached to the NXT. 
 * By default the NXTBee is set to 9600 baud, but you can change this to a faster value.
 * 
 * @author Mark Crosbie, mark@mastincrosbie.com
 *
 */
public class NXTBeeBaud {

	static NXTBee nb;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		byte[] b = {'H', 'e', 'l', 'l', 'o', '!', 0};
		int i;
				
		LCD.drawString("NXTBee Baud", 0, 0);
		
		RConsole.openUSB(3000);
		
		//
		// CHANGE THIS if you have changed the baud rate of your NXTBee
		// 9600 baud is the default setting
		int currentBaudrate = 9600;
		int newBaudrate = 115200;
		
		// My NXTBee runs at 115200 baud by default.
		// We do not want the data thread running as we are going to use command mode
		// Turn on debug mode as we want ot see output on the RConsoleViewer
		nb = new NXTBee(currentBaudrate, false, true);

		LCD.drawString("Baud is " + currentBaudrate, 0, 1);
		
		LCD.drawString("Enter Cmd Mode", 0, 2);
		nb.enterCommandMode();
		
		LCD.drawString("Set baud " + newBaudrate, 0, 3);
		nb.setNXTBeeBaudrate(newBaudrate);
		
		LCD.drawString("Save config", 0, 4);
		nb.saveConfiguration();
		
		LCD.drawString("Exit Cmd Mode", 0, 5);
		nb.exitCommandMode();
				
		// Now create a new NXTBee object running at the new baud rate to test sending
		nb = new NXTBee(newBaudrate, false, true);
	
		LCD.drawString("Press ENTER", 0, 6);
		LCD.drawString("to test", 0, 7);
		Button.ENTER.waitForPressAndRelease();
		
		LCD.clear();
		LCD.drawString("NXTBee Baud", 0, 0);
		
		for(i=0; i < 10; i++) {
			LCD.drawString("Iter "+i, 0, 5);
			nb.write(b, 0, b.length);
			Delay.msDelay(1000);
		}
				
		RConsole.close();
	}

}
