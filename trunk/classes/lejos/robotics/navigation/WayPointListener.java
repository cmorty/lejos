package lejos.robotics.navigation;

/**
 * Interface for informing listeners that a way point has been reached.
 * 
 */
public interface WayPointListener
{
  /**
   * Called when the class providing waypoints has another waypoint to report.
   * @param thePose the actual estimated pose of the robot when the waypoint has
   * been approximately reached.
   */
  public void nextWaypoint(WayPoint wp);
  
  /**
   * Called if/when the last Waypoint in the path has been sent.
   */
  public void pathComplete();  
}
