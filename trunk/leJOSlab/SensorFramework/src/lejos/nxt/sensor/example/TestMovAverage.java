package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.*;
import lejos.util.Delay;

/**
 * This example shows how to get the average light condition over the last second.<br>
 * The average is based on a continuous sampling scheme that samples the lightsensor every 100 ms.
 * @author Aswin
 *
 */
public class TestMovAverage {
	public static void main(String[] args) {
		TestScalarSensor test=new TestScalarSensor();
	}
	
	public TestMovAverage() {
		// instantiate sensor driver
		SensorDataProvider sensor=new LightSensor(SensorPort.S1);
		
		// Instantiate statistics filter, configure it to return the mean of last 10 samples
		StatisticsFilter stat=new StatisticsFilter(sensor);
		stat.setStatistic(StatisticsFilter.MEAN);
		stat.setSampleSize(10);
		
		// Instantiate buffer, configure it to fetch a new sample every 100 msec
		SensorDataBuffer buffer=new SensorDataBuffer(stat);
		buffer.setRefreshRate(100);
	
		// Show average light coditions over the past second
		while (!Button.ESCAPE.isDown()) {
			LCD.drawString("Value: "+buffer.fetchData(), 0, 0);
			Delay.msDelay(buffer.getMinimumFetchInterval());
		}
	}

	

}
