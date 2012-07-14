package skoehler.sensor.example;

import skoehler.sensor.device.*;
import skoehler.sensor.filter.*;
import skoehler.sensor.sampling.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

public class UsSensorAdvanced {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UsSensorAdvanced test=new UsSensorAdvanced();
	}

	public UsSensorAdvanced() {
		float[] dist=new float[1];
		float[] obstacle=new float[3];
		LcUltrasonic sensor=new LcUltrasonic(SensorPort.S1);

		// add statistics filter (median, N=5) to remove incidental out-of-range values 
		StatisticsFilter range=new StatisticsFilter(sensor);
		range.setStatistic(StatisticsFilter.MEDIAN);
		range.setSampleSize(5);
		
		// buffer value, so that it can be used in multiple data streams, and to always have fresh samples
		SampleBuffer bufferedRange=new SampleBuffer(range);
		bufferedRange.setRefreshRate(50);
		
		// convert to position relative to robot;
		Align toPosition=new Align(bufferedRange);
		toPosition.addRotation("Z", 45);
		
		// Show distance to obstacle;
		while (!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString("Distance",0,0);
			bufferedRange.fetchSample(dist,0);
			if (dist[0]==255){
				LCD.drawString("OutOfRange", 0, 1);
			}
			else {
				LCD.drawString("to sensor: "+dist[0], 0, 1);
				toPosition.fetchSample(obstacle,0);
				LCD.drawString("to front: "+obstacle[0], 0, 2);
				LCD.drawString("to left: "+obstacle[1], 0, 3);
			}
			Delay.msDelay(100);
		}

	}
	


}
