package lejos.robotics.proposal;

import lejos.robotics.localization.PoseProvider;
import java.util.*;
import lejos.geom.Point;
import lejos.robotics.*;

/**
 * This class can cause the robot to follow a route - a sequence of way points ;
 * The way points are stored in a queue (actually, a Collection).
 * This  class uses  an inner class running its own thread to issue movement commands to its pilot.
 * It can use either a differential pilot or steering pilot.
 * It also uses a PoseProvider to keep its pose updated, and calls its Waypoint Listeners
 * when a way point is reached.
 * @author Roger Glassey
 */
public class BasicNavigator implements PoseController
{
	
  /**
   * Can use any pilot that implements the ArcMoveController interface
   * @param pilot
   */
  public BasicNavigator(MoveController pilot )
  {
    this(pilot,null);
  }

  public BasicNavigator(MoveController  pilot, PoseProvider poseProvider )
  {  // toDo - modify to use MCLPoseProvider
    _pilot = pilot;
    if(poseProvider == null)
      this.poseProvider = new DeadReckonerPoseProvider((ArcMoveController)_pilot);
//    else this.poseProvider =(MCLPoseProvider) poseProvider;
    
    _radius = (_pilot instanceof ArcMoveController ? ((ArcMoveController) _pilot).getMinRadius() : 0);
    _nav = new Nav();
    _nav.start();
  }  

  /**
   * This method is the same as followRoute(aRoute, true );
   * @param aRoute
   */
  public void followRoute(Collection<WayPoint>  aRoute)
  {
    followRoute( aRoute ,false);
  }

  public void followRoute(Collection<WayPoint>aRoute, boolean immediateReturn )
  {
    _route = (ArrayList<WayPoint>) aRoute;
    _keepGoing = true;
    if(immediateReturn)return;
    else while(_keepGoing) Thread.yield();
  }

  public void goTo(WayPoint destination, boolean immediateReturn)
  {
    addWaypoint(destination);
    if(!immediateReturn)
    {
      while(_keepGoing)Thread.yield();
    }
  }

  /**
   * Causes the robot move to the coordinates specified in the parameters
   * @param x coordinate of the destination
   * @param y coordinate of the destination
   * @param immediateReturn if<b>true<>/b> this method returns immediately
   */
  public void goTo(float x, float y, boolean immediateReturn)
  {
    goTo(new WayPoint(x,y),immediateReturn);
  }

  public void addListener(WayPointListener aListener)
  {
    if(listeners == null )listeners = new ArrayList<WayPointListener>();
    listeners.add(aListener);
  }

  /**
   * Sets the pose of the robot in the pose provider
   * @param x coordinate of the robot
   * @param y coordinate of the robot
   * @param heading  of the robot
   */
   public void setPose(float x, float y, float heading)
   {
     setPose(new Pose(x,y,heading));
   }
 
  /**
   * Sets the pose of the robot in the pose provider
   * @param pose
   */
  public void setPose(Pose pose)
  {
    _pose = pose;
    poseProvider.setPose(_pose);
  }

  /**
   * sets the heading of the robot in the pose provider
   * @param heading
   */
  public void setHeading(float heading)
  {
    setPose(_pose.getX(),_pose.getY(),heading);
  }

  /**
   * This method is for future use with  MCLPoseProvider
   * @param aPose : the initial pose
   * @param headingNoise
   * @param radiusNoise
   */
   public void setInitialPose(Pose aPose, float headingNoise, float radiusNoise )
   {
     _pose = aPose;
     poseProvider.setPose(_pose);
   }

  /**
   * Returns a reference to the pilot.
   * The Navigator pose will be automatically updated as a result of methods
   * executed on the pilot.
   * @return reference to the pilot
   */
  public MoveController getPilot(){ return _pilot;}

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

  public void resume()
  {
    if(_route.size() > 0 ) _keepGoing = true;
  }

  /**
   * Calls interrupt()
   */
  public void stop()
  {
    interrupt();
  }

  public void flushQueue()
  {
    _keepGoing = false;
    _pilot.stop();
    for(int i = _route.size()-1 ; i > 0; i++)_route.remove(i);
  }
  
  /**
   * Helper method for goTo() if a SteeringPilot is used ; uses a simple algorithm for performing the
   * arc move to change direction before
   * the robot travels to the destination.  The default arc is followed in the
   * forward direction.  If the destination is inside the default arc, this
   * method is called again with the  second parameter set to <b>true</b>
   * @param destinationRelativeBearing
   * @param close  <b> true </b>if the destination is inside of the default turning circle
   */
  protected void performArc(float destinationRelativeBearing,
          boolean  close)
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
    // actually, the actual tangent is perpendicular to the tangent angle.
    float tangentAngle = (float)Math.toDegrees(Math.acos(_radius / destDistance));
    if( destDistance < _radius )
    {
      performArc(destinationRelativeBearing ,true); // use the center on the opposite side
      return;
    }
    else 
    {
    float newHeading  = centerToDestBearing + side* (90 - tangentAngle );
    ((ArcMoveController) _pilot).arc(side*_radius,newHeading - _pose.getHeading(),true);
    }
  }

  /**
   * Returns the waypoint to which the robot is moving
   * @return the waypoint to which the robot is moving
   */
  public WayPoint getWaypoint()
  {
    if(_route.size() <= 0 ) return null;
    else return _route.get(0);
  }

  /**
   * Returns the current pose of the robot
   * @return the current pose of the robot
   */
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
   * Returns the equivalent angle between -180 and +180 degrees
   * @param angle
   * @return normalized angle
   */
   public float normalize(float angle)
   {
     while(angle > 180 )angle -=  360;
     while(angle < -180) angle += 360;
     return angle;
   }
 
  /**
   * This inner class runs the thread that processes the waypoint queue
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
          else
          {
  			// 1. Get shortest path:
  			Move [] moves;
  			float minRadius = (_pilot instanceof ArcMoveController ? ((ArcMoveController) _pilot).getMinRadius() : 0);
  			
  			if (_destination.headingRequired)
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), minRadius, _destination.getPose(),minRadius);
  			} 
  			else
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), _destination, minRadius);  				
  			}
  			// 2. Drive the path
  			for(int i=0;i<moves.length;i++) {
  				((ArcMoveController) _pilot).travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
  			}
          }
          //else performArc(destinationRelativeBearing,true);
          
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          
          if(!_keepGoing) break;
         
          _pose = poseProvider.getPose();
//           RConsole.println("after rotation " +((DifferentialPilot)_pilot).getAngleIncrement());

          if (_radius == 0)
          {
            float distance = _pose.distanceTo(_destination);
            _pilot.travel(distance, true);
          
            while (_pilot.isMoving() && _keepGoing)
            {
              Thread.yield();
            }
          
            if(!_keepGoing) break;
            
            _pose = poseProvider.getPose();
 
            if (_destination.headingRequired) {
            	_pose = poseProvider.getPose();
            	_destination.getHeading();
            	((RotateMoveController) _pilot).rotate(_destination.getHeading() - _pose.getHeading());
            }
          }         
          
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
  protected ArrayList<WayPoint>  _route  = new ArrayList<WayPoint>() ;
  protected ArrayList<WayPointListener>  listeners ;
  protected boolean _keepGoing = false;
  protected MoveController _pilot;
  public PoseProvider poseProvider;
//    DeadReckonerPoseProvider poseProvider;
  protected Pose _pose = new Pose();
  protected WayPoint _destination;
  protected float _radius;
}
