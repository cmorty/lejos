package lejos.nxt.sensor.example;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.sensor.api.SampleProviderVector;
import lejos.nxt.sensor.filter.CalibrateOffsetScale;
import lejos.nxt.sensor.filter.ToScalar;
import lejos.nxt.sensor.filter.Integrator;
import lejos.nxt.sensor.sensor.DiIMUGyro;
import lejos.util.Delay;


/**
 * Demos the concept of the sensor framework. <p>
 * This demo uses a triaxis gyro (from Dexter Industries IMU) and transforms its ouput to (buffered) azyimuth.
 * The transformation proces takes several steps:
 * <li>Get the current rate of turn over three axes from the gyro sensor</li>
 * <li>Extract the turn rate over the Z-axis</li>
 * <li>Transform to calibrated turn rate</li>
 * <li>Integrate turn rate to (buffered) azimuth</li>
 * @author Aswin
 *
 */
public class TestTriaxisSensor {
	int counter=0;
	// set the sensor of choice (or use DummySensor)
	SampleProviderVector sensor=new DiIMUGyro(SensorPort.S1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestTriaxisSensor test=new TestTriaxisSensor();

	}
	
	public TestTriaxisSensor() {
		float time;
		float value;
		ToScalar justOne =new ToScalar(sensor);
		justOne.setAxis("Z");
		CalibrateOffsetScale calibrated = new CalibrateOffsetScale(justOne);
		calibrate(calibrated);
		Integrator integrated=new Integrator(calibrated);
		while (!Button.ESCAPE.isDown()) {
			time=System.nanoTime();
			value=integrated.fetchSample();
			time=(System.nanoTime()-time)/1000000;
			LCD.drawString("Value: "+value, 0, 5);
			LCD.drawString("dt: "+time, 0, 6);
			Delay.msDelay(integrated.getMinimumFetchInterval());
		}
	}

	/**
	 * Calibrate the sensor
	 * @param filter
	 * The calibrator object used to convert data to calibrated data
	 */
	private void calibrate(CalibrateOffsetScale filter) {
		LCD.drawString("Calibrate gyro", 0, 0);
		LCD.drawString("Press enter", 0, 1);
		Button.ENTER.waitForPressAndRelease();
		Sound.beep();
		filter.calibrateLow(0, 500);
		Sound.beep();
		LCD.clear();
	}
	

}
