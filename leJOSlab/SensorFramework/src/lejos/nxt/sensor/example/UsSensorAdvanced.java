package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.LcUltrasonic;
import lejos.util.Delay;

/**
 * This example shows how to use the US sensor to detect objects to the front left of the robot.
 * The US sensor is assumed to be mounted horizontally in a 45 degree angle to the left. <br>
 * The example shows 
 * how to convert scalar data from a sensor into a 3 dimensional space, 
 * how to correct for sensor misalignment on the robot,
 * the benefits of working with a vector package for use with spatial data.
 * 
 * @author Aswin
 *
 */
public class UsSensorAdvanced {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UsSensorAdvanced test=new UsSensorAdvanced();
	}

	public UsSensorAdvanced() {
		float[] obstacle=new float[3];
		
		// instantiate US sensor
		LcUltrasonic sensor=new LcUltrasonic(SensorPort.S1);

		// Filter out incidental out of range values using a statistics filter
		StatisticsFilter range=new StatisticsFilter(sensor,StatisticsFilter.MEDIAN,5);
		
		// buffer the sample, so that it can be used in multiple data streams
		AutoSampler bufferedRange=new AutoSampler(range,sensor.getSampleRate());
		
		// convert range to the position of the detected obstacle (relative to robot);
		Align toPosition=new Align(bufferedRange);
		// the sensor is pointing 45 degrees to the left instead of straight ahead
		toPosition.addRotation("Z", 45);
		
		// Show distance to obstacle;
		while (!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Distance",0,0);
			if (bufferedRange.fetchSample()==255){
				LCD.drawString("OutOfRange", 0, 1);
			}
			else {
				LCD.drawString("range: "+bufferedRange.fetchSample(), 0, 1);
				toPosition.fetchSample(obstacle,0);
				LCD.drawString("to front: "+obstacle[0], 0, 2);
				LCD.drawString("to left: "+obstacle[1], 0, 3);
			}
			Delay.msDelay(100);
		}

	}
	
}
