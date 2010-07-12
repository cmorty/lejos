package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 *
 * @author roger
 */
public interface WayPointListener
{
  /**
   * @param thePose :the actual estimated pose  of the robot when the waypoion has
   * been approximately reached.
   */
  public  void  atWayPoint(Pose thePose);
}
