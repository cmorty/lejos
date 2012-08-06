/**
 * 
 */
package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.api.SampleProvider;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.*;
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
    	SampleProvider sensor=new LcLight(SensorPort.S1);
    	
    	// Instantiate statistics filter, configure it to return the mean of last 10 samples
    	StatisticsFilter stat=new StatisticsFilter(sensor,StatisticsFilter.MEAN,10);
    	
    	// Instantiate buffer, configure it to fetch a new sample at 10 Hertz
    	SampleBuffer buffer=new SampleBuffer(stat,10);
    	
    	// Show average light coditions over the past second
    	while (!Button.ESCAPE.isDown()) {
    		LCD.drawString("Value: "+buffer.fetchSample(), 0, 0);
    		Delay.msDelay(100);
    	}
    }


}
