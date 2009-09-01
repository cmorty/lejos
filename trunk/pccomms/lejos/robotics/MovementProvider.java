package lejos.robotics;

import lejos.robotics.Movement;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

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
   * @param listener the move listener
   */
  public void addMoveListener(MoveListener listener);
}
