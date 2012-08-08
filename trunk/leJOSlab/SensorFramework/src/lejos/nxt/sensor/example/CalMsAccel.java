package lejos.nxt.sensor.example;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.DiCompass;
import lejos.nxt.sensor.sensor.DiImuAccel;
import lejos.nxt.sensor.sensor.MsAccelV3;
import lejos.util.Delay;
import lejos.util.LogColumn;

/**
 * Example to calibrate an accelerometer.
 * <p>
 * 
 * @author Aswin To calibrate turn the sensor on all six sides and keep on each
 *         side for a few seconds. Then press enter to store the calibration
 *         parameters.
 *         <p>
 *         Avoids shocks when calibrating.
 * 
 */
public class CalMsAccel {

	/**
	 * One can modify the fields below to calibrate other accelerometers,
	 * magnetometers and gyro's (All triaxis)
	 */

	// Name of calibration set to use
	static String							NAME							= "MsAccel";
	// Strength of the reference signal (9.81 for accelerometers, field strength
	// for magnetometers)
	static float							RANGE							= 9.81f;
	// If true both offset and scale are calibrated, if false just offset
	// calibration is done
	static boolean						CALIBRATEFORSCALE	= false;
	// Timeconstant for the lowpass filter that helps to filter out noise (use 0.8
	// for accelerometers and 0.2 for compasses)
	static float							TIMECONSTANT			= 0.8f;
	// Sensorport to use
	static SensorPort					port							= SensorPort.S1;
	// Sensor to calibrate
	static SampleProvider			sensor						= new MsAccelV3(port);

	static CalibrationManager	CALMAN						= new CalibrationManager();
	float[]										raw								= new float[3];
	float[]										cal								= new float[3];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CalMsAccel c = new CalMsAccel();
	}

	public CalMsAccel() {
		boolean forward = true;
		float[] min = new float[3];
		float[] max = new float[3];
		float[] sample = new float[3];
		SampleProvider sense, sense2;

		initRange(min, max);

		// Set the calibration set
		if (CALMAN.setCurrent(NAME) == false) {
			CALMAN.add(NAME, 3);
			CALMAN.save();
		}
		CALMAN.setCurrent(NAME);

		// connect to NXTchartingLogger
		lejos.util.NXTDataLogger dlog = new lejos.util.NXTDataLogger();
		lejos.nxt.comm.NXTConnection conn = Bluetooth.waitForConnection(15000, lejos.nxt.comm.NXTConnection.PACKET);
		try {
			dlog.startRealtimeLog(conn);
			dlog.appendColumn(new LogColumn("X", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MIN-X", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MAX-X", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("Y", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MIN-Y", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MAX-Y", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("Z", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MIN-Z", LogColumn.DT_FLOAT));
			dlog.appendColumn(new LogColumn("MAX-Z", LogColumn.DT_FLOAT));
		}
		catch (IOException e) {
			dlog = null;
		}

		port.i2cEnable(I2CPort.HIGH_SPEED);

		// lowpass and buffer the sample
		sense = new LowPass(sensor, TIMECONSTANT);
		sense = new SampleBuffer(sense, 40);
		// Get a second sample stream to show both uncalibrated and calibrated
		// values on screen
		sense2 = new Calibrate(sense, NAME);

		while (true) {

			LCD.clear();
			LCD.drawString("Raw       Cal", 0, 0);

			// get the sample
			sense.fetchSample(raw, 0);
			sense2.fetchSample(cal, 0);
			// show both uncalibrated and calibrated sample and write min and max to
			// logger;
			for (int i = 0; i < 3; i++) {
				LCD.drawString(fmt(raw[i]), 0, i + 1);
				LCD.drawString(fmt(cal[i]), 8, i + 1);
				if (min[i] > raw[i])
					min[i] = raw[i];
				if (max[i] < raw[i])
					max[i] = raw[i];
				if (dlog != null) {
					dlog.writeLog(raw[i]);
					dlog.writeLog(min[i]);
					dlog.writeLog(max[i]);
				}
			}
			if (dlog != null)
				dlog.finishLine();

			// Update calibration set when enter is pressed
			LCD.drawString("Enter to calibrate", 0, 7);
			if (Button.ENTER.isDown()) {
				calibrate(min, max);
				initRange(min, max);
				forward = !forward;
				if (forward)
					Motor.A.forward();
				else
					Motor.A.backward();
			}
			Delay.msDelay(25);
		}
	}

	public void calibrate(float[] min, float[] max) {
		float[] scale = CALMAN.getScale();
		float[] offset = CALMAN.getOffset();
		LCD.clear();
		LCD.drawString("Offset    Scale", 0, 0);

		for (int i = 0; i < 3; i++) {
			offset[i] = min[i] + (max[i] - min[i]) / 2;
			if (this.CALIBRATEFORSCALE == true)
				scale[i] = (max[i] - min[i]) / (2 * RANGE);
			else
				scale[i] = 1;
			LCD.drawString(fmt(offset[i]), 0, i + 1);
			LCD.drawString(fmt(scale[i]), 8, i + 1);
		}
		CALMAN.save();
		while (Button.ENTER.isDown())
			Delay.msDelay(25);
	}

	protected String fmt(float in) {
		String tmp = Float.toString(in) + "00000";
		return tmp.substring(0, 5);
	}

	protected void initRange(float[] min, float[] max) {
		for (int i = 0; i < 3; i++) {
			min[i] = Float.POSITIVE_INFINITY;
			max[i] = Float.NEGATIVE_INFINITY;
		}

	}
}
