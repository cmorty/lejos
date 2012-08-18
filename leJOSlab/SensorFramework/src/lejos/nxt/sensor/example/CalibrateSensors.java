package lejos.nxt.sensor.example;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.*;
import lejos.util.Delay;
import lejos.util.LogColumn;
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
 * <li>Press enter</li>
 * <li>Tumble the sensor on all six sides and wait for the signal to settle on
 * each of the sides</li>
 * <li>Press enter and examine the offset (and scale) factors</li>
 * <li>Release enter and check the calibrated values</li>
 * <li>Repeat this proces if needed.</li>
 * </ol>
 * <ol>
 * Magnetometer
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press enter</li>
 * <li>Examine the NXTChartingLogger and try to get all three max lines as high
 * as possible by turning the sensor on all sides</li>
 * <li>Press enter and examine the offset (and scale) factors</li>
 * <li>Release enter and check the calibrated values</li>
 * <li>Repeat this proces if needed.</li>
 * </ol>
 * <ol>
 * Gyro, offset only
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press enter</li>
 * <li>Keep the sensor still</li>
 * <li>Examine the NXTChartingLogger and wait till the min and max lines
 * stabilize.
 * <li>Press enter and examine the offset factors</li>
 * <li>Release enter and check the calibrated values</li>
 * <li>Repeat this proces if needed.</li>
 * </ol>
 * *
 * <ol>
 * Gyro, offset and scale
 * <li>Wait till the sample settles, see the chart or display for this.</li>
 * <li>Press enter</li>
 * <li>center the sensor on a turntable</li>
 * <li>Start the turntable ;-)</li>
 * <li>Tumble the sensor on all six sides and wait for the signal to settle on
 * each of the sides</li>
 * <li>Press enter and examine the offset (and scale) factors</li>
 * <li>Release enter and check the calibrated values</li>
 * <li>Repeat this proces if needed.</li>
 * </ol>
 * 
 * <p>
 * 
 * @author Aswin
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
	String					NAME							= "test2";

	/**
	 * Indicates type of calibration If true both offset and scale are calibrated,
	 * if false just offset calibration is done
	 */
	boolean					CALIBRATEFORSCALE	= false;

	/**
	 * Port of the sensor to calibrate
	 */
	SensorPort			port							= SensorPort.S3;

	/**
	 * Driver for the sensor to calibrate
	 */
	SampleProvider	sensor						= new CuADXL345(port);

	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			CalibrateSensors c = new CalibrateSensors();
		}
		catch (Exception e) {
			System.exit(0);
		}
	}

	public CalibrateSensors() throws Exception {
		float range = 1, timeConstant = 0;
		SampleProvider sense, sense2;
		CalibrationManager calMan = new CalibrationManager();
		int elements = sensor.getElementsCount();
		float[] min = new float[elements];
		float[] max = new float[elements];
		float[] raw = new float[elements];
		float[] cal = new float[elements];

		switch (sensor.getQuantity()) {
			case Quantities.ACCELERATION:
				range = 9.81f;
				timeConstant = 0.95f;
				break;
			case Quantities.TURNRATE:
				range = 3.456f;
				timeConstant = 0.1f;
				break;
			case Quantities.MAGNETIC_FIELD:
				range = 1; // adjust to local strength of magnetic field;
				timeConstant = 0.1f;
				break;
			default:
				throw new RuntimeException("Unsupported sensor");
		}

		initRange(min, max);

		// Set the calibration set
		if (calMan.setCurrent(NAME) == false) {
			calMan.add(NAME, elements);
			calMan.save();
		}
		calMan.setCurrent(NAME);

		// connect to NXTchartingLogger
		NXTDataLogger dlog = connect();
		if (dlog != null)
			defineColumns(dlog, elements);

		port.i2cEnable(I2CPort.HIGH_SPEED);

		// lowpass and buffer the sample
		sense = new LowPass(sensor, timeConstant);
		sense = new SampleBuffer(sense, 40);
		// Get a second sample stream to show both uncalibrated and calibrated
		// values on screen
		sense2 = new Calibrate(sense, NAME);

		while (!Button.ESCAPE.isDown()) {

			LCD.clear();
			LCD.drawString("Raw       Cal", 0, 0);

			// get the sample
			sense.fetchSample(raw, 0);
			sense2.fetchSample(cal, 0);
			// show both uncalibrated and calibrated sample and write min and max to
			// logger;
			for (int i = 0; i < elements; i++) {
				LCD.drawString(fmt(raw[i]), 0, i + 1);
				LCD.drawString(fmt(cal[i]), 8, i + 1);
				if (min[i] > raw[i])
					min[i] = raw[i];
				if (max[i] < raw[i])
					max[i] = raw[i];
				if (dlog != null) {
					dlog.writeLog(raw[i]);
					dlog.writeLog(cal[i]);
					dlog.writeLog(min[i]);
					dlog.writeLog(max[i]);
				}
			}
			if (dlog != null)
				dlog.finishLine();

			// Update calibration set when enter is pressed
			LCD.drawString("Enter to calibrate", 0, 7);
			if (Button.ENTER.isDown()) {
				calibrate(calMan, min, max, range);
				dlog.writeComment("Calibrated");
				// Reset
				initRange(min, max);
			}
			Delay.msDelay(33);
		}
	}


	/**
	 * Calculate offset and scale and store these
	 * 
	 * @param min
	 * @param max
	 */
	private void calibrate(CalibrationManager calMan, float[] min, float[] max, float range) {
		float[] scale = calMan.getScale();
		float[] offset = calMan.getOffset();
		LCD.clear();
		LCD.drawString("Offset    Scale", 0, 0);

		for (int i = 0; i < min.length; i++) {
			offset[i] = min[i] + (max[i] - min[i]) / 2;
			if (this.CALIBRATEFORSCALE == true)
				scale[i] = (max[i] - min[i]) / (2 * range);
			else
				scale[i] = 1;
			LCD.drawString(fmt(offset[i]), 0, i + 1);
			LCD.drawString(fmt(scale[i]), 8, i + 1);
		}
		calMan.save();
		while (Button.ENTER.isDown())
			Delay.msDelay(25);
	}

	protected String fmt(float in) {
		if (Math.abs(in) < 0.001)
			return "0.000";
		String tmp = Float.toString(in) + "00000";
		return tmp.substring(0, 5);
	}

	protected void initRange(float[] min, float[] max) {
		for (int i = 0; i < min.length; i++) {
			min[i] = Float.POSITIVE_INFINITY;
			max[i] = Float.NEGATIVE_INFINITY;
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
	
	private void defineColumns(NXTDataLogger dlog, int elements) {
		LogColumn[] columns = new LogColumn[elements * 4];
		for (int i = 0; i < elements; i++)
		{
			columns[i*4] = new LogColumn("Raw " + Integer.toString(i), LogColumn.DT_FLOAT);
			columns[i*4 + 1] = new LogColumn("Calibrated " + Integer.toString(i), LogColumn.DT_FLOAT);
			columns[i*4 + 2] = new LogColumn("Min Raw " + Integer.toString(i), LogColumn.DT_FLOAT);
			columns[i*4 + 3] = new LogColumn("Max Raw " + Integer.toString(i), LogColumn.DT_FLOAT);
		}
		dlog.setColumns(columns);
	}
}
