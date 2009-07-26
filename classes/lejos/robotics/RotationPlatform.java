package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

// TODO: For object completeness, this probably needs to be split into three interfaces: ElevationPlatform, RotationPlatform,
// and FullRotationPlatform which implements both interfaces. Some uses might only want the rotating left-right thing. 

/**
 * A platform for rotating a sensor and elevating the angle.
 * 
 * @author BB
 */

public interface RotationPlatform {
	
	/**
	 * Rotates the sensor to the desired angle. The direction is relative to the robot. The method will not return until the
	 * rotator has achieved the target angle.
	 * @param angle The angle, in degrees, to rotate to. 0 to 360, increasing counter clockwise from 0, the starting position.
	 */
	public void setDirection(int angle);

	/**
	 * Get the direction the sensor will face. The direction is relative to the robot.
	 * @return the relative direction of the sensor in degrees
	 * (0 to 360, increasing counter clockwise from 0, the starting position)
	 * 
	 */
	public int getDirection();
	
	/**
	 * Cables or other hindrances can restrict movement of the platform. This method indicates the maximum it can rotate left.   
	 * @return
	 */
	public int getMaximumDirection();
	
	/**
	 * Cables or other hindrances can restrict movement of the platform. This method indicates the maximum it can rotate right.
	 * @return
	 */
	public int getMinimumDirection();

	/**
	 * Cables or other hindrances can restrict movement of the platform. This method sets the maximum angle it can rotate left.
	 * @param maxAngle
	 */
	public void setMaximumDirection(int maxAngle);
	
	/**
	 * Cables or other hindrances can restrict movement of the platform. This method sets the maximum angle it can rotate right.
	 * @param minAngle
	 */
	public void setMinimumDirection(int minAngle);
	
	
	/**
	 *  Includes the direction of the pilot in the calculation of the real angle of the sensor 
	 *  (as opposed to the relative direction of the sensor)
	 *  
	 *  NOTE: There is no corresponding setAbsoluteDirection() method because the angle the Pilot
	 *  would rotate to and the angle the sensor would rotate to would be ambiguous. e.g. to rotate
	 *  the sensor 90 degrees, the Pilot could rotate 10 degrees and the sensor 80 degrees, or any
	 *  number of combinations to get 90 degrees.
	 *  
	 * @param pilot
	 * @return
	 */
	public int getAbsoluteDirection(Pose pose);
	
	/**
	 * Sets the speed the platform should rotate, in degrees per second. The default speed is XX degrees/second.
	 * @param speed
	 */
	public void setRotationSpeed(int speed);
	
	/**
	 * Gets the speed the platform rotates, in degrees per second.  The default speed is XX degrees/second.
	 * @return
	 */
	public int getRotationSpeed();
	
	/**
	 * Starts rotating counterclockwise (left). 
	 * Automatically stops when the angle matches or exceeds getMaximumDirection().
	 */
	public void scanLeft();
	
	/**
	 * Starts rotating to the clockwise (right). 
	 * Automatically stops when the angle matches or is less than getMinimumDirection().
	 */
	public void scanRight();
	
	/**
	 * Stops a rotation in motion from one of the rotate methods, like rotateLeft() or rotateUp().
	 */
	public void stopRotation();
	
	/**
	 * This method calibrates the direction and elevation to the zero angle position, which is pointing in the
	 * same direction as the robot and facing horizontal. If the sensor platform uses a touch sensor, light sensor or
	 * some other sensor to calibrate the position, this can be used in conjunction with motor movements to set the 
	 * position to the zero position, then reset the angles to zero. Otherwise this method relies on the user to
	 * set the sensor platform to the zero position manually by hand.   
	 * 
	 * @return true if calibration worked, false if it failed.
	 */
	public boolean zero();
	
	/**
	 * The actual central axis of the sensor rotator will reside off center from the actual robot center.
	 * So we could include methods to set the x offset, y offset and z offset from the center of robot 
	 * (center = point between wheels).
	 */
	public void setXOffset();
	public void setYOffset();
	public void setZOffset();
	
	public float getXOffset();
	public float getYOffset();
	public float getZOffset();
}
