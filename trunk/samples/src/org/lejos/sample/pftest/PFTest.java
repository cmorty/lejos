package org.lejos.sample.pftest;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRLink;

/**
 * Test of HiTechnic IRLink driving PF motors.
 * 
 * @author Lawrie Griffiths
 *
 */public class PFTest {

	/**
	 * Test of PF Motors using the HiTechic IRLink
	 */
	public static void main(String[] args) {
		IRLink link = new IRLink(SensorPort.S1);
		LCD.drawString(link.getVersion(), 0, 0);
		LCD.drawString(link.getVendorID(), 0, 1);
		LCD.drawString(link.getProductID(), 0, 2);
		
		// Move Motor A forwards and B backwards on channel 4
		while(!Button.ESCAPE.isDown()) {
			link.sendPFComboDirect(3,IRLink.PF_FORWARD, IRLink.PF_BACKWARD);
		}
	}
}
