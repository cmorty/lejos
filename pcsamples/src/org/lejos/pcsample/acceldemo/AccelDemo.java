package org.lejos.pcsample.acceldemo;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.SensorSelector;
import lejos.robotics.Accelerometer;

/**
 * Test of remote access to Acceleration (Tilt) sensor using
 * iCommand-like classes in pccomm.jar.
 * 
 * @author Lawrie Griffiths
 *
 */
public class AccelDemo {
	public static void main(String [] args) throws Exception {
		Accelerometer a = SensorSelector.createAccelerometer(SensorPort.S1);
		
		for(;;) {
			System.out.println("ACCEL: x=" + a.getXAccel() + "  y=" + a.getYAccel() + " z=" + a.getZAccel());
		}
	}
}