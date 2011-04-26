package lejos.robotics.navigation;

import lejos.robotics.navigation.Move;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Any class that wants to be updated automatically by a MoveProvider should
 * implement this interface. Both movementStarted() and movementStopped() also return 
 * a MoveProvider object. In a scenario where one MoveListener GUI is watching movements from
 * multiple MovementProviders, it might want to draw one robot as blue, one as green, one as red, etc..
 * The MoveProvider allows it to differentiate the MovementProviders from one another.
 * 
 * @author nxj team
 */
public interface MoveListener {
	
	/**
	 * Called when a Move Provider starts a move
	 *  
	 * @param event the movement
	 * @param mp the movement provider
	 */
	public void moveStarted(Move event, MoveProvider mp);
	
	/**
	 * Called by the movement provider when a move stops
	 * 
	 * @param event the movement
	 * @param mp movement provider
	 */
	public void moveStopped(Move event, MoveProvider mp);
}
