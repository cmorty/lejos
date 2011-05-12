package lejos.robotics.localization;

import lejos.geom.Point;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
/**
 * <p>A PoseProvider that keeps track of coordinates using odometry (dead reckoning), by monitoring Pilot movements.
 * The method a pilot uses to keep track of movements is irrelevant to this class. For example, it can keep track of 
 * movements using optical rotation sensors in the motors, or it could use something more exotic such as 
 * <a href="http://en.wikipedia.org/wiki/Visual_odometry">visual odometry</a> used by the 
 * <a href="http://en.wikipedia.org/wiki/Mars_Exploration_Rover">MER</a>.  
 * 
 */
// TODO: Probably makes more sense to use inner class implementation of MoveListener?
public class OdometryPoseProvider implements PoseProvider, MoveListener
{

  private float x = 0, y = 0, heading = 0;
  private float angle0, distance0;
  MoveProvider mp;
  boolean current = true;

  /**
   * Internally, the constructor  listens to movements from the Pilot. This allows it to keep
   * track of all vector movements made.
   *
   * @param mp the movement provider
   */
  public OdometryPoseProvider(MoveProvider mp)
  {
    mp.addMoveListener(this);
  }

  /**
   * returns  a new pose that represents the current location and heading of the robot
   * @return pose
   */
  public Pose getPose()
  {
    if (!current )
    {
      updatePose(mp.getMovement());
    }
    return new Pose(x, y, heading);
  }

  /**
   * called by a MoveProvider when movement starts
   * @param event - the event that just started
   * @param mp the MoveProvider that called this method
   */
  public void moveStarted(Move event, MoveProvider mp)
  {
    angle0 = 0;
    distance0 = 0;
    current = false;
    this.mp = mp;
  }
  
  public void setPose(Pose aPose )
  {
    setPosition(aPose.getLocation());
    setHeading(aPose.getHeading());
  }
  
  /**
   * called by a MoveProvider when movement ends
   * @param event - the event that just started
   * @param mp
   */
  public void moveStopped(Move event, MoveProvider mp)
  {
    updatePose(event);
  }

  /*
   * Update the pose with the incremental movement that has occurred since the
   * movementStarted 
   */
  private void updatePose(Move event)
  {
    float angle = event.getAngleTurned() - angle0;
    float distance = event.getDistanceTraveled() - distance0;
    double dx = 0, dy = 0;
    double headingRad = (Math.toRadians(heading));

    if (event.getMoveType() == Move.MoveType.TRAVEL   || Math.abs(angle)<0.2f)
    {
      dx = (distance) * (float) Math.cos(headingRad);
      dy = (distance) * (float) Math.sin(headingRad);
    }
    else if(event.getMoveType() == Move.MoveType.ARC)
    {
      double turnRad = Math.toRadians(angle);
      double radius = distance / turnRad;
      dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
      dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));
    }
    x += dx;
    y += dy;
    heading = normalize(heading + angle); // keep angle between -180 and 180
    angle0 = event.getAngleTurned();
    distance0 = event.getDistanceTraveled();
    current = !event.isMoving();
  }

  /*
   * returns equivalent angle between -180 and +180
   */
  private float normalize(float angle)
  {
    float a = angle;
    while (a > 180)a -= 360;
    while (a < -180) a += 360;
    return a;
  }

  private void setPosition(Point p)
  {
    x = p.x;
    y = p.y;
    current = true;
  }

  private void setHeading(float heading)
  {
    this.heading = heading;
    current = true;
  }
}
