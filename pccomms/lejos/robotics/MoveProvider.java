package lejos.robotics;

import lejos.robotics.Move;

/**
 * Should be implemented by a Pilot that provides a partial movement to a pose
 * when requested.
 *
 * @author nxj team
 */
public interface MoveProvider {
  public Move getMovement();
  
  /**
   * Adds a MoveListener that will be notified of all movement events.
   * @param listener the move listener
   */
  public void addMoveListener(MoveListener listener);
}
