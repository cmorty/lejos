package lejos.robotics;

import lejos.robotics.Move;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Any class that wants to be updated automatically by a MoveProvider should
 * implement this interface/
 * 
 * @author nxj team
 */
public interface MoveListener {
	/* TODO: BB in my opinion movementStarted() and movementStopped() should also return 
	 *  a MoveProvider object. In a scenario where one MoveListener GUI is watching movements from
	 *  multiple MovementProviders, it might want to draw one robot as blue, one as green, one as red, etc..
	 *  So it needs to be able to differentiate the different MovementProviders from one another.
	 *  If it just gets a Move object it doesn't know who reported it. 
	 */
	
	/**
	 * Called when a Move Provider starts a move
	 *  
	 * @param event the movement
	 * @param mp the movement provider
	 */
	public void moveStarted(Move event, MoveProvider mp);
	
	//TODO: Change to moveStopped/moveStarted
	
	/**
	 * Called by the movementr provider when a move stops
	 * 
	 * @param event the movement
	 * @param mp movement provider
	 */
	public void moveStopped(Move event, MoveProvider mp);
}
