package lejos.robotics.proposal;


import lejos.geom.Point;
import lejos.robotics.*;

/**
 * Moves the robot to a destination.
 * Uses any pilot that implements the MoveControl face.
 * You can executed methods on the Pilot if you wish; the Navitator pose will
 * automatically updated.
 * @author Roger
 */

public class BasicNavigator
{
  public BasicNavigator(ArcRotateMoveController  pilot )
  {
    _pilot = pilot;
    drpp = new DeadReckonerPoseProvider((DifferentialPilot)_pilot);
  }
/**
 * Sets the initial pose of the robot;
 * @param x robot X location
 * @param y roboe Y location
 * @param heading robot initial heading
 */
  public void setInitialPose(float x, float y, float heading)
  {
    _pose = new Pose(x,y,heading);
  }

  /**
   * returns the current pose of the robot.
   * The pose is kept up to date by a DeadReckonerPoseProvider
   * @return
   */
  public Pose getPose( ){
    return drpp.getPose();
  }
  /**
   * returns a referenct to the pilot.
   * The Navigator pose will be automatically updated as a result of methods
   * executed on the pilot.
   * @return
   */
public ArcRotateMoveController getPilot(){ return _pilot;}

/**
 * Moves the robot to the destinatin location
 * @param destination
 * @param immediateReturn  if true, this method will return as soon as the
 * rotation is complete;
 */
  public void goTo(Point destination, boolean immediateReturn)
  {
    _keepGoing = true;
    _destination = destination;
    _pose = drpp.getPose();
    float angle = _pose.relativeBearing(_destination);
    _pilot.rotate(normalize(angle));
    float distance = _pose.distanceTo(_destination);
    _pilot.travel(distance, immediateReturn);
    _pose = drpp.getPose();
  }

  /**
   * Goes to a location specified by the x,y coordinates
   * @param x
   * @param y
   * @param immediateReturn
   */
  public void goTo(float x, float y, boolean immediateReturn)
  {
    goTo(new Point(x,y),immediateReturn);
  }

  /**
   * goes to location specified by x and y coordinates.  Returns when it gets there.
   * @param x
   * @param y
   */
  public void goTo(float x, float y)
  {
    goTo(x,y,false);
  }
/**
 * Stops the robot immediately.
 */
  public void stop()
  {
    _keepGoing = false;
    _pilot.stop();
  }
/**
 * returns the equivalent angle between -180 and +180 degrees
 * @param angle
 * @return
 */
  float normalize(float angle)
  {
    while(angle > 180 )angle -=  360;
    while(angle < -180) angle += 360;
    return angle;
  }
  /**
   *
   */
    public boolean _keepGoing = false;
    protected ArcRotateMoveController _pilot;
    protected DeadReckonerPoseProvider drpp;
    protected Pose _pose = new Pose();
    protected Point _destination;
}
