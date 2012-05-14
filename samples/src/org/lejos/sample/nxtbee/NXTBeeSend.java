package org.lejos.sample.nxtbee;


import java.io.DataOutputStream;
import java.io.OutputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.addon.NXTBee;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

/**
 * Send data from the NXT to a receiver, be it a PC or another NXT, using the Dexter Industries NXTBee. 
 * The NXTBee must be connected on port 4.
 * 
 * Uses the NXTBee class.
 * 
 * @author Mark Crosbie, mark@mastincrosbie.com
 *
 */

public class NXTBeeSend {

	static NXTBee nb;
	
	/**
	 * Test sending data to the NXTBee for transmission to a remote host connected to another NXTBee
	 * or with an XStick/Sparkfun board attached.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Remove this if you do not want debug output
		RConsole.openUSB(3000);
		
		// My NXTBee runs at 115200 baud, and I want the internal data reading thread active
		//nb = new NXTBee(); // default 9600 baud
		nb = new NXTBee(115200);
		
		//int len;
		//byte[] b = {'H', 'e', 'l', 'l', 'o', '!', 0};
		
		Thread t = new Thread(nb);
		t.setDaemon(true);
		t.start();
		
		OutputStream os = nb.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		
		LCD.drawString("NXTBeeSend", 0, 1);
		
		try {
			int i = 0;
			while(Button.ENTER.isUp()){
				
				dos.writeBytes("Hello " + i + "\n");
				
				// Change to test sending other values and types
				//dos.writeInt(i);
				//dos.writeByte(i);
				//dos.write(b);
				i++;
				
				LCD.drawString("Iter: " + i, 0, 2);
				
				Delay.msDelay(500);
			}
		} catch(Exception e) {
		}
	
		RConsole.close();
	}

}

