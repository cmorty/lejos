package lejos.robotics;

import lejos.robotics.Movement;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Any class that wants to be updated automatically by a MovementProvider should
 * implement this interface/
 * 
 * @author nxj team
 */
public interface MoveListener {
	
	/**
	 * Called when a Movement Provider starts a move
	 *  
	 * @param event the movement
	 * @param mp the movement provider
	 */
	public void movementStarted(Movement event, MovementProvider mp);
	
	/**
	 * Called by the movementr provider when a move stops
	 * 
	 * @param event the movement
	 * @param mp movement provider
	 */
	public void movementStopped(Movement event, MovementProvider mp);
}
