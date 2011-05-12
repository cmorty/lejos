package lejos.robotics.navigation;

import java.util.Collection;
import lejos.robotics.localization.PoseProvider;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * 
 * The PathController guides a MoveController  to a destination. It can not plan a route,
 * but will navigate to a set of coordinates and possibly avoid obstacles along the way.
 * It uses a collection of waypoints as a queue.
 * The PathController constructor very likely accepts a MoveController and PoseProvider.
 * 
 * @author NXJ Team
 *
 */
public interface PathController
{

  /**
   * This method causes the robot to travel to a new location.
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination {@link lejos.robotics.navigation.WayPoint}
   * @param destination the destination {@link lejos.robotics.navigation.MoveController}
   * @param immediateReturn true for non-blocking, false for blocking
   */
  public void goTo(WayPoint destination, boolean immediateReturn);

  /**
   * This method causes the robot to travel to a new location.
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination WayPoint.
   * @param destination the destination waypoint
   */
  public void goTo(WayPoint destination);
  
  /**
   * This method causes the robot to travel to a new location.
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination WayPoint.
   * @param x The x coordinate
   * @param y The y coordinate
   */
  public void goTo(double x, double y);
  
  /**
   * Moves the robot through the sequence of waypoints
   * contained in the route.
   * Informs its listeners of each waypoint reached. The waypoint is removed
   * from the route when it is reached.
   * @param theRoute
   * @param immediateReturn ;  if <b>false</b> the method returns when the route is empty
   */
  public void followRoute(Collection<WayPoint> theRoute, boolean immediateReturn);

  /**
   * Stops the robot and empties the queue of waypoints (the route)
   */
  public void flushQueue();

  /**
   * Stops the robot immediately; preserves the rest of the queue
   */
  public void interrupt();

  /**
   * Resumes following the route
   */
  public void resume();  //following the route after an interruption;

  /**
   * Adds a WayPoint to the route.  If the route was empty, the robot immediately
   * starts moving toward the Waypoint
   * @param aWayPoint
   */
  public void addWayPoint(WayPoint aWayPoint); // adds a WayPoint to the route.

  /**
   * Adds a {@link lejos.robotics.navigation.WayPointListener}
   * that will be notified with the actual waypoint it reached.
   * @param aListener
   */
  public void addListener(WayPointListener aListener);

  /**
   * Adds a waypoint listener that will be notified with the theoretical target waypoint it reached.
   * @param targetListener
   */
  public void addTargetListener(WayPointListener targetListener);
  
  /**
   * Note: There is no corresponding setMoveController() method because the type of robot vehicle could
   * not change after the program starts, unless it was physically a transformer robot.
   * @return the MoveController
   */
  public MoveController getMoveController();

  /**
   * Get a reference to the PoseProvider being used as a localizer.
   * @return the pose provider
   */
  public PoseProvider getPoseProvider();

  /**
   * Sets a new PoseProvider for the PathController robot to use.
   *
   * Example: If the robot moves from one environment (indoors) to another environment (outdoors) it might
   * want to change to another method of localization if a change in environment is detected.
   *
   * @param aProvider the new PoseProvider
   */
  public void setPoseProvider(PoseProvider aProvider);
}
