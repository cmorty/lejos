package lejos.robotics.proposal;

import lejos.robotics.localization.PoseProvider;
import java.util.*;
import lejos.geom.Point;
import lejos.robotics.*;

/**
 *This  class can follow a sequence of way points;
 *Uses an inner class that has it own thresd to do the work.
 * It can use either a differential pilot or steering pilot.
 * Uses a PoseController to keep its pose updated, and calls its Waypoint Listeners
 * when a way point is reached.
 * @author Roger
 */
public class BasicNavigator   implements PoseController
{
/**
 * can use any pilot the impolements the MoveControl interrface
 * @param pilot
 */
  public BasicNavigator(ArcMoveController pilot )
  {
    this(pilot,null);
  
  }
public BasicNavigator(ArcMoveController  pilot, PoseProvider poseProvider )
  {  // toDo - modify to use MCLPoseProvider
    _pilot = pilot;
    if(poseProvider == null)
    this.poseProvider = new DeadReckonerPoseProvider((ArcMoveController)_pilot);
//    else this.poseProvider =(MCLPoseProvider) poseProvider;
    _radius = _pilot.getMinRadius();
    _nav = new Nav();
    _nav.start();
  }
  public void followRoute(ArrayList<WayPoint>  aRoute)
  {
    followRoute( aRoute ,false);
  }

  
  public void followRoute(ArrayList<WayPoint>aRoute, boolean immediateReturn )
  {
    _route = aRoute;
    _keepGoing = true;
    if(immediateReturn)return;
    else while(_keepGoing) Thread.yield();
  }


public void goTo(WayPoint destination)
{
//  RConsole.println("goTo "+destination);
  addWaypoint(destination);
}

public void goTo(float x, float y, boolean immediateReturn)
{
  goTo(new WayPoint(x,y));
  if(!immediateReturn)
  {
    while(!_keepGoing)Thread.yield();
  }
}

/**
 * Add a WayPointListener
 * @param aListener
 */
  public void addListener(WayPointListener aListener)
  {
    if(listeners == null )listeners = new ArrayList<WayPointListener>();
    listeners.add(aListener);
  }

   public void setPose(float x, float y, float heading)
  {
    setPose(new Pose(x,y,heading));
  }

  public void setPose(Pose pose)
  {
    _pose = pose;
    poseProvider.setPose(_pose);
  }
  public void setHeading(float heading)
  {
    setPose(_pose.getX(),_pose.getY(),heading);
  }

/**
 * Betin following the route  Can be a non-blocking method
 * @param aRoute sequemce of way points to be visited
 * @param immediateReturn if true, returns immidiately
 */
  public void setInitialPose(Pose aPose, float headingNoise, float radiusNoise )
  {
    _pose = aPose;
    poseProvider.setPose(_pose);
  }

  /**
   * returns a referenct to the pilot.
   * The Navigator pose will be automatically updated as a result of methods
   * executed on the pilot.
   * @return
   */
public ArcMoveController getPilot(){ return _pilot;}

  /**
   * add a WayPoint to the route array. If the robot is not moving, it will
   * start following the route.
   * @param aWayPoint
   */
  public void addWaypoint(WayPoint aWayPoint)
  {

    _route.add(aWayPoint);
    _keepGoing = true;
  }
public void interrupt()
{
  _keepGoing = false;
  _pilot.stop();
}

/**
 * Resume the route after an interrupt
 */
  public void resume()
  {
    if(_route.size() > 0 ) _keepGoing = true;
  }


  /**
   * calls interrupt()
   */
  public void stop()
  {
    interrupt();
  }
  /**
   * Stop the robot and emptay the  queue
   */
  public void flushQueue()
  {
    _keepGoing = false;
    _pilot.stop();
    for(int i = _route.size()-1 ; i > 0; i++)_route.remove(i);
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
   * Returns the  waypoint to which the robot is moving
   * @return
   */
public WayPoint getWaypoint()
{
  if(_route.size() <= 0 ) return null;
  else return _route.get(0);
}

public Pose getPose()
{
  return poseProvider.getPose();
}
public void setPoseProvider(PoseProvider aProvider)
{
  poseProvider = aProvider;
}

public PoseProvider getPoseProvider()
{
  return poseProvider;
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
 *this inner class runs the thread that processes the waypoint queue
 */
   protected  class Nav extends Thread
  {
    boolean more = true;

    public void run()
    { 
      setDaemon(true);
      while (more)
      {
        while (_keepGoing)// && _route != null && _route.size()>0)
        { 
          _destination = _route.get(0);
          _pose = poseProvider.getPose();
          float destinationRelativeBearing = _pose.relativeBearing(_destination);
         if(!_keepGoing) break;
           if(_radius == 0)
    {
      ((RotateMoveController) _pilot).rotate(destinationRelativeBearing);
    }
           else performArc(destinationRelativeBearing,true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
         
           _pose = poseProvider.getPose();
//           RConsole.println("after rotation " +((DifferentialPilot)_pilot).getAngleIncrement());
          float distance = _pose.distanceTo(_destination);
           if(!_keepGoing) break;
          _pilot.travel(distance, true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          if(!_keepGoing) break;
          _pose = poseProvider.getPose();
          if(listeners != null)
          { 
            for(WayPointListener l : listeners)
              l.atWayPoint(poseProvider.getPose());
          }
          if (_keepGoing && 0 < _route.size()) {_route.remove(0);}
          _keepGoing = _keepGoing && 0 < _route.size();
          Thread.yield();
        } // end while keepGoing
        Thread.yield();
      }  // end while more
    }  // end run
  } // end Nav class

//   int _count = 0;
   protected Nav _nav ;
   protected ArrayList<WayPoint>  _route  = new ArrayList() ;
   protected ArrayList<WayPointListener>  listeners ;
  protected boolean _keepGoing = false;
    protected ArcMoveController _pilot;
    public PoseProvider poseProvider;
//    DeadReckonerPoseProvider poseProvider;
    protected Pose _pose = new Pose();
    protected Point _destination;
    protected float _radius;

}
