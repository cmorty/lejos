/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lejos.robotics.proposal;
import lejos.robotics.navigation.Pilot;

/**
 *
 * @author owner
 */
interface PilotListenerX
{
/**
	 * TODO: Lawrie doesn't want Pilot passed in this method. It's pretty typical in Java for the
	 * object that produced the event to be passed, so I don't see a problem. Just ignore it
	 * if you don't want to use it.
	 *
	 * @param event
	 * @param p
	 */
	public void movementStarted(Movement event, Pilot p);

	public void movementStopped(Movement event, Pilot p);
}
