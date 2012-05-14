package org.lejos.sample.nxtbee;


import lejos.nxt.*;

import java.io.*;

import lejos.nxt.addon.*;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

/**
 * Receive data from another NXT/PC using a Dexter Industries NXTBee on port 4.
 * 
 * @author mcrosbie
 *
 */
public class NXTBeeReceive {

	static NXTBee nb;
	
	/**
	 * Test sending data to the NXTBee for transmission to a remote host connected to another NXTBee
	 * or with an XStick/Sparkfun board attached.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RConsole.openUSB(3000);
		
		// My NXTBee runs at 115200 baud, and I want the internal data reading thread active
		//nb = new NXTBee();
		nb = new NXTBee(115200, true, true);
				
		Thread t = new Thread(nb);
		t.setDaemon(true);
		t.start();
		
		InputStream is = nb.getInputStream();		
		DataInputStream dis = new DataInputStream(is);

		byte[] b = new byte[20];
		
		Delay.msDelay(1000);

		LCD.clear();
		LCD.drawString("NXTBee Recv", 0, 0);
		

		try {
			while(Button.ENTER.isUp()){
				
				if(dis.available() > 0) {
					int bytesRead = dis.read(b);
					
					LCD.drawString("Read " + bytesRead + " bytes",0, 3);
					String s = new String(b);
					LCD.drawString(s, 0, 5);
					
					RConsole.println("Read " + bytesRead + " bytes");
					RConsole.println(s);
				}
				Delay.msDelay(1000);
			}
		} catch(Exception e) {
			RConsole.println("***** EXCEPTION " + e);
		}
						
		RConsole.close();
	}

}

