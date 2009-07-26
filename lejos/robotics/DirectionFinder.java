package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for compasses and other devices than return the heading of a robot.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface DirectionFinder {
	/**
	 * Compass readings increase clockwise from 0 to 360, but Cartesian
	 * coordinate systems increase counter-clockwise. This method returns
	 * the Cartesian compass reading. Also, the resetCartesianZero() method
	 * can be used to designate any direction as zero, rather than relying
	 * on North as being zero.
	 * @return Cartesian direction.
	 */
	public float getDegreesCartesian();
	
	/**
	 * Starts calibration.
	 * Must call stopCalibration() when done.
	 */
	public void startCalibration();
	
	/**
	 * Ends calibration sequence.
	 */
	public void stopCalibration();
	
	/**
	 * Changes the current direction the compass is facing into the zero 
	 * angle. 
	 */
	public void resetCartesianZero();
}
