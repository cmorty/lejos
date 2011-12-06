package lejos.robotics.navigation;

import java.util.ArrayList;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.pathfinding.Path;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
/**
 * This class controls a robot to traverse a Path,  a sequence of  {@link  lejos.robotics.navigation.Waypoint}s.
 * It's default mode is continuous movement(no stopping at waypoints)  but see also {@link #singleStep(boolean)}.  To interrupt the path following task,  call stup().
 *  It uses  an inner class running its own thread to issue movement commands to its
 * {@link lejos.robotics.navigation.MoveController},
 * which can be either a  {@link lejos.robotics.navigation.DifferentialPilot}
 * or {@link lejos.robotics.navigation.SteeringPilot}.
 * It also uses a {@link lejos.robotics.localization.PoseProvider}
 * to keep its pose updated, and calls its {@link lejos.robotics.navigation.NavigationListener}s
 * when  a Waypoint is reached or the robot stops.
 * This class has only one blocking method.
 * @author Roger Glassey
 */
public class Navigator implements WaypointListener
{

   /**
    * Allocates a Navigator object,  using pilot that implements the ArcMoveController interface. 
    * @param pilot
    */
   public Navigator(MoveController pilot)
   {
      this(pilot, null);
   }

   /**
    * Allocates a Navigator object using a pilot and a custom poseProvider, rather than the default
    * OdometryPoseProvider.  
    * @param pilot  the pilot 
    * @param poseProvider  the custom PoseProvider
    */
   public Navigator(MoveController pilot, PoseProvider poseProvider)
   {
      _pilot = pilot;
      if (poseProvider == null)
         this.poseProvider = new OdometryPoseProvider(_pilot);
      else
         this.poseProvider = poseProvider;
      _radius = (_pilot instanceof ArcMoveController ? ((ArcMoveController) _pilot).getMinRadius() : 0);
      _nav = new Nav();
      _nav.setDaemon(true);
      _nav.start();
   }

   /**
    * Sets  the PoseProvider after construction of the Navigator
    * @param aProvider  the PoseProvider
    */
   public void setPoseProvider(PoseProvider aProvider)
   {
      poseProvider = aProvider;
   }

   /**
    * Adds a NavigationListener that is informed when a the robot stops or 
    * reaches a WayPoint.
    * @param listener  the NavitationListener
    */
   public void addNavigationListener(NavigationListener listener)
   {
      _listeners.add(listener);
   }

   /**
    * Returns the PoseProvider
    * @return the PoseProvider
    */
   public PoseProvider getPoseProvider()
   {
      return poseProvider;
   }

   /**
    * Returns the MoveController belonging to this object.
    * @return the pilot
    */
   public MoveController getMoveController()
   {
      return _pilot;
   }

   /**
    * Sets the path that the Navigator will traverse.
    * By default, the  robot will not stop along the way.
    * If the robot is moving when this method is called,  it stops and the current
    * path is replaced by the new one.
    * @param path to be followed.
    */
   public void setPath(Path path)
   {
      if (_keepGoing)
         stop();
      _path = path;
      _singleStep = false;
      _sequenceNr = 0;
   }
   
   /**
    * Clears the current path.
    * If the robot is moving, it will be stopped. 
    */
   public void clearPath() {
	   if (_keepGoing)
	         stop();
	   _path.clear();
   }
   
   /**
    * Gets the current path
    * 
    * @return the path
    */
   public Path getPath() {
	   return _path;
   }

   /**
    * Starts the robot traversing the path.
    * By default, the robot will not stop along  along the way unless stop() is called.
    * @param path  to be followed.
    */
   public void followPath(Path path)
   {
      _path = path;
      followPath();
   }

   /**
    * Starts the robot traversing the current path. 
    * By default, the robot will not stop along  along the way unless stop() is called. 
    */
   public void followPath()
   {
      if (_path.isEmpty())
         return;
      _interrupted = false;
      _keepGoing = true;
   }

   /**
    * Controls whether the robot stops at each Waypoint; applies to the current path only.
    * The robot will move to the next Waypoint if you call {@link #followPath()}.
    * @param yes  if <code>true </code>, the robot stops at each Waypoint.  
    */
   public void singleStep(boolean yes)
   {
      _singleStep = yes;
   }

   /**
    * Starts the robot moving toward the destination.
    * Creates a path consisting of the destination.
    * @param destination  the waypoint to be reached
    */
   public void goTo(Waypoint destination)
   {
      addWaypoint(destination);
      _interrupted = false;
      _keepGoing = true;
      _singleStep = false;
      _sequenceNr = 0;
      followPath(_path);
   }

   /**
    * Starts the  moving toward the destination Waypoint defined by 
    * the parameters/
    * Creates a path consisting of the destination.
    * @param x  coordinate of the destination
    * @param y  coordinate of the destination
    */
   public void goTo(float x, float y)
   {
      goTo(new Waypoint(x, y));
   }

   /**
    * Starts the robot  moving toward the destination Waypoint defined by 
    * the parameters
    * Creates a path consisting of the destination.
    * @param x coordinate of the destination
    * @param y coordinate of th destination
    * @param heading  desired robot heading at arrival 
    */
   public void goTo(float x, float y, float heading)
   {
      goTo(new Waypoint(x, y, heading));
   }

   /**
    * Adds a  Waypoint  to the end of the path. 
    * @param aWaypoint  to be added
    */
   public void addWaypoint(Waypoint aWaypoint)
   {
      _path.add(aWaypoint);
   }

   /**
    *  Adds  a  Waypoint  to the end of the path. 
    * @param x coordinate of the waypoint
    * @param y coordinate of the waypoint
    */
   public void addWaypoint(float x, float y)
   {
      addWaypoint(new Waypoint(x, y));
   }

   /**
    *  Adds  a  Waypoint  to the end of the  path. 
    * @param x coordinate of the waypoint
    * @param y coordinate of the waypoint
    * @param heading the heading of the robot when it reaches the waypoint
    */
   public void addWaypoint(float x, float y, float heading)
   {
      addWaypoint(new Waypoint(x, y, heading));
   }

   /**
    * Stops the robot. 
    * The robot will resume its path traversal if you call {@link #followPath()}.
    */
   public void stop()
   {
      _keepGoing = false;
      _pilot.stop();
      _interrupted = true;
      callListeners();
   }

   /**
    * Returns the waypoint to which the robot is presently moving.
    * @return the waypoint
    */
   public Waypoint getWaypoint()
   {
      if (_path.size() <= 0)
         return null;
      return _path.get(0);
   }

   /** 
    * Returns <code> true </code> if the the final waypoint has been reached 
    * @return  <code> true </code>  if the path is completed
    */
   public boolean pathCompleted()
   {
      return _path.size() == 0;
   }

   /**
    * Waits for the robot  to stop for any reason ;
    * returns <code>true</code> if the robot stopped at the final Waypoint of
    *the  path. 
    * @return   <code> true </code>  if the path is completed
    */
   public boolean waitForStop()
   {
      while (_keepGoing)
         Thread.yield();
      return _path.isEmpty();
   }

   /**
    * Returns <code>true<code> if the robot is moving toward a waypoint
    * @return  <code>true </code> if moving.
    */
   public boolean isMoving()
   {
      return _keepGoing;
   }
   
   public void pathGenerated() {
		// Currently does nothing	
	}

   private void callListeners()
   {
      if (_listeners != null)
      {
         _pose = poseProvider.getPose();
//            RConsole.println("listener called interrupt"+_interrupted +" done "+_path.isEmpty()+" "+_pose);
         for (NavigationListener l : _listeners)
            if (_interrupted)
               l.pathInterrupted(_destination, _pose, _sequenceNr);
            else
            {
               l.atWaypoint(_destination, _pose, _sequenceNr);
               if (_path.isEmpty())
                  l.pathComplete(_destination, _pose, _sequenceNr);
            }
      }
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
        while (_keepGoing && _path != null && ! _path.isEmpty())
        {
          _destination = _path.get(0);
          _pose = poseProvider.getPose();
          float destinationRelativeBearing = _pose.relativeBearing(_destination);
          if(!_keepGoing) break;
          if(_radius == 0)
          {
            ((RotateMoveController) _pilot).rotate(destinationRelativeBearing,true); 
            while (_pilot.isMoving() && _keepGoing);       
            if(!_keepGoing) break;
          }
          else // begin arc direction change
          {
  			// 1. Get shortest path:
  			Move [] moves;
  			double minRadius = (_pilot instanceof ArcMoveController ? 
                       ((ArcMoveController) _pilot).getMinRadius() : 0);
  			
  			if (_destination.headingRequired)
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), 
                            (float)minRadius, _destination.getPose(),(float)minRadius);
  			} 
  			else
  			{
  				moves = ArcAlgorithms.getBestPath(poseProvider.getPose(),
                            _destination, (float)minRadius);  				
  			}
  			// 2. Drive the path
  			for(int i=0;i<moves.length;i++) {
  				((ArcMoveController) _pilot).travelArc(moves[i].getArcRadius(),
                            moves[i].getDistanceTraveled());
  			}
          while (_pilot.isMoving() && _keepGoing)Thread.yield();
          }  // Arc direction change complete
          _pose = poseProvider.getPose();
          if(!_keepGoing) break;
         
          if (_radius == 0)
          {
//             RConsole.println("Navrun travel");
            float distance = _pose.distanceTo(_destination);
            _pilot.travel(distance, true);
          
            while (_pilot.isMoving() && _keepGoing)Thread.yield(); 
//            RConsole.println("travel complete");
             _pose = poseProvider.getPose();
            if(!_keepGoing) break;
            
            if (_destination.headingRequired) 
            {
            _pose = poseProvider.getPose();
            	_destination.getHeading();
            	((RotateMoveController) _pilot).rotate(_destination.getHeading()
                       - _pose.getHeading());
            }
          }         
          
          if (_keepGoing && ! _path.isEmpty()) 
          { 
//             RConsole.println("NavRun keep going "+_keepGoing +" ss "+_singleStep);
             if(!_interrupted)        
          { 
                _path.remove(0);
                _sequenceNr++;           
          }
             callListeners();
          }
          _keepGoing = _keepGoing && ! _path.isEmpty();
          if(_singleStep)_keepGoing = false;
//          RConsole.println("NavRun SS)
          Thread.yield();
        } // end while keepGoing
        Thread.yield();
      }  // end while more
    }  // end run
  } // end Nav classR

  private Nav _nav ;
  private Path _path = new Path();
  /**
   * frequently tested by Nav.run() to break out of primary control loop
   * reset by stop(), and in Nav if _singleStep is set. or end of path is reached
   * set by followPath(xx) and goTo(xx)
   */
  private boolean _keepGoing = false;
  /**
   * if true, causes Nav.run to break whenever  waypoint is reached. 
   */
  private boolean _singleStep = false;
  /** 
   * set by Stop,  reset by followPath() , goTo()
   * used by  Nav.run(), callListeners
   */
  private boolean _interrupted = false;
  private MoveController _pilot;
  private PoseProvider poseProvider;
  private Pose _pose = new Pose();
  private Waypoint _destination;
  private double _radius;
  private int _sequenceNr;
  private ArrayList<NavigationListener> _listeners = new ArrayList<NavigationListener>();
  
}
