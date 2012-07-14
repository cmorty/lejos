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
		Vector3f obstacle=new Vector3f();
		LcUltrasonic sensor=new LcUltrasonic(SensorPort.S1);

		// add statistics filter (median, N=5) to remove incidental out-of-range values 
		StatisticsFilter range=new StatisticsFilter(sensor);
		range.setStatistic(StatisticsFilter.MEDIAN);
		range.setSampleSize(5);
		
		// buffer value, so that it can be used in multiple data streams, and to always have fresh samples
		SensorDataBuffer bufferedRange=new SensorDataBuffer(range);
		bufferedRange.setRefreshRate(range.getMinimumFetchInterval());
		
		// convert range to position relative to sensor;
		ToVector rangeVector=new ToVector(bufferedRange);
		rangeVector.setAxis("X");
		
		// convert position to position relative to robot;
		Align toPosition=new Align(rangeVector);
		toPosition.addRotation("Z", 45);
		
		// Show distance to obstacle;
		while (!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Distance",0,0);
			if (bufferedRange.fetchSample()==255){
				LCD.drawString("OutOfRange", 0, 1);
			}
			else {
				LCD.drawString("to sensor: "+bufferedRange.fetchSample(), 0, 1);
				toPosition.fetchSample(obstacle);
				LCD.drawString("to front: "+obstacle.x, 0, 2);
				LCD.drawString("to left: "+obstacle.y, 0, 3);
			}
			Delay.msDelay(100);
		}

	}
	
}
