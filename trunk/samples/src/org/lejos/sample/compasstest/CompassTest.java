package org.lejos.sample.compasstest;
import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * Simple test of compass sensors.
 * 
 * Works with Mindsensors and HiTechnic compass sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
 public class CompassTest {
	
	public static void main(String[] args) throws Exception {
		CompassMindSensor compass = new CompassMindSensor(SensorPort.S1);
					
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Bearing:", 0, 0);
			LCD.drawInt((int) compass.getDegrees(), 0, 1);
			Thread.sleep(500);
		}
	}	
}
