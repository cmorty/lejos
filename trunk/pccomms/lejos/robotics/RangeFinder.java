package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for a range finder sensor that returns the distance to the nearest object
 * @see lejos.robotics.RangeScanner
 * @author Lawrie Griffiths
 */
public interface RangeFinder {
	/**
	 * Get the range to the nearest object
	 * 
	 * @return the distance to the nearest object
	 */
	public float getRange();
}
