package lejos.robotics.proposal;

import lejos.robotics.proposal.*;
import lejos.util.Delay;

import java.util.*;
//import lejos.nxt.comm.RConsole;

/**
 *This  class can follow a sequence of way points;
 *Uses an inner class that has it own thresd to do the work.
 * @author Roger
 */
public class QueueNavigator  extends BasicNavigator
{
/**
 * can use any pilot the impolements the MoveControl interrface
 * @param pilot
 */
  public QueueNavigator(ArcRotateMoveController pilot )
  {
    super(pilot);
    _nav = new Nav();
    _nav.start();
  }
/**
 * Betin following the route  Can be a non-blocking method
 * @param aRoute seqiemce of way points to be visited
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
   * Stop the robot
   */
  public void interrupt()
  {
    _keepGoing =false;
    stop();
  }

/**
 * Resume the route after an interrupt
 */
  public void resume()
  {
    if(_route.size() > 0 ) _keepGoing = true;
    //RConsole.println("resume "+_route.size());
  }

  /**
   * emptay the  queue
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
          _pose = drpp.getPose();
          float angle = _pose.relativeBearing(_destination);

         if(!_keepGoing) break;
          _pilot.rotate(normalize(angle), true);
          //RConsole.println("rotate "+angle);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
           //RConsole.println("rotate "+angle+" incr "+_pilot.getAngleIncrement());
//          Delay.msDelay(20);
          //RConsole.println("WPN "+drpp.getPose());

          float distance = _pose.distanceTo(_destination);
           if(!_keepGoing) break;
          _pilot.travel(distance, true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          if(!_keepGoing) break;
          //RConsole.println("travel "+distance+" dist "+_pilot.getMovementIncrement());
//          Delay.msDelay(20);
          //RConsole.println("WPN "+drpp.getPose());
          _pose = drpp.getPose();
          if(listeners != null)
          {
            for(WayPointListener l : listeners)
              l.atWayPoint(drpp.getPose());
          }
          _route.remove(0);
          _keepGoing = _keepGoing && 0 < _route.size();
//          System.out.println("NAV S "+_route.size()+" "+_keepGoing);
          Thread.yield();
        }
      }
    }
  }

//   int _count = 0;
   protected Nav _nav ;
   protected ArrayList<WayPoint>  _route ;
   protected ArrayList<WayPointListener>  listeners ;


}
