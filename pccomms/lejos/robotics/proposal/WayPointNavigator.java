package lejos.robotics.proposal;


import java.util.*;

/**
 *This  class can follow a sequence of way points;
 *Uses an inner class that has it own thresd to do the work.
 * @author Roger
 */
public class WayPointNavigator  extends BasicNavigator
{
/**
 * can use any pilot the impolements the MoveControl interrface
 * @param pilot
 */
  public WayPointNavigator(ArcRotateMoveController pilot )
  {
    super(pilot);
    _nav = new Nav();
    _nav.start();
  }
/**
 * Follows the route.  Can be a non-blocking method
 * @param aRoute seqiemce of way points to be visited
 * @param immediateReturn if true, returns immidiately
 */
  public void folowRoute( ArrayList<WayPoint>  aRoute, boolean immediateReturn)
  {
    _route = aRoute;
    _keepGoing = true;
    _count = 0;
    System.out.println("follow "+_keepGoing+" "+_route.size());
    if(immediateReturn)return;
    else while(_keepGoing) Thread.yield();
  }
  /**
   * Returns the index of the current waypoint to which the robot is moving
   * @return
   */
public int getCount() { return _count;}

  protected  class Nav extends Thread
  {
    boolean more = true;

    public void run()
    {
      while (more)
      {
        while (_keepGoing)
        {
          _destination = _route.get(_count);
          _pose = drpp.getPose();
          float angle = _pose.relativeBearing(_destination);
          _pilot.rotate(normalize(angle), true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          float distance = _pose.distanceTo(_destination);
          _pilot.travel(distance, true);
          while (_pilot.isMoving() && _keepGoing)
          {
            Thread.yield();
          }
          _pose = drpp.getPose();
          _count++;
          _keepGoing = _keepGoing && _count < _route.size();
        }
      }
    }
  }

   int _count = 0;
   protected Nav _nav ;
   protected ArrayList<WayPoint>  _route ;


}
