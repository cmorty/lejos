package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.Delay;
import lejos.util.EndianTools;

/**
 * Driver for the Dexter Industries compass sensor
 * 
 * @author Aswin
 * @version 1.0
 * 
 */
public class DiCompass extends I2CSensor implements SampleProvider, SensorInfo, SensorControl {

	// sensor configuration
	static final int							MODE_NORMAL					= 0;
	static final int							MODE_POSITIVE_BIAS	= 1;
	static final int							MODE_NEGATIVE_BIAS	= 2;
	private final static float[]	RATES								= { 0.75f, 1.5f, 3, 7.5f, 15, 30, 75 };
	private final static int[]		RANGEMULTIPLIER			= { 1370, 1090, 820, 660, 440, 390, 330, 230 };
	private final static float[]	RANGES							= { 0.88f, 1.3f, 1.9f, 2.5f, 4, 4.7f, 5.6f, 8.1f };
	static final int							CONTINUOUS					= 0;
	static final int							SINGLE							= 1;
	static final int							IDLE								= 2;

	// default configuration
	int														measurementMode			= MODE_NORMAL;
	int														range								= 6;
	int														rate								= 4;
	int														operatingMode				= CONTINUOUS;

	// sensor register adresses
	private static final int			I2C_ADDRESS					= 0x3C;
	protected static final int		REG_CONFIG					= 0x00;
	protected static final int		REG_MAGNETO					= 0x03;
	protected static final int		REG_STATUS					= 0x09;

	// local variables for common use
	float[]												raw									= new float[3];
	float[]												dummy								= new float[3];
	byte[]												buf									= new byte[6];
	private float									multiplier;

	/**
	 * Constructor for the driver. Also loads calibration settings when available.
	 * 
	 * @param port
	 */
	public DiCompass(I2CPort port) {
		super(port, I2C_ADDRESS, I2CPort.HIGH_SPEED, TYPE_LOWSPEED);
		configureSensor();
		// test(); // to degauss
	}

	/**
	 * Sets the configuration registers of the sensor according to the current
	 * settings
	 */
	private void configureSensor() {
		buf[0] = (byte) (3 << 5 + rate << 2 + measurementMode);
		buf[0] = (byte) (rate << 2 );
		buf[1] = (byte) (range << 5);
		buf[2] = (byte) (operatingMode);
		sendData(REG_CONFIG, buf, 3);
		multiplier = 1.0f / RANGEMULTIPLIER[range];
		// first measurement after configuration is not yet configured properly;
		fetchSample(dummy, 0);
	}

	/**
	 * Fills an array of floats with measurements from the sensor in the specified
	 * unit.
	 * <p>
	 * The array order is X, Y, Z
	 * <P>
	 * When the sensor is idle zeros will be returned.
	 */
	public void fetchSample(float[] ret, int offset) {
		// get raw data
		switch (operatingMode) {
			case 2:
				fetchSingleMeasurementMode(ret, offset);
				break;
			case (CONTINUOUS):
				fetch(ret, offset);
				break;
			default:
				for (int axis = 0; axis < 3; axis++)
					ret[axis + offset] = 0;
				break;
		}
	}

	/**
	 * Returns the raw values from the data registers of the sensor
	 * 
	 * @param ret
	 */
	private void fetch(float[] ret, int offset) {
		// The order of data registers seems to be X,Z,Y. (Aswin).
		getData(REG_MAGNETO, buf, 6);
		ret[0 + offset] = EndianTools.decodeShortBE(buf, 0) * multiplier;
		ret[1 + offset] = EndianTools.decodeShortBE(buf, 4) * multiplier;
		ret[2 + offset] = EndianTools.decodeShortBE(buf, 2) * multiplier;
	}

	/**
	 * fetches measurement in single measurement mode
	 * 
	 * @param ret
	 */
	private void fetchSingleMeasurementMode(float[] ret, int offset) {
		buf[0] = 0x01;
		sendData(0x02, buf[0]);
		Delay.msDelay(6);
		fetch(ret, offset);
	}

	/**
	 * @return Returns the measurement mode of the sensor (normal, positive bias
	 *         or negative bias).
	 *         <p>
	 *         positive and negative bias mode should only be used for testing the
	 *         sensor.
	 */
	public int getMeasurementMode() {
		return measurementMode;
	}

	/**
	 * @return The operating mode of the sensor (single measurement, continuous or
	 *         Idle)
	 */
	public int getOperatingMode() {
		return operatingMode;
	}

	/**
	 * @return The dynamic range of the sensor.
	 */
	public float getMaximumRange() {
		return RANGES[range];
	}

	@Override
	public String getProductID() {
		return "DiCompass";
	}

	@Override
	public String getVendorID() {
		return "Dexter";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	/**
	 * Reads the new data ready bit of the status register of the sensor.
	 * 
	 * @return True if new data available
	 */
	public boolean newDataAvailable() {
		getData(REG_STATUS, buf, 1);
		return ((buf[0] & 0x01) != 0);
	}

	/**
	 * @param measurementMode
	 *          Sets the measurement mode of the sensor.
	 */
	protected void setMeasurementMode(int measurementMode) {
		this.measurementMode = measurementMode;
		configureSensor();
	}

	/**
	 * Sets the operating mode of the sensor
	 * 
	 * @param operatingMode
	 *          Continuous is normal mode of operation
	 *          <p>
	 *          SingleMeasurement can be used to conserve energy or to increase
	 *          maximum measurement rate
	 *          <p>
	 *          Idle is to stop the sensor and conserve energy
	 */
	public void setOperatingMode(int operatingMode) {
		this.operatingMode = operatingMode;
		configureSensor();
	}

	/**
	 * Sets the dynamic range of the sensor (1.3 Gauss is default).
	 * 
	 * @param range
	 */
	public void setRange(int range) {
		this.range = (byte) range;
		configureSensor();
	}

	/**
	 * Self-test routine of the sensor.
	 * 
	 * @return An array of boolean values. A true indicates the sensor axis is
	 *         working properly.
	 */
	public boolean[] test() {
		boolean[] ret = new boolean[3];

		// store current settings;
		int currentMode = measurementMode;
		int currentRange = range;
		int currentOperatingMode = operatingMode;

		// modify settings for testing;
		measurementMode = MODE_POSITIVE_BIAS;
		range = 5;
		operatingMode = SINGLE;
		configureSensor();

		// get measurement
		buf[0] = 0x01;
		sendData(0x02, buf[0]);
		Delay.msDelay(6);
		fetch(dummy, 0);

		// test for limits;
		for (int axis = 0; axis < 3; axis++)
			if (dummy[axis] > 243 && dummy[axis] < 575)
				ret[axis] = true;
			else
				ret[axis] = false;

		// restore settings;
		measurementMode = currentMode;
		range = currentRange;
		operatingMode = currentOperatingMode;
		configureSensor();

		return ret;
	}

	public int getQuantity() {
		return Quantities.MAGNETIC_FIELD;
	}

	public int getElementsCount() {
		return 3;
	}

	public float fetchSample() {
		fetchSample(dummy, 0);
		return dummy[0];
	}

	public void setSampleRate(float rate) {
		for (int i = 0; i < RATES.length; i++)
			if (RATES[i] == rate)
				rate = i;
		configureSensor();
	}

	public float[] getSampleRates() {
		return RATES;
	}

	public void start() {
		this.setOperatingMode(CONTINUOUS);
	}

	public void stop() {
		this.setOperatingMode(IDLE);
	}

	public float getSampleRate() {
		return RATES[rate];
	}

	public void setRange(float range) {
		for (int i = 0; i < RANGES.length; i++)
			if (RANGES[i] == range)
				range=i;
		configureSensor();
	}

	public float[] getRanges() {
		return RANGES;
	}

}
