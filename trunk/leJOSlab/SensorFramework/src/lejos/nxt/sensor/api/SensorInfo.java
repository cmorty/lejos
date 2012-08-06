package lejos.nxt.sensor.api;

/**
 * The SensorInfo represents a sensor that provides information about its settings
 * @author Aswin
 *
 */
public interface SensorInfo {
	/** Returns the current internal sample rate of the sensor.
	 * @return
	 * sample rate in Hertz (samples/second)
	 */
	public float getSampleRate();
	
	//maybe add getSampleInterval() ?

	/**
	 * Returns the dynamic range of the sensor
	 * @return
	 * The number of units between 0 and maximum that the sensor can measure. 
	 */
	public float getMaximumRange();
}
