package lejos.robotics.proposal;

import java.util.Collection;
import lejos.robotics.localization.PoseProvider;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
/**
 * 
 * The PoseController guides a Pilot to a destination. It can not plan a route, 
 * but will navigate to a set of coordinates and possibly avoid obstacles along the way.
 * Ir uses a collection of waypoints as a queue.
 * The PoseController constructor very likely accepts a Pilot and PoseProvider.
 * 
 * @author NXJ Team
 *
 */
public interface PoseController
{

  /**
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination WayPoint.
   * @param destination
   */
  public void goTo(WayPoint destination, boolean immediateReturn);

  /**
   * Moves the robot through the sequence of waypoints
   * contained in the route.
   * Informs its listeners of each waypoint reached. The waypoint is removed
   * from the route when it is reached.
   * This method returns immediately.
   * @param theRoute
   */
  public void followRoute(Collection<WayPoint> theRoute);

  /**
   * Moves the robot throuth the sequence of waypoints
   * contained in the route.
   * Informs its listeners of each waypoint reached. The waypoint is removed
   * from the route when it is reached.
   * @param theRoute
   * @param immediateReturn ;  if <b>false</b> the method returns when the route is empty
   */
  public void followRoute(Collection<WayPoint> theRoute, boolean immediateReturn);

  /**
   * stops the robot and empties the queue of waypoints (the route)
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

  public void addWaypoint(WayPoint aWayPoint); // adds a WayPoint to the route.

  /**
   * adds a waypoint listener.
   * @param aListener
   */
  public void addListener(WayPointListener aListener);

  /**
   * Note: There is no corresponding setPilot() method because the type of robot vehicle could
   * not change after the program starts, unless it was physically a transformer robot.
   * @return the pilot
   */
  public MoveController getPilot();

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
