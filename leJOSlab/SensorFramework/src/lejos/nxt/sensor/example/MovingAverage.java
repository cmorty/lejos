/**
 * 
 */
package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.api.SensorDataProvider;
import lejos.nxt.sensor.filter.SensorDataBuffer;
import lejos.nxt.sensor.filter.StatisticsFilter;
import lejos.nxt.sensor.sensor.LightSensor;
import lejos.util.Delay;

/**
 * @author Aswin
 *
 */
public class MovingAverage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MovingAverage test= new MovingAverage();

	}
	
	public MovingAverage(){ 
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
