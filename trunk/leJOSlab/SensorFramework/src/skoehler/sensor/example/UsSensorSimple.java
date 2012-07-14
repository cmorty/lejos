package skoehler.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import skoehler.sensor.api.*;
import skoehler.sensor.device.*;
import skoehler.sensor.filter.StatisticsFilter;
import lejos.util.Delay;


/**
 * Tis example shows how to use median values to effectively remove incidental out-of-range values
 * from the US sensor
 * @author Aswin
 *
 */
public class UsSensorSimple {

	public static void main(String[] args) {
		UsSensorSimple test=new UsSensorSimple();
	}

	public UsSensorSimple() {
		float[] sample=new float[1];
		VectorData sensor=new LcUltrasonic(SensorPort.S1);
		//VectorData sensor=new LcUltrasonic(SensorPort.S1);
		StatisticsFilter range=new StatisticsFilter(sensor);
		range.setStatistic(StatisticsFilter.MEDIAN);
		range.setSampleSize(5);
		while (!Button.ESCAPE.isDown()) {
			range.fetchSample(sample, 0);
			LCD.clear();
			LCD.drawString("Range: "+sample[0], 0, 0);
			Delay.msDelay(50);
		}
	}
}
