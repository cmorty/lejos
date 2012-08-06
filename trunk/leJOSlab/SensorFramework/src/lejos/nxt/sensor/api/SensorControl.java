package lejos.nxt.sensor.api;

/**
 * The SensorControl represents a sensor that can be controlled.
 * <ul>Controls include:
 * <li>Starting the sensor.</li> 
 * <li>Stopping the sensor.</li>
 * <li>Setting the internal sampling rate.<br> The internal sampling rate is the speed at which the sensor updates its data registers. It is also the maximum rate at which a sensor can be queried without getting the same sample twice.</li>
 * <li>Setting the dynamic range.<br> The dynamic range is the difference between the lowest (or zero) value and the maximum value a sensor can measure.</li>
 * </ul>
 * 
 *  @author Aswin
 *
 */
public interface SensorControl {


	/** Sets the sample rate of the sensor.
	 * @param rate
	 * The sample rate in Hertz (samples/second).
	 * Unsupported sample rates are ignored.
	 */
	public void setSampleRate(float rate);

	/** Returns the sample rates supported by the sensor.
	 * @return
	 * An array of sample rates (in Hertz) supported by the sensor.
	 */
	public float[] getSampleRates();
	
	/** Sets the dynamic ranges of the sensor.
	 * @param range
	 * The dynamic range .
	 * Unsupported dynamic ranges are ignored.
	 */
	public void setRange(float range);

	/** Returns the dynamic ranges supported by the sensor.
	 * @return
	 * An array of dynamic ranges supported by the sensor.
	 */
	public float[] getRanges();
	
		
	/** Instructs the sensor to start taking measurements.
	 * 
	 */
	public void start();

	
	/** Instructs the sensor to stop taking measurements and go into a state of low power consumption if supported by the sensor.
	 * 
	 */
	public void stop();
}
