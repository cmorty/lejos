package org.lejos.sample.acceltest;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.SensorSelector;
import lejos.robotics.Accelerometer;

/**
 * Simple test of Acceleration sensors.
 * This should work with Mindsensors and HiTechnic Acceleration
 * sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public class AccelTest {
	
	public static void main(String[] args) throws Exception {
		Accelerometer accelerometer = SensorSelector.createAccelerometer(SensorPort.S1);
		
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Accel", 0, 0);
			LCD.drawInt(accelerometer.getXAccel(), 6, 0, 1);
			LCD.drawInt(accelerometer.getYAccel(), 6, 0, 2);
			LCD.drawInt(accelerometer.getZAccel(), 6, 0, 3);
			Thread.sleep(500);
		}
	}	
}
