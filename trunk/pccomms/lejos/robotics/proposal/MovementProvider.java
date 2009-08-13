package lejos.robotics.proposal;

/**
 * Should be implemented by a Pilot that provides a partial movement to a pose
 * when requested.
 *
 * @author nxj team
 */
public interface MovementProvider {
  public Movement getMovement();
  
  /**
   * Adds a MoveListener that will be notified of all movement events.
   * @param p
   */
  public void addMoveListener(MoveListener listener);
}
