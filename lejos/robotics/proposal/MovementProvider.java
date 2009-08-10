package lejos.robotics.proposal;

/**
 * Should be implement by Pilot that provides a partial movement  to a pose
 * when requested.
 */

/**
 *
 * @author owner
 */
public interface MovementProvider {
  public Movement getMovement();
  public boolean isMoving(); // required for the Pose to do a partial move update

}
