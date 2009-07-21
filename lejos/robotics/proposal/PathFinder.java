package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 * PathFinders very likely accept a Pilot and PoseProvider in the constructor.
 * 
 * @author NXJ Team
 *
 */
public interface PathFinder {

	/**
	 * Travels to the coordinates in the destination Pose, including the new heading in Pose?
	 * If it can't reach the destination, it tries to get as close as possible before giving up
	 * and reporting the actual coordinates (Pose) achieved.
	 * TODO: Maybe int x, int y coordinates would be better?
	 * 
	 * @param destination
	 * @return The new pose it achieved.
	 */
	public Pose goTo(Pose destination);
		
	public Pose goTo(Pose destination, boolean closestPoint);
	
	/**
	 * Note: There is no corresponding setPilot() method because the type of robot vehicle could
	 * not change after the program starts, unless it was physically a transformer robot. 
	 * @return
	 */
	public Pilot getPilot();
		
	/**
	 * Get a reference to the PoseProvider being used as a localizer.
	 * @return
	 */
	public PoseProvider getPoseProvider();
	
	/**
	 * Sets a new PoseProvider for the PathFinder robot to use.
	 * 
	 * Example: If the robot moves from one environment (indoors) to another environment (outdoors) it might
	 * want to change to another method of localization if a change in environment is detected.
	 * @param poseProvider
	 */
	public void setPoseProvider(PoseProvider replacement);
	
}
