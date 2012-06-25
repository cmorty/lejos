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
	public static final int RANGE=1;
	/**
	 * rate of turn (in rad/sec)
	 */
	public static final int TURNRATE=2;
	/**
	 * Acceleration (in m/sec2)
	 */
	public static final int ACCELERATION=3;
	/**
	 * Tilt (in rad) to front (or tilt to left?)
	 */
	public static final int TILT=4;
	/**
	 * Azimuth (direction), the angle (in rad) the sensor makes with some reference line in the XY-plane
	 */
	public static final int AZIMUTH=5;
	// TODO: add some more
}
