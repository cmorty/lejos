package lejos.robotics.proposal;

import lejos.robotics.Pose;
import lejos.robotics.localization.PoseProvider;
import lejos.geom.Point;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * 
 * The PoseController guides a Pilot to a destination. It can not plan a route, but will try 
 * to navigate to a set of coordinates and possibly avoid obstacles along the way.
 * 
 * The PoseController constructor very likely accepts a Pilot and PoseProvider.
 * 
 * @author NXJ Team
 *
 */
public interface PoseController {

	/**
	 * Travels to the coordinates in the destination Point.
	 * If it can't reach the destination, it tries to get as close as possible before giving up
	 * and reporting the actual coordinates (Pose) achieved.
	 * 
	 * @param destination
	 * @return The new pose it achieved.
	 */
	public Pose goTo(Point destination);
	
	/**
	 * Travels to the coordinates specified.
	 * If it can't reach the destination, it tries to get as close as possible before giving up
	 * and reporting the actual coordinates (Pose) achieved.
	 * 
	 * @param x the x coordinate of the target point
	 * @param y the y co-ordinate of the target point
	 * @return The new pose it achieved.
	 */
	public Pose goTo(float x, float y);
		
	// TODO: Add method to travel to waypoints in order?
	//public Pose goTo(Collection <Point> destination);
	
	/**
	 * Note: There is no corresponding setPilot() method because the type of robot vehicle could
	 * not change after the program starts, unless it was physically a transformer robot. 
	 * @return the pilot
	 */
	public ArcPilot getPilot();
		
	/**
	 * Get a reference to the PoseProvider being used as a localizer.
	 * @return the pose provider
	 */
	public PoseProvider getPoseProvider();
	
	/**
	 * Sets a new PoseProvider for the PoseController robot to use.
	 * 
	 * Example: If the robot moves from one environment (indoors) to another environment (outdoors) it might
	 * want to change to another method of localization if a change in environment is detected.
	 * 
	 * @param replacement the new PoseProvider
	 */
	public void setPoseProvider(PoseProvider replacement);
	
}
