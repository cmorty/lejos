package lejos.navigation;

import java.util.ArrayList;

/**
 * The QueueNavigator class extends the SimpleNavigator allowing the specify a path along a set of coordinates.
 * 
 * @author Dirk Sturzebecher - 20090131 - initial version
 */
public class QueueNavigator extends SimpleNavigator {

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

  private final ArrayList segments = new ArrayList();

  /**
   * Create a QueueNavigator. Just add points to the queue to determine path.
   * 
   * @param pilot The Pilot to use.
   * @param x The starting x position.
   * @param y The starting y position.
   * @param heading The starting heading.
   */
  public QueueNavigator(final Pilot pilot, final float x, final float y, final float heading) {
    super(pilot);
    setPosition(x, y, heading);
  }

  /**
   * Stop the robot and clear the queue.
   */
  public void clear() {
    stop();
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
  public void execute() {
    while (segments.size() > 0) {
      Segment segment = (Segment) segments.get(0);
      _pilot.setTurnSpeed(segment.getTurnSpeed());
      rotate(angleTo(segment.getX(), segment.getY()));
      _pilot.setMoveSpeed(segment.getMoveSpeed());
      travel(distanceTo(segment.getX(), segment.getY()));
      segments.remove(0);
    }
  }
}
