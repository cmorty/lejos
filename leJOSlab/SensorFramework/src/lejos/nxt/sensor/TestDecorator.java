package lejos.nxt.sensor;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * Demos the sensor framework concept. <p>
 * This demo uses a light sensor. Calibrates it to a dark to light range of 0-100 and then updates the calibrated value every second. 
 * @author Aswin
 *
 */
public class TestDecorator{
	int counter=0;
	// set the sensor of choice (or use DummySensor)
	SensorDataProvider sensor=new LightSensor(SensorPort.S1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestDecorator test=new TestDecorator();

	}
	
	public TestDecorator() {
		float time;
		float value;
		CalibrateOffsetScale filter=new CalibrateOffsetScale(sensor);
		calibrate(filter);
		SensorDataBuffer buffer=new SensorDataBuffer(filter);
		buffer.setRefreshRate(1000);
		
//		filter.setStatistic(StatisticsFilter.mean);
//		filter.setSampleSize(5);
		while (!Button.ESCAPE.isDown()) {
			time=System.nanoTime();
			value=buffer.fetchData();
			time=(System.nanoTime()-time)/1000000;
			LCD.drawString("Value: "+value, 0, 5);
			LCD.drawString("dt: "+time, 0, 6);
			Delay.msDelay(buffer.getRefreshRate());
		}
	}

	private void calibrate(CalibrateOffsetScale filter) {
		LCD.drawString("Set dark", 0, 0);
		LCD.drawString("Press enter", 0, 1);
		Button.ENTER.waitForPressAndRelease();
		filter.calibrateLow(0, 50);
		LCD.clear();
		LCD.drawString("Set light", 0, 0);
		LCD.drawString("Press enter", 0, 1);
		Button.ENTER.waitForPressAndRelease();
		filter.calibrateHigh(100, 50);
		LCD.clear();
	}
	

}
