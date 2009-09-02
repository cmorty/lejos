package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for a single range scanning sensor, rotating platform with a range finder,
 * or a complete robot, that obtains a set of range readings at a set of angles to#
 * the robot's heading.
 */
public interface RangeScanner {
	/**
	 * Take a set of range readings. The RangeReadings object defines the
	 * number of readings and their angles to the robot's heading.
	 * 
	 * @return the range readings
	 */ 
	public RangeReadings getRangeValues();
}
