package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 * (Cut and paste discussion list info here)
 * PathPlanners very likely accept a Pilot and PoseProvider in the constructor.
 * @author NXJ Team
 *
 */
public interface PathPlanner {

	/**
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
	 * Example: If the robot moves from one environment (indoors) to another environment (outdoors) it might
	 * want to change to another method of localization if a change in environment is detected.
	 * @param poseProvider
	 */
	public void setPoseProvider(PoseProvider replacement);
	
}
