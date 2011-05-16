package lejos.robotics.navigation;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Interface for informing listeners that a way point has been reached.
 * 
 */
public interface WayPointListener
{
  /**
   * Called when the class providing waypoints has another waypoint to report.
   * @param wp the actual estimated pose of the robot when the waypoint has
   * been approximately reached.
   */
  public void nextWaypoint(WayPoint wp);
  
  /**
   * Called if/when the last Waypoint in the path has been sent.
   */
  public void pathComplete();  
}
