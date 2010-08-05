package lejos.robotics.localization;

import lejos.geom.Point;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Pose;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.RangeScanner;
import java.util.ArrayList;

/**
 * Qususd architecture navitaotr that uses MCL to estimate position at way point
 * @author Roger
 */
public class MCLWayPointNavigator
{

  public MCLWayPointNavigator(DifferentialPilot aPilot, RangeScanner aScaner, RangeMap aMap)
  {

    pilot = aPilot;
     scanner =aScaner;
     map = aMap;
     mcl = new MCLPoseProvider(pilot,scanner, map, NUM_PARTICLES );
     MCLParticleSet.setSigma(4);
  }
/**
 * set the initial pose of the robot. The robot will perform a scan and estimate
 * its pose from the map data.
 * @param pose - the initial pose.
 */
  public void setInitialPose(Pose pose )
  {
//   RConsole.println("set inital  pose "+pose );
    mcl.setInitialPose( pose, 7,5);
     float[] angles =
    {
      normalize(180 - pose.getHeading()), normalize(-90 - pose.getHeading())
    };
    scanner.setAngles(angles);
     mcl.update();
    _pose = mcl.getPose();
//    dl.writeLog(_pose.getX(),_pose.getY(),_pose.getHeading());
     RConsole.println("initial pose estimate "+_pose);
  }
/**
 * The robot will follow the route defined by the list of WayPoints.
 * The method will return when the last waypoint is reached.
 * The robot pose at each waypoint is diaplayed on the console.
 * To do:  use the queuing architecture so the method can return immediately.
 * @param route
 */
   public void followRoute( ArrayList<WayPoint>  route)
  {
     _keepGoing = true;
     while(route.size()>0 && _keepGoing)
     {
       Pose p = route.remove(0).getPose();
       goTo(p.getLocation());
     }
  }


 /**
  * The robot goes to the destination.
  * This method is called repeatedly by the followRoute() method
  * @param destination
  */
  public void goTo(Point destination)
  {
    _destination = destination;
    _pose = mcl.getPose();
    float nextHeading = _pose.angleTo(destination);
    float angle =_pose.relativeBearing(destination);

    RConsole.println("Destination "+destination+" direction "+nextHeading+" rotation angle "+angle);
    float[] angles =
    {
      normalize(180 - nextHeading), normalize(-90 - nextHeading)
    };
    scanner.setAngles(angles);
    float distance = _pose.distanceTo(_destination);
//    RConsole.println("Destination " + destination + " dist " + distance + " heading " + nextHeading);
//    dl.writeLog((float)destination.getX(),(float)destination.getY(),nextHeading);
    pilot.rotate(angle);
    pilot.travel(distance);
     mcl.update();  // to Do : make this update automatic
    _pose = mcl.getPose();
    RConsole.print("at waypoint "+mcl.getPose());
       RConsole.println(" sX "+mcl.getSigmaX()+" sY "+
           mcl.getSigmaY()+" sH "+mcl.getSigmaHeading());
  }

  /**
   * Interrupts the progreas of the robot.
   */
  public void interrupt(){_keepGoing = false;}


public static float normalize(float angle)
{
  while(angle < -180)angle += 360;
  while(angle > 180)angle -= 360;
  return angle;
}

/***************  instance variables. ********************************/

 protected Pose _pose;
  private static int NUM_PARTICLES = 100;
  protected boolean _keepGoing = false;
   protected Point _destination;
   static RangeScanner scanner;
  private static RangeMap map;
  private static DifferentialPilot pilot;
  private static MCLPoseProvider mcl;
//  Datalogger dl = new Datalogger();  //remove commment markers to use this/

}
