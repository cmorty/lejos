package lejos.robotics.navigation;

import java.util.Collection;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.pathfinding.Path;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * 
 * The PathController guides a MoveController  to a destination. It can not plan a route,
 * but will navigate to a set of coordinates and possibly avoid obstacles along the way.
 * It uses a collection of Waypoints as a queue.
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
   * destination {@link lejos.robotics.navigation.Waypoint}
   * @param destination the destination {@link lejos.robotics.navigation.MoveController}
   * @param immediateReturn true for non-blocking, false for blocking
   */
  public void goTo(Waypoint destination, boolean immediateReturn);

  /**
   * This method causes the robot to travel to a new location.
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination Waypoint.
   * @param destination the destination Waypoint
   */
  public void goTo(Waypoint destination);
  
  /**
   * This method causes the robot to travel to a new location.
   * If the robot is moving, it stops,  the route is emptied and the destination
   * is added to it. The robot then starts  moving to the coordinates of the
   * destination Waypoint.
   * @param x The x coordinate
   * @param y The y coordinate
   */
  public void goTo(double x, double y);
  
  /**
   * This method will navigate to a point. If a PathFinder was used in the constructor, it will rely
   * on it to calculate a series of Waypoints to get to the destination.
   * @param x The x coordinate
   * @param y The y coordinate
   * @param heading The target angle to arrive at (in degrees)
   */
  public void goTo(double x, double y, double heading);
  
  /**
   * Moves the robot through the sequence of Waypoints
   * contained in the route.
   * Informs its listeners of each Waypoint reached. The Waypoint is removed
   * from the route when it is reached.
   * @param theRoute
   * @param immediateReturn ;  if <b>false</b> the method returns when the route is empty
   */
  public void followRoute(Path theRoute, boolean immediateReturn);

  /**
   * Stops the robot and empties the queue of Waypoints (the route)
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
   * Adds a Waypoint to the route.  If the route was empty, the robot immediately
   * starts moving toward the Waypoint
   * @param aWaypoint
   */
  public void addWaypoint(Waypoint aWaypoint); // adds a Waypoint to the route.

  /**
   * Adds a {@link lejos.robotics.navigation.WaypointListener}
   * that will be notified with the actual Waypoint it reached.
   * @param aListener
   */
  public void addListener(WaypointListener aListener);

  /**
   * Adds a Waypoint listener that will be notified with the theoretical target Waypoint it reached.
   * @param targetListener
   */
  public void addTargetListener(WaypointListener targetListener);
  
  /**
   * <p>Returns a reference to the MoveController. The Navigator pose will be automatically updated 
   * as a result of methods executed on the MoveController.</p>
   * 
   * <p>Note: There is no corresponding setMoveController() method because the type of robot vehicle 
   * could not change after the program starts, unless it was physically a transformer robot.</p>
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
