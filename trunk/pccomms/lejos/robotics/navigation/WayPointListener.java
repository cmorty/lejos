package lejos.robotics.navigation;

import lejos.robotics.Pose;

/**
 * Interface for informing listeners that a way point has been reached.
 * 
 * @author Roger Glassey
 */
public interface WayPointListener
{
  /**
   * @param thePose the actual estimated pose of the robot when the waypoint has
   * been approximately reached.
   */
  public void atWayPoint(Pose thePose);
}
