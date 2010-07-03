package lejos.robotics.proposal;


import java.util.ArrayList;
import lejos.robotics.proposal.*;
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
public interface PoseController
{
  /**
   * starts  the robot moving to the coordinates in the destination WayPoint.
   * @param destination
   */
  public void goTo(WayPoint destination);

   /**
    * Starts the robot moving to location specified by x and y coordinates.
    * if the immmediatereturn parameter is false, this method returns when the
    * arrives.
    * @param x coordinate of the destination
    * @param y coordinate of the testinati0n
    * @param immediateReturn  if<b>true</b> the method returns immediately, if
    */
  public void goTo(float x, float y, boolean immediateReturn);

  /**
   * goes to the sequence of waypoints contained in the route.
   * Informs its listeners of each waypoint reached. The waypoint is removed
   * from the queue when it is reached.
   * @param theRoute
   */
  public void followRoute(ArrayList<WayPoint> theRoute);
/**
 * stops the robot and empties the queue of waypoints
 */
  public void flushQueue(); 

  /**
   * stops the robot immediately; preserves the rest of the queue
   */
  public void interrupt();

/**
 * resumes following the route
 */
  public void resume();  //following the route after an interruption;

  /**
   * Adds a waypoint to the queue.  If the robot has stopped because the queue
   * is empty, it goes to this waypoint.
   * @param aWayPoint
   */
  public void addWaypoint(WayPoint aWayPoint); // adds a WayPoint to the route.

  /**
   * adds a waypoint listener.
   * @param aListener
   */
  public void addListener (WayPointListener aListener);

  /**
   * Note: There is no corresponding setPilot() method because the type of robot vehicle could
   * not change after the program starts, unless it was physically a transformer robot.
   * @return the pilot
   */
	public ArcMoveController getPilot();
		
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
