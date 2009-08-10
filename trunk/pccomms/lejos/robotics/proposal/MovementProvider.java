package lejos.robotics.proposal;

/**
 * Should be implemented by a Pilot that provides a partial movement to a pose
 * when requested.
 *
 * @author nxj team
 */
public interface MovementProvider {
  public Movement getMovement();
  
  public boolean isMoving(); // required for the Pose to do a partial move update
  
  /**
   * Adds a MoveListener that will be notified of all movement events.
   * @param p
   */
  public void addMoveListener(MoveListener listener);
}
