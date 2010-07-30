package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 *
 * @author Roger Glassey
 */
public interface WayPointListener
{
  /**
   * @param thePose :the actual estimated pose of the robot when the waypoint has
   * been approximately reached.
   */
  public void atWayPoint(Pose thePose);
}
