package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public interface ElevationPlatform {

	/**
	 * Raises or lowers the sensor elevation.
	 * @param angle getMaximumElevation() to getMinimumElevation() +90 to -90, or if platform is incapable of this movement
	 */
	public void setElevation(int angle);

	/**
	 * Returns the current sensor elevation angle.
	 * @return
	 */
	public int getElevation();
	
	/**
	 * Maximum angle the elevation can be raised. Should be no larger than 90 degrees.
	 * @return
	 */
	public int getMaximumElevation();
	
	/**
	 * Minimum angle the elevation can be lowered to. Should be no less than -90 degrees.
	 * @return
	 */
	public int getMinimumElevation();
	
	/**
	 * Sets the maximum angle the elevation can be raised. 
	 * @param maxAngle Should be no greater than +90 degrees.
	 */
	public void setMaximumElevation(int maxAngle);
	
	/**
	 * Sets the minimum angle the elevation can be raised. 
	 * @param minAngle Should be no less than -90 degrees.
	 */
	public void setMinimumElevation(int minAngle);

	/**
	 * Sets the speed the platform should rotate, in degrees per second. The default speed is XX degrees/second.
	 * @param speed
	 */
	public void setElevationSpeed(int speed);
	
	/**
	 * Gets the speed the platform rotates, in degrees per second.  The default speed is XX degrees/second.
	 * @return
	 */
	public int getElevationSpeed();
		
	public void scanUp();

	public void scanDown();

	/**
	 * Stops a scan in motion from scanUp() or scanDown().
	 */
	public void stopElevation();

	
}
