package org.lejos.sample.tilttest;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.AccelMindSensor;

/**
 * Simple test of Acceleration (Tilt) sensors.
 * This should work with Mindsensors Acceleration sensor.
 * 
 * @author Sven Köhler
 *
 */
public class TiltTest {
	
	public static void main(String[] args) throws Exception {
		AccelMindSensor tilt = new AccelMindSensor(SensorPort.S1);
			
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Tilt", 0, 0);
			LCD.drawInt(tilt.getXTilt(), 6, 0, 1);
			LCD.drawInt(tilt.getYTilt(), 6, 0, 2);
			LCD.drawInt(tilt.getZTilt(), 6, 0, 3);
			LCD.drawString("Accel", 0, 4);
			LCD.drawInt(tilt.getXAccel(), 6, 0, 5);
			LCD.drawInt(tilt.getYAccel(), 6, 0, 6);
			LCD.drawInt(tilt.getZAccel(), 6, 0, 7);
			Thread.sleep(500);
		}
	}	
}
