package lejos.robotics.navigation;

import lejos.robotics.localization.DeadReckonerPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.pathfinding.PathFinder;

import java.util.*;
import lejos.robotics.*;

/**
 * This class can cause the robot to follow a route - a sequence of way points ;
 * The way points are stored in a queue (actually, a Collection).
 * This  class uses  an inner class running its own thread to issue movement commands to its pilot.
 * It can use either a differential pilot or steering pilot.
 * It also uses a PoseProvider to keep its pose updated, and calls its WayPoint Listeners
 * when a way point is reached.
 * 
 * @author Roger Glassey
 */
public class NavPathController implements PathController
{
	
  /**
   * Can use any pilot that implements the ArcMoveController interface. 
   * @param pilot
   */
  public NavPathController(MoveController pilot )
  {
    this(pilot,null);
  }
  
  /**
   * Creates a PathController using a custom poseProvider, rather than the default tachometer pose
   * provider.
   * @param pilot
   * @param poseProvider
   */
  public NavPathController(MoveController  pilot, PoseProvider poseProvider )
  {
    _pilot = pilot;
    if(poseProvider == null)
      this.poseProvider = new DeadReckonerPoseProvider((ArcMoveController)_pilot);
    else
    	this.poseProvider = poseProvider;
    
    _radius = (_pilot instanceof ArcMoveController ? ((ArcMoveController) _pilot).getMinRadius() : 0);
    _nav = new Nav();
    _nav.setDaemon(true);
    _nav.start();
  }  

  /**
   * Creates a PathController which will navigate to a point via goTo() using a PathFinder to provide 
   * assistance to create a path. 
   * @param pilot
   * @param poseProvider
   * @param pathFinder
   */
   public NavPathController(MoveController pilot, PoseProvider poseProvider, PathFinder pathFinder) {
	  this(pilot, poseProvider);
	  setPathFinder(pathFinder);
  }
  
  public void setPathFinder(PathFinder pathFinder) {
	  this.pathFinder = pathFinder; 
	  final PathController pc = this; // Grab reference to NavPathController object for inner class
	  pathFinder.addListener(new WayPointListener() {

		  public void nextWaypoint(WayPoint wp) {
			  pc.addWayPoint(wp);
		  }

		  public void pathComplete() {
			  // Nothing to do. PathController keeps waiting for more Waypoints in queue.
		  }
	  });	  
  }
  
  public PathFinder getPathFinder() {
	  return pathFinder;
  }
  
  /** returns <code> false </code> if the the final waypoint has been reached or interrupt() has been called
   */
  public boolean isGoing()
  {
	  return _keepGoing;
  }
  
  public void followRoute(Collection<WayPoint>aRoute, boolean immediateReturn )
  {
    _route = (ArrayList<WayPoint>) aRoute;
    _keepGoing = true;
    if(immediateReturn)return;
    else while(_keepGoing) Thread.yield();
  }
 
  /**
   * This method will navigate to a point. If a PathFinder was used in the constructor, it will rely
   * on it to calculate a series of waypoints to get to the destination.
   */
  public void goTo(WayPoint destination, boolean immediateReturn)
  {
    // Check if using PathFinder:
	if(pathFinder == null) 
		addWayPoint(destination);
	else
		pathFinder.startPathFinding(poseProvider.getPose(), destination);
    
	if(!immediateReturn){
      while(_keepGoing)Thread.yield();
    }
  }

  /**
   * This method will navigate to a point. If a PathFinder was used in the constructor, it will rely
   * on it to calculate a series of waypoints to get to the destination.
   */
  public void goTo(WayPoint destination) {
   
	  goTo(destination, false);

  }

  /**
   * This method will navigate to a point. If a PathFinder was used in the constructor, it will rely
   * on it to calculate a series of waypoints to get to the destination.
   */
  public void goTo(float x, float y) {
	  goTo(new WayPoint(x, y));
  }

  public void addListener(WayPointListener aListener)
  {
    if(listeners == null )listeners = new ArrayList<WayPointListener>();
    listeners.add(aListener);
  }
  
  public void addTargetListener(WayPointListener targetListener)
  {
    if(targetListeners == null )targetListeners = new ArrayList<WayPointListener>();
    targetListeners.add(targetListener);
  }

  /**
   * Returns a reference to the MoveController.
   * The Navigator pose will be automatically updated as a result of methods
   * executed on the MoveController.
   * @return reference to the MoveController
   */
  public MoveController getMoveController(){ return _pilot;}

  public void addWayPoint(WayPoint aWayPoint)
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

  public void flushQueue()
  {
    _keepGoing = false;
    _pilot.stop();
    for(int i = _route.size()-1 ; i > 0; i++)_route.remove(i);
  }

  /**
   * Returns the waypoint to which the robot is moving
   * @return the waypoint to which the robot is moving
   */
  public WayPoint getWayPoint()
  {
    if(_route.size() <= 0 ) return null;
    else return _route.get(0);
  }

  public void setPoseProvider(PoseProvider aProvider)
  {
    poseProvider = aProvider;
  }

  public PoseProvider getPoseProvider()
  {
    return poseProvider;
  }
  
  public void waitForDestinationReached() {
	  while (_keepGoing) Thread.yield();
  }
 
  /**
   * This inner class runs the thread that processes the waypoint queue
   */
  protected  class Nav extends Thread
  {
    boolean more = true;

    public void run()
    { 
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
          
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          
          if(!_keepGoing) break;
         
          _pose = poseProvider.getPose();

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
              l.nextWaypoint(new WayPoint(poseProvider.getPose()));
          }
          
          if(targetListeners != null)
          { 
            for(WayPointListener l : targetListeners)
              l.nextWaypoint(_destination);
          }
                    
          if (_keepGoing && 0 < _route.size()) {_route.remove(0);}
          _keepGoing = _keepGoing && 0 < _route.size();
          Thread.yield();
        } // end while keepGoing
        Thread.yield();
      }  // end while more
    }  // end run
  } // end Nav class

  protected Nav _nav ;
  protected ArrayList<WayPoint> _route  = new ArrayList<WayPoint>() ;
  protected ArrayList<WayPointListener> listeners;
  protected ArrayList<WayPointListener> targetListeners;
  protected boolean _keepGoing = false;
  protected MoveController _pilot;
  protected PoseProvider poseProvider;
  protected PathFinder pathFinder = null;
  protected Pose _pose = new Pose();
  protected WayPoint _destination;
  protected float _radius;
}
