import lejos.robotics.Pose;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.*;
import lejos.util.PilotProps;
import lejos.nxt.*;
import java.util.ArrayList;

/**
 * The WaypointNav class uses  the SimpleNavigator to execute the
 * individual segments
 *
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Dirk Sturzebecher - 20090131 - initial version
 * revised 20090701  to use instead of extend  a SimpleNavigator.
 */
public class WaypointNav
{
  NavPathController nav;
  /**
   * Inner class to describe an segment by providing the next end point and wanted velocity for turning and moving.
   */
  private class Segment {

    private final float xpos;

    private final float ypos;

    private final float turnSpeed;

    private final float moveSpeed;

    Segment(final float xpos, float ypos, float turnSpeed, float moveSpeed) {
      this.xpos = xpos;
      this.ypos = ypos;
      this.turnSpeed = turnSpeed;
      this.moveSpeed = moveSpeed;
    }

    float getX() {
      return xpos;
    }

    float getY() {
      return ypos;
    }

    float getTurnSpeed() {
      return turnSpeed;
    }

    float getMoveSpeed() {
      return moveSpeed;
    }
  }

  private final ArrayList<Segment> segments = new ArrayList<Segment>();

  /**
   * Create a WaypointNav. Just add points to the queue to determine path.
   * 
   * @param pilot The Pilot to use.
   * @param x The starting x position.
   * @param y The starting y position.
   * @param heading The starting heading.
   */
  public WaypointNav(NavPathController navigator) {
    nav = navigator;
  }

  /**
   * Stop the robot and clear the queue.
   */
  public void clear() {
    nav.interrupt();
    segments.clear();
  }

  /**
   * Add a point to the path to move.
   * 
   * @param xpos The x coordinate.
   * @param ypos The y coordinate.
   * @param turnSpeed The speed to use for turns.
   * @param moveSpeed The speed to use for moves.
   */
  public void add(final float xpos, final float ypos, final float turnSpeed, final float moveSpeed) {
    Segment segment = new Segment(xpos, ypos, turnSpeed, moveSpeed);
    segments.add(segment);
  }

  /**
   * Start to move along the list of points in the queue.
   */
  public void execute(float x, float y, float heading)
  {
    nav.getPoseProvider().setPose(new Pose(x,y,heading));
    System.out.println(" exec ");
    while (segments.size() > 0)
    {
      Segment segment = segments.get(0);
      System.out.println("x,y "+segment.getX()+ " "+segment.getY());
      ((RotateMoveController) nav.getMoveController()).setRotateSpeed(segment.getTurnSpeed());
      nav.getMoveController().setTravelSpeed(segment.getMoveSpeed());
      nav.goTo(new WayPoint(segment.getX(), segment.getY()), false);
      segments.remove(0);
    }
  }
  
	static PilotProps pp = new PilotProps();
	static Float wheelDiameter = Float.parseFloat(pp.getProperty("wheelDiameter", "4.96"));
	static Float trackWidth = Float.parseFloat(pp.getProperty("trackWidth", "13"));
	static RegulatedMotor leftMotor = pp.getMotor(pp.getProperty("leftMotor", "B"));
	static RegulatedMotor rightMotor = pp.getMotor(pp.getProperty("rightMotor", "C"));
	static Boolean reverse = Boolean.parseBoolean(pp.getProperty("reverse","false"));
	
  /**
   * test of WaypointNav
   * @param args
   */
     public static void main(String[] args)
    {
      System.out.println("Any button");
      Button.waitForPress();
      NavPathController nav = new NavPathController(
              new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse));
      WaypointNav wpNav = new WaypointNav(nav);
      wpNav.add(20, 20, 90, 15);
      wpNav.add(20,0,180,30);
      wpNav.add(0,20, 90, 20);
      wpNav.add(0,0,180,30);
      wpNav.execute(0, 0, 0);
    }
}
