package lejos.nxt.sensor.example;

import lejos.nxt.*;
import lejos.nxt.sensor.api.SampleProvider;
import lejos.nxt.sensor.filter.StatisticsFilter;
import lejos.nxt.sensor.sensor.LcUltrasonic;
import lejos.util.Delay;

public class UsSensorSimple {
	
	public static void main(String[] args) {
		
		// Define the sample processing chain
		SampleProvider sensor=new LcUltrasonic(SensorPort.S1);
		SampleProvider range=new StatisticsFilter(sensor,StatisticsFilter.MEDIAN,5);

		// Use the sample
		while (!Button.ESCAPE.isDown()) {
			System.out.println(range.fetchSample());
			Delay.msDelay(50);
		}	
	}
}
