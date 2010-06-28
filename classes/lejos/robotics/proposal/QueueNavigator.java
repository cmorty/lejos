package lejos.robotics.proposal;



import lejos.robotics.proposal.*;

import java.util.*;


/**
 *This  class can follow a sequence of way points;
 *Uses an inner class that has it own thresd to do the work.
 * It can use either a differential pilot or steering pilot.
 * Uses a PoseController to keep its pose updated, and calls its Waypoint Listeners
 * when a way point is reached.
 * @author Roger
 */
public class QueueNavigator  extends BasicNavigator
{
/**
 * can use any pilot the impolements the MoveControl interrface
 * @param pilot
 */
  public QueueNavigator(ArcMoveController pilot )
  {
    super(pilot, null);
    _nav = new Nav();
    _nav.start();
  }

/**
 * Betin following the route  Can be a non-blocking method
 * @param aRoute sequemce of way points to be visited
 * @param immediateReturn if true, returns immidiately
 */
  public void folowRoute( ArrayList<WayPoint>  aRoute, boolean immediateReturn)
  {
    _route = aRoute;
    _keepGoing = true;
    if(immediateReturn)return;
    else while(_keepGoing) Thread.yield();
  }

/**
 * Add a WayPointListener
 * @param aListener
 */
  public void addWayPointListener(WayPointListener aListener)
  {
    if(listeners == null )listeners = new ArrayList<WayPointListener>();
    listeners.add(aListener);
  }

  /**
   * add a WayPoint to the queue
   * @param aWayPoint
   */
  public void add(WayPoint aWayPoint)
  {
    _route.add(aWayPoint);
    _keepGoing = true;
  }


/**
 * Resume the route after an interrupt
 */
  public void resume()
  {
    if(_route.size() > 0 ) _keepGoing = true;
  }

  /**
   * Stop the robot and emptay the  queue
   */
  public void flush()
  {
    stop();
    for(int i = _route.size()-1 ; i > 0; i++)_route.remove(i);
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
        while (_keepGoing)
        {
          _destination = _route.get(0);
          _pose = poseProvider.getPose();
          float destinationRelativeBearing = _pose.relativeBearing(_destination);
         if(!_keepGoing) break;
           if(_radius == 0)
    {
      ((RotateMoveController) _pilot).rotate(destinationRelativeBearing,true);
    }
           else performArc(destinationRelativeBearing,true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
           _pose = poseProvider.getPose();
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
   protected ArrayList<WayPoint>  _route ;
   protected ArrayList<WayPointListener>  listeners ;


}
