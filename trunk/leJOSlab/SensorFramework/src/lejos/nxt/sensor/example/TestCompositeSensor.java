package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.sensor.MiCruizcore;
import lejos.util.Delay;

public class TestCompositeSensor {

	/**
	 * Demo's the usage of composite sensors <P>
	 * The Cruiscore supports ACCELERATION, TURNRATE and AZIMUTH. 
	 * The example gets the data provider for Angle (direction) from the driver and displays the value in degrees;
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestCompositeSensor test=new TestCompositeSensor();
	}
	
	/**
	 * 
	 */
	public TestCompositeSensor() {
		MiCruizcore sensor=new MiCruizcore(SensorPort.S1);
		SampleProvider direction=sensor.getSampleProvider(Quantities.ANGLE);
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("direction: "+Math.toDegrees(direction.fetchSample()), 0, 0);
			Delay.msDelay(100);
		}
		
		
	}

}
