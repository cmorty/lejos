import lejos.robotics.navigation.*;
import lejos.nxt.*;
import java.util.ArrayList;

/**
 * The WaypointNav class uses  the SimpleNavigator to execute the
 * individual segments
 *
 * 
 * @author Dirk Sturzebecher - 20090131 - initial version
 * revised 20090701  to use instead of extend  a SimpleNavigator.
 */
public class WaypointNav
{
  SimpleNavigator nav;
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
  public WaypointNav(SimpleNavigator navigator) {
    nav = navigator;
  }

  /**
   * Stop the robot and clear the queue.
   */
  public void clear() {
    nav.stop();
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
    nav.setPose(x,y,heading);
    System.out.println(" exec ");
    while (segments.size() > 0)
    {
      Segment segment = segments.get(0);
      System.out.println("x,y "+segment.getX()+ " "+segment.getY());
      nav.setTurnSpeed(segment.getTurnSpeed());
      nav.setMoveSpeed(segment.getMoveSpeed());
      nav.goTo(segment.getX(), segment.getY());
      segments.remove(0);
    }
  }
  /**
   * test of WaypointNav
   * @param args
   */
     public static void main(String[] args)
    {
      System.out.println("Any button");
      Button.waitForPress();
      SimpleNavigator nav = new SimpleNavigator(
              new TachoPilot(5.6f, 14.2f, Motor.A, Motor.C));
      WaypointNav wpNav = new WaypointNav(nav);
      wpNav.add(20, 20, 90, 15);
      wpNav.add(20,0,180,30);
      wpNav.add(0,20, 90, 20);
      wpNav.add(0,0,180,30);
      wpNav.execute(0, 0, 0);
    }
}
