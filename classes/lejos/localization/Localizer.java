package lejos.localization;

import lejos.navigation.*;

/**
 * Extension to Navigator for robot that supports localization
 * 
 * @author Lawrie Griffiths
 *
 */
public interface Localizer extends Navigator {
	/**
	 * Take a set of readings to the nearest map features
	 */
	public void takeReadings();
	
	/**
	 * Get the estimated pose of the robot
	 * 
	 * @return the pose
	 */
	public Pose getEstimatedPose();
}
