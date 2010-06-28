package lejos.robotics.proposal;

import lejos.robotics.proposal.*;

//import lejos.nxt.comm.RConsole;
import lejos.geom.Point;
import lejos.robotics.*;
import lejos.robotics.localization.*;

/**
 * Moves the robot to a destination.
 * Uses either a differenial pilot or a steering pilot
 * You can executed methods directly on the Pilot if you wish;
 * the Navitator pose will automatically be updated
 * @author Roger
 */
public class BasicNavigator
{
  public BasicNavigator(ArcMoveController  pilot, PoseProvider poseProvider )
  {  // toDo - modify to use MCLPoseProvider
    _pilot = pilot;
    if(poseProvider == null)
    this.poseProvider = new DeadReckonerPoseProvider((ArcMoveController)_pilot);
    else this.poseProvider =(MCLPoseProvider) poseProvider;
    _radius = _pilot.getMinRadius();
  }

  public void setPose(Pose aPose)
  {
    _pose = aPose;
  }
/**
 * Sets the initial pose of the robot;
 * @param x robot X location
 * @param y roboe Y location
 * @param heading robot initial heading
 */
  public void setInitialPose(Pose aPose, float headingNoise, float radiusNoise )
  {
    _pose = aPose;
    ((MCLPoseProvider)poseProvider).setInitialPose(_pose, headingNoise, radiusNoise);
  }

  /**
   * returns the current pose of the robot.
   * The pose is kept up to date by a DeadReckonerPoseProvider
   * @return
   */
  public Pose getPose( ){
    return poseProvider.getPose();
  }
  /**
   * returns a referenct to the pilot.
   * The Navigator pose will be automatically updated as a result of methods
   * executed on the pilot.
   * @return
   */
public ArcMoveController getPilot(){ return _pilot;}

/**
 * Moves the robot to the destinatin location
 * @param destination
 * @param immediateReturn  if true, this method will return as soon as the
 * rotation is complete;
 */
  public void goTo(Point destination, boolean immediateReturn)
  {
    _radius = _pilot.getMinRadius();
    _keepGoing = true;
    _destination = destination;
    _pose = poseProvider.getPose();
    float destinationRelativeBearing = normalize(_pose.relativeBearing(_destination));
    float distance = _pose.distanceTo(destination);
    if(_radius == 0)
    {
      ((RotateMoveController) _pilot).rotate(destinationRelativeBearing,true);
    }

    else performArc(destinationRelativeBearing,false);
    while(_pilot.isMoving() && _keepGoing)Thread.yield();
    _pose = poseProvider.getPose();
    distance = _pose.distanceTo(_destination);
    _pilot.travel(distance,true);
      while(_pilot.isMoving() && _keepGoing)Thread.yield();
    _pose = poseProvider.getPose();
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
 * go to the point
 * @param p
 */
  public void goTo(Point p )
  {
    goTo((float)p.getX(),(float)p.getY());
  }

  /**
   * Helper method for goTo() ; uses a simile altorithm for performing the
   * arc move to change direction before
   * the robot travels to the destination.  The default arc followed in the
   * forward direction.  If the destination is inside the default arc, this
   * method is called agin with the  second parameter set to <b>true</b>
   * @param destinationRelativeBearing
   * @param close  // true if the destination is inside of the default turning circle
   */
  protected void performArc(float destinationRelativeBearing,
          boolean  close )
  {
    if(destinationRelativeBearing == 0)return;
    int side = (int)Math.signum(destinationRelativeBearing);
    if (close) side *= -1;
    float xc,yc;   // turning center;
    float centerBearing = _pose.getHeading() + side*90;  // direction of center from robot
    centerBearing = (float) Math.toRadians(centerBearing);
    xc = _pose.getX() + _radius*(float)Math.cos(centerBearing);
    yc = _pose.getY() + _radius*(float)Math.sin(centerBearing);
    Point center = new Point(xc,yc);
    float centerToDestBearing = center.angleTo(_destination);
    float  destDistance = (float) center.distance(_destination);
//    float newHeading = 0;
    // acatually, the actual tangent is perpendicular to the tangent angle.
    float tangentAngle = (float)Math.toDegrees(Math.acos(_radius / destDistance));
    if( destDistance < _radius )
    {
      performArc(destinationRelativeBearing ,true); // use the center on the opposice side
      return;
    }
    else 
    {
    float newHeading  = centerToDestBearing + side* (90 - tangentAngle );
    _pilot.arc(side*_radius,newHeading - _pose.getHeading(),true);
    }
  }

   /**
   * Interrupts the current move and stops the robot
   */
  public void interrupt()
  {
    stop();
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
  public float normalize(float angle)
  {
    while(angle > 180 )angle -=  360;
    while(angle < -180) angle += 360;
    return angle;
  }
  /**
   *
   */
    protected boolean _keepGoing = false;
    protected ArcMoveController _pilot;
    protected PoseProvider poseProvider;
//    DeadReckonerPoseProvider poseProvider;
    protected Pose _pose = new Pose();
    protected Point _destination;
    protected float _radius;
}
