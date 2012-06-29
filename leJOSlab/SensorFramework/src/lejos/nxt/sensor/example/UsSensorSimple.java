package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.filter.StatisticsFilter;
import lejos.nxt.sensor.sensor.LcUltrasonic;
import lejos.util.Delay;

/**
 * Tis example shows how to use median values to effectively remove incidental out-of-range values
 * from the US sensor
 * @author Aswin
 *
 */
public class UsSensorSimple {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UsSensorSimple test=new UsSensorSimple();
	}

	public UsSensorSimple() {
		LcUltrasonic sensor=new LcUltrasonic(SensorPort.S1);
		StatisticsFilter range=new StatisticsFilter(sensor);
		range.setStatistic(StatisticsFilter.MEDIAN);
		range.setSampleSize(5);
		while (!Button.ESCAPE.isDown()) {
			LCD.drawString("Range: "+range.fetchData(), 0, 0);
			Delay.msDelay(range.getMinimumFetchInterval());
		}

	}
	
}
