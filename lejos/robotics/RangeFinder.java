package lejos.robotics;

/**
 * Abstraction for a range finder sensor that returns the distance to the nearest object
 * 
 * @author Lawrie Griffiths
 *
 */
public interface RangeFinder {
	/**
	 * Get the range to the nearest object
	 * 
	 * @return the distance to the nearest object
	 */
	public float getRange();
}
