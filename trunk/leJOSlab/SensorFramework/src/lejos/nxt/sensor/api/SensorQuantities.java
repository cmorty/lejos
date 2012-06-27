package lejos.nxt.sensor.api;

/**
 * Definitions for different kinds of data that sensors measure. 
 * @author Aswin
 *
 */
public interface SensorQuantities {
	/**
	 * Raw data from analogue sensors (in the range of 0-1023)
	 */
	public static final int RAW=0;
	/**
	 * Distance between sensor and some object (in cm)
	 */
	public static final int LENGTH=1;
	/**
	 * rate of turn (in rad/sec)
	 */
	public static final int TURNRATE=2;
	/**
	 * Acceleration (in m/sec2)
	 */
	public static final int ACCELERATION=3;
	/**
	 * Rotation, or angle between robot and some reference axis 
	 */
	public static final int ROTATION=4;

	// TODO: add some more
}
