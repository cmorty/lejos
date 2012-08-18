package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.LcLight;
import lejos.util.Delay;

/**
 * This example shows both uncalibrated and calibrated values returned by the light sensor and lets the user calibrate its sensor. <p>
 * Pressing enter recalibrates the sensor to a 0-100 range. Prior to pressing enter the user should feed the light sensor both most light and most dark conditions.
 * Calibarition values are stored on the file system and reused whenever the program is run again.
 * <p>
 * The source code shows how to calibrate a sensor for offset and scale and how to use the CalibrationManager.
 * @author Aswin
 *
 */
public class CalibrateLight {
	
	static CalibrationManager calMan=new CalibrationManager(); 
	
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float min=Float.POSITIVE_INFINITY;
		float max=Float.NEGATIVE_INFINITY;
		float uncalibratedValue, calibratedValue;
		float[] buf=new float[3];

		// Create a new calibration set for the light sensor (if it does not exist yet) and make it the current Calibration set
		calMan.add("LcLight",1);
		calMan.setCurrent("LcLight");
		calMan.save();

		// instantiate the light sensor and buffer its readings;
		LcLight sensor=new LcLight(SensorPort.S1);
		SampleBuffer buffer=new SampleBuffer(sensor,10);
		Calibrate calibrated=new Calibrate(buffer,"LcLight");
		
		while (true) {
			
			LCD.clear();
			LCD.drawString("Calibration",0,0);
			
			// get the sample
			calibrated.fetchSample(buf, 0);
			uncalibratedValue=buffer.fetchSample();
			calibratedValue=calibrated.fetchSample();
			calibratedValue=buf[0];
			
			// Keep track of maximum and minimum values
			if (min>uncalibratedValue) min=uncalibratedValue;
			if (max<uncalibratedValue) max=uncalibratedValue;
			
			// show both uncalibrated and calibrated sample
			LCD.drawString("Before: "+uncalibratedValue, 0, 1);
			LCD.drawString("After: "+calibratedValue, 0, 2);
			
			// Update calibration set when enter is pressed
			LCD.drawString("Enter to calibrate",0,5);
			if (Button.ENTER.isDown()) {
				calibrate(min,max);
				min=Float.POSITIVE_INFINITY;
				max=Float.NEGATIVE_INFINITY;
			}
			Delay.msDelay(100);
			
		}
		
	}
	
	public static void calibrate(float min, float max) {
		calMan.setCurrent("LcLight");
		float[] scale=calMan.getScale();
		float[] offset=calMan.getOffset();
		offset[0]=min;
		scale[0]=(max-min)/100;
		calMan.save();
		LCD.clear();
		LCD.drawString("Offset:"+offset[0], 0, 0);
		LCD.drawString("Scale:"+scale[0], 0, 1);
		while (Button.ENTER.isDown()) Delay.msDelay(100);
	}

	
	
}
