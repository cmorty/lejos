package lejos.pc.comm;

/**
 * Structure that gives information about a NXT sensor.
 * 
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.1 August 3, 2006
 * @see NXTCommand
 */
public class InputValues {
	
	/**
	 * The number ofd the port (0 - 3)
	 */
	public int inputPort;
	/**
	 * NXT indicates if it thinks the data is valid (as it has had time to stabilize)
	 */
	public boolean valid = true;
	
	/**
	 * Indicates whether the sensor has been calibrated - not currently used.
	 */
	public boolean isCalibrated;
	
	/**
	 * The type of the sensor - see NXTProtocol.
	 */
	public int sensorType;
	
	/**
	 * The mode of the sensor - see NXTProtocol.
	 */
	public int sensorMode;
	/**
	 * The raw value from the Analog to Digital (AD) converter.
	 */
	public int rawADValue;
	/**
	 * The normalized value from the Analog to Digital (AD) converter. I really don't
	 * know for sure which values are normalized yet.
	 * 0 to 1023
	 */
	public int normalizedADValue;
	/**
	 * The scaled value starts working after the first call to the sensor.
	 * The first value will be the raw value, but after that it produces scaled values.
	 * With the touch sensor, off scales to 0 and on scales to 1.
	 */
	public short scaledValue;
	/**
	 * Currently unused.
	 */
	public short calibratedValue;
}
