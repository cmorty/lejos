import lejos.nxt.*;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.Pose;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.mapping.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.WayPoint;
import java.util.ArrayList;
import lejos.nxt.comm.RConsole;
import lejos.robotics.localization.*;

/**
 *
 * @author Roger Glassey
 */
public class MCLWPNavTest {

/**
 * this class tests the MCLWayPointNavigator.  It uses a Fixed Range scanner,
 * but can use a RotatingRangeScanner by a couple of simple changes.
 * The map assumes the lines define the X and  Y axes, and the initial pose of
 * the robot is at 60,60 heading 180
 */
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

      DifferentialPilot p = new DifferentialPilot(5.6f,14.5f, Motor.A, Motor.C);

      RotatingRangeScanner rs = new RotatingRangeScanner(Motor.B, SensorPort.S3);
      FixedRangeScanner fs = new FixedRangeScanner(p,SensorPort.S3);
     lejos.geom.Line[] lines = {
      new lejos.geom.Line(0,0,0,200),
        new lejos.geom.Line(0,0,200,0 )};
      RangeMap aMap = new LineMap(lines,null);
      MCLWayPointNavigator  robot = new MCLWayPointNavigator(p, fs,aMap);
       ArrayList<WayPoint>  route = new ArrayList<WayPoint>();
      route.add(new WayPoint(20, 20));
      route.add(new WayPoint(20, 60));
      route.add(new WayPoint(90, 60));
      route.add(new WayPoint(90, 20));
      route.add(new WayPoint(60, 20));
      route.add(new WayPoint(60, 60));
      RConsole.openBluetooth(0);
      Pose pose = new Pose(60,60,180);
      RConsole.println("Initial pose set to "+pose);
      robot.setInitialPose(pose );

      robot.followRoute(route);
    }
} 

