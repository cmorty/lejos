package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/**
 * This class supports the <a
 * href="http://dexterindustries.com/TIR_Sensor.html">Thermal Infrared
 * Sensor</a> by dexter industries.
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class DexterTIRSensor extends I2CSensor {

	private static final int AMBIENT_TEMPERATURE = 0x00;
	private static final int OBJECT_TEMPERATURE = 0x01;
	// TODO support emissivity
	// private static final int EMISSIVITY = 0x02;

	private static float TEMP_FACTOR = 0.02f;
	private static float KELVIN_NULL_IN_CELSIUS = -273.15f;

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            the {@link I2CPort} the sensor is connected to.
	 */
	public DexterTIRSensor(final I2CPort port) {
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
	public DexterTIRSensor(final I2CPort port, final int address) {
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	/**
	 * @return the ambient temperature measured by the sensor in Kelvin.
	 */
	public float getAmbientTemperatureKelvin() {
		return getTemperatureInKelvin(AMBIENT_TEMPERATURE);
	}

	/**
	 * @return the ambient temperature measured by the sensor in Celsius.
	 */
	public float getAmbientTemperatureCelsius() {
		return convertKelvinToCelsius(getAmbientTemperatureKelvin());
	}

	/**
	 * @return the object temperature measured by the sensor in Kelvin.
	 */
	public float getObjectTemperatureKelvin() {
		return getTemperatureInKelvin(OBJECT_TEMPERATURE);
	}

	/**
	 * @return the object temperature measured by the sensor in Celsius.
	 */
	public float getObjectTemperatureCelsius() {
		return convertKelvinToCelsius(getObjectTemperatureKelvin());
	}

	private float getTemperatureInKelvin(int registerAddress) {
		float result = Float.NaN;
		byte[] buffer = new byte[2];
		// read registers
		if (0 == getData(registerAddress, buffer, 2)) {
			// create float value
			result = ((buffer[1]) << 8) + buffer[0];
			// get temperature in Kelvin
			result = (result * TEMP_FACTOR);
		}
		return result;
	}

	private float convertKelvinToCelsius(float kelvin) {
		float result = Float.NaN;
		if (!Float.isNaN(kelvin)) {
			// convert to Celsius
			result = kelvin + KELVIN_NULL_IN_CELSIUS;
		}
		return result;
	}
}