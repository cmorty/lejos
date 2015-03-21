package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.util.EndianTools;

/**
 * This class supports the <a href="http://www.hitechnic.com">HiTechnic</a>
 * barometric sensor.
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class BarometricHTSensor extends I2CSensor {

	private static final int BAROMETRIC_TEMPERATURE = 0x42;
	private static final int BAROMETRIC_PRESSURE = 0x44;
	private static final int BAROMETRIC_PRESSURE_CALIBRATION = 0x46;
	private final double INHG_TO_HPA = 2992 / 1013.25;

	private final byte[] buffer = new byte[2];

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            the {@link I2CPort} the sensor is connected to.
	 */
	public BarometricHTSensor(final I2CPort port) {
		super(port);
	}

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            the {@link I2CPort} the sensor is connected to.
	 * @param address
	 *            the address
	 */
	public BarometricHTSensor(final I2CPort port, final int address) {
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	/**
	 * @return the barometric pressure measured by the sensor in units of 1/1000
	 *         inches of mercury (inHg).
	 */
	public long getPressureImperial() {
		long result = Long.MIN_VALUE;
		if (0 == getData(BAROMETRIC_PRESSURE, buffer, 2)) {
			result = EndianTools.decodeUShortBE(buffer, 0);
		}
		return result;
	}

	/**
	 * @return the barometric pressure measured by the sensor in units of 1/10
	 *         hectopascals (hPa).
	 */
	public long getPressureMetric() {
		long result = getPressureImperial();
		if (Long.MIN_VALUE != result) {
			result /= INHG_TO_HPA;
		}
		return result;
	}

	/**
	 * @return the temperature measured by the sensor in units of 1/10 °C.
	 */
	public int getTemperature() {
		int result = Integer.MIN_VALUE;
		if (0 == getData(BAROMETRIC_TEMPERATURE, buffer, 2)) {
			result = EndianTools.decodeShortBE(buffer, 0);
		}
		return result;
	}

	/**
	 * Re-calibrates the sensor.
	 * 
	 * @param calibrationImperial
	 *            the recalibration value in units of 1/1000 inches of mercury
	 *            (inHg).
	 */
	public void recalibrate(final int calibrationImperial) {
		EndianTools.encodeShortBE(calibrationImperial, buffer, 0);
		super.sendData(BAROMETRIC_PRESSURE_CALIBRATION, buffer, 2);
	}

	/**
	 * @return the present calibration value in units of of 1/1000 inches of
	 *         mercury (inHg). Will be 0 in case no explicit recalibration has
	 *         been performed.
	 */
	public int getCalibrationImperial() {
		int result = Integer.MIN_VALUE;
		if (0 == getData(BAROMETRIC_PRESSURE_CALIBRATION, buffer, 2)) {
			result = EndianTools.decodeUShortBE(buffer, 0);
		}
		return result;
	}

	/**
	 * @return the present calibration value in units of of 1/10 hectopascals
	 *         (hPa). Will be 0 in case no explicit recalibration has been
	 *         performed.
	 */
	public int getCalibrationMetric() {
		int result = getCalibrationImperial();
		if (Integer.MIN_VALUE != result) {
			result /= INHG_TO_HPA;
		}
		return result;
	}
}