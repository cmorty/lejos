package lejos.nxt.sensor.example;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.*;
import lejos.util.Delay;
import lejos.util.NXTDataLogger;

/**
 * Utility program to calibrate an accelerometer, gyro or magnetometer.
 * <p>
 * Modify the the program (fields:, port and sensor) to configure the program for your
 * sensor. Run the program, connect to the NXTChartingLogger. Then follow the
 * steps for your sensor type. Avoids shocks when calibrating.
 * <ol>
 * Accelerometer
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press left</li>
 * <li>Tumble the sensor on all six sides and wait for the signal to settle on
 * each of the sides</li>
 * <li>Press enter to store calibration parameters</li>
 * </ol>
 * <ol>
 * Magnetometer
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press left</li>
 * <li>Examine the NXTChartingLogger and try to get all three max lines as high
 * as possible by turning the sensor on all sides</li>
 * <li>Do the same to get the minimum lines as low as possible</li>
 * <li>Press enter to store calibration parameters</li>
 * </ol>
 * <ol>
 * Gyro, offset only
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press left</li>
 * <li>Keep the sensor still</li>
 * <li>Examine the NXTChartingLogger and wait till the min and max lines
 * stabilize.
 * <li>Press enter to store calibration parameters</li>
 * </ol>
 * *
 * <ol>
 * Gyro, offset and scale
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press left</li>
 * <li>center the sensor on a turntable</li>
 * <li>Start the turntable ;-)</li>
 * <li>Tumble the sensor on all six sides and wait for the signal to settle on
 * each of the sides</li>
 * <li>Press enter to store calibration parameters</li>
 * </ol>
 * 
 * <p>
 * 
 * @author Aswin Bouwmeester
 * 
 */
public class CalibrateSensors {

	/**
	 * One can modify the fields below to calibrate other accelerometers,
	 * magnetometers and gyro's (All triaxis)
	 */

	/**
	 * Name of calibration set to use
	 * <p>
	 * It is suggested to use the name of the sensor driver
	 */
	String					NAME							= "DiCompass2";

	/**
	 * Port of the sensor to calibrate
	 */
	SensorPort			port							= SensorPort.S1;

	/**
	 * Driver for the sensor to calibrate
	 */
	SampleProvider	sensor						= new DiCompass(port);


	public static void main(String[] args) {
		CalibrateSensors c = new CalibrateSensors();
	}

	public CalibrateSensors()  {
		CalibratorFilter calibrate= new CalibratorFilter(sensor);
		float[] sample=new float[sensor.getElementsCount()];

		switch (sensor.getQuantity()) {
			case Quantities.ACCELERATION:
				calibrate.setRange( 2 * 9.81f);
				calibrate.setReference(0);
				calibrate.setTimeConstant(0.95f);
				calibrate.calibrateForOffset(true);
				calibrate.calibrateForScale(true);
				break;
			case Quantities.TURNRATE:
				calibrate.setRange( 200);
				calibrate.setReference(0);
				calibrate.setTimeConstant(0.1f);
				calibrate.calibrateForOffset(true);
				calibrate.calibrateForScale(false);
				break;
			case Quantities.MAGNETIC_FIELD:
				calibrate.setRange( 1); // adjust to local strength of magnetic field for true output;
				calibrate.setReference(0);
				calibrate.setTimeConstant(0.1f);
				calibrate.calibrateForOffset(true);
				calibrate.calibrateForScale(true);
				break;
			default:
				throw new RuntimeException("Unsupported sensor");
		}



		// connect to NXTchartingLogger
		NXTDataLogger dlog = connect();
		if (dlog != null)
			calibrate.setNXTDataLogger(dlog);


		while (!Button.ESCAPE.isDown()) {
			if (Button.ENTER.isDown()) {
				calibrate.storeCalibration(NAME);
				Sound.beep();
				while (Button.ENTER.isDown());
			}
			if (Button.LEFT.isDown()) {
					calibrate.startCalibration(); 
				while (Button.LEFT.isDown());
			}
			if (Button.RIGHT.isDown()) {
				calibrate.stopCalibration(); 
			while (Button.RIGHT.isDown());
		}
			
			calibrate.fetchSample(sample, 0);
			LCD.clear();
			for (int i=0;i<3;i++)
				LCD.drawString(Float.toString(sample[i]), 0, i);
			Delay.msDelay(33);
		}

	}




	static NXTDataLogger connect() {
		LCD.clear();
		LCD.drawString("Connecting..", 0, 0);
		NXTDataLogger dlog = new lejos.util.NXTDataLogger();
		NXTConnection conn = Bluetooth.waitForConnection(15000, lejos.nxt.comm.NXTConnection.PACKET);
		try {
			dlog.startRealtimeLog(conn);
		}
		catch (IOException e) {
			dlog = null;
		}
		LCD.clear();
		return dlog;
	}
	
	
	
}
