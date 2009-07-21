package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 * PathFinders very likely accept a Pilot and PoseProvider in the constructor.
 * 
 * With stacking logic, Lawrie mentioned: "This logic needs to be invoked from all methods that subgoals 
 * of a higher level path finder might use, not just the goTo method. If the higher level called a pilot 
 * directly, this obstacle avoidance logic would need to be duplicated at the higher level."
 * 
 * Lawrie: "...when I investigated stacking path finders, it seemed that for some path finding algorithms, 
 * the navigation strategy needed to be implemented by at least some duplicated pilot methods and not just 
 * goTo in order for higher level path finders to use it effectively. See more on this below."
 * 
 * So it seems like he's saying BLOCKING needs to take place. In other words, the PathFinder subclass overrides the 
 * methods in the superclass.  
 * 
 * Lawrie: "I was more thinking that even though the overall goal of the path finder 
 * is to go to (x,y), it splits this up into a set of subgoals and those subgoals can be to follow 
 * a wall for a while, follow a line for a while, steer round a map feature etc. While it is doing 
 * that, it still wants to implement the lower level navigation strategies of stacked path finders 
 * like dynamic obstacle avoidance and avoiding  falling off cliffs."
 * 
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
