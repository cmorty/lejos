package lejos.robotics.navigation;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.pathfinding.PathFinder;

import java.util.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class can cause the robot to follow a route - a sequence of  {@link  lejos.robotics.navigation.Waypoint }
 * ;
 * The way points are stored in a queue (actually, a Collection).
 * This  class uses  an inner class running its own thread to issue movement commands to its
 * {@link lejos.robotics.navigation.MoveController},
 * which can be either a  {@link lejos.robotics.navigation.DifferentialPilot}
 * or {@link lejos.robotics.navigation.SteeringPilot}.
 * It also uses a {@link lejos.robotics.localization.PoseProvider}
 * to keep its pose updated, and calls its {@link lejos.robotics.navigation.WaypointListener}
 * when a way point is reached.
 * 
 * @author Roger Glassey
 */
public class Navigator implements PathController
{
	
  /**
   * Can use any pilot that implements the ArcMoveController interface. 
   * @param pilot
   */
  public Navigator(MoveController pilot )
  {
    this(pilot,null);
  }
  
  /**
   * Creates a PathController using a custom poseProvider, rather than the default
   * OdometryPoseProvider.  
   * @param pilot
   * @param poseProvider
   */
  public Navigator(MoveController  pilot, PoseProvider poseProvider )
  {
    _pilot = pilot;
    if(poseProvider == null)
      this.poseProvider = new OdometryPoseProvider(_pilot);
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
   public Navigator(MoveController pilot, PoseProvider poseProvider, PathFinder pathFinder) {
	  this(pilot, poseProvider);
	  setPathFinder(pathFinder);
  }
  
  // TODO: Should this method be part of PathController interface too?
  public void setPathFinder(PathFinder pathFinder) {
	  this.pathFinder = pathFinder; 
	  final PathController pc = this; // Grab reference to Navigator object for inner class
	  pathFinder.addListener(new WaypointListener() {

		  public void nextWaypoint(Waypoint wp) {
			  pc.addWayPoint(wp);
		  }

		  public void pathComplete() {
			  // Nothing to do. PathController keeps waiting for more Waypoints in queue.
		  }
	  });	  
  }
  
//TODO: Should this method be part of PathController interface too?
  public PathFinder getPathFinder() {
	  return pathFinder;
  }

  /**
   * @param aRoute 
   */
  public void setRoute(Collection<Waypoint>aRoute)
    {
      _route = (ArrayList<Waypoint>) aRoute;
  }
  /** returns <code> false </code> if the the final waypoint has been reached or interrupt() has been called
   */
  public  boolean isGoing()
  {
	  return _keepGoing;
  }
  
  public void followRoute(Collection<Waypoint>aRoute, boolean immediateReturn )
  {
    _route = (ArrayList<Waypoint>) aRoute;
    _keepGoing = true;
    _singleStep = false;
    if(immediateReturn)return;
    while(_keepGoing) Thread.yield();
  }
 
  public void goTo(Waypoint destination, boolean immediateReturn)
  {
    // Check if using PathFinder:
      _singleStep = false;
	if(pathFinder == null) 
		addWayPoint(destination);
	else
		pathFinder.startPathFinding(poseProvider.getPose(), destination);
    
	if(!immediateReturn){
      while(_keepGoing)Thread.yield();
    }
  }

  public void goTo(Waypoint destination) {
   
	  goTo(destination, false);
	// TODO: It would be helpful to return boolean if it got to destination successfully.
  }

  public void goTo(double x, double y) {
	  goTo(new Waypoint(x, y));
  }

  public void goTo(double x, double y, double heading) {
	  goTo(new Waypoint(x, y, heading));
  }
  
  /**
   * If the queue is not empty, the robot will go the first  WayPoint in the
   * queue and then stop. Meanwhile, isGoing() returns true.
   * This method returns immediately.  
   */
  public void goToNext()
    {
      if(_route.size()== 0 ) return;
      else _singleStep = true;
      if(_route.size() > 0 ) _keepGoing = true;
  }
  public void addListener(WaypointListener aListener)
  {
    if(listeners == null )listeners = new ArrayList<WaypointListener>();
    listeners.add(aListener);
  }
  
  public void addTargetListener(WaypointListener targetListener)
  {
    if(targetListeners == null )targetListeners = new ArrayList<WaypointListener>();
    targetListeners.add(targetListener);
  }

  public MoveController getMoveController(){ return _pilot;}

  public void addWayPoint(Waypoint aWayPoint)
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
    _singleStep = false;
  }

  public void flushQueue()
  {
    _keepGoing = false;
    _pilot.stop();
    _route.clear();
  }

  /**
   * Returns the waypoint to which the robot is presently moving.
   * @return the waypoint
   */
  public Waypoint getWayPoint() // TODO: Delete this method? Or add to PathController interface? Might be used by some other sample?
  {
    if(_route.size() <= 0 ) return null;
    return _route.get(0);
  }

  public void setPoseProvider(PoseProvider aProvider)
  {
    poseProvider = aProvider;
  }

  public PoseProvider getPoseProvider()
  {
    return poseProvider;
  }
  
  public void waitForDestinationReached() { // TODO: Delete this method? Might be used by some other sample?
	  while (_keepGoing) Thread.yield();
  }
 
  /**
   * This inner class runs the thread that processes the waypoint queue
   */
  private class Nav extends Thread
  {
    boolean more = true;

    @Override
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
  			double minRadius = (_pilot instanceof ArcMoveController ? ((ArcMoveController) _pilot).getMinRadius() : 0);
  			
  			if (_destination.headingRequired)
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), (float)minRadius, _destination.getPose(),(float)minRadius);
  			} 
  			else
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), _destination, (float)minRadius);  				
  			}
  			// 2. Drive the path
  			for(int i=0;i<moves.length;i++) {
  				((ArcMoveController) _pilot).travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
  			}
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }                                }
          // direction change complete
          
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
            for(WaypointListener l : listeners)
              l.nextWaypoint(new Waypoint(poseProvider.getPose()));
          }
          
          if(targetListeners != null)
          { 
            for(WaypointListener l : targetListeners)
              l.nextWaypoint(_destination);
          }
          
          if (_keepGoing && 0 < _route.size()) {_route.remove(0);}
          _keepGoing = _keepGoing && 0 < _route.size();
          if(_singleStep)_keepGoing = false;
          Thread.yield();
        } // end while keepGoing
        Thread.yield();
      }  // end while more
    }  // end run
  } // end Nav class

  private Nav _nav ;
  private ArrayList<Waypoint> _route  = new ArrayList<Waypoint>() ;
  private ArrayList<WaypointListener> listeners;
  private ArrayList<WaypointListener> targetListeners;
  private boolean _keepGoing = false;
  private boolean _singleStep = false;
  private MoveController _pilot;
  private PoseProvider poseProvider;
  private PathFinder pathFinder = null;
  private Pose _pose = new Pose();
  private Waypoint _destination;
  private double _radius;
}
