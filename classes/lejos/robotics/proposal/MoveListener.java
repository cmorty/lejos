package lejos.robotics.proposal;

/**
 * a Pose that wants to be updated automatically by a MovementProvider should
 * implement this interface
 * @author Roger Glassey
 */


public interface MoveListener {
	
	/**
	 * TODO: Lawrie doesn't want Pilot passed in this method. It's pretty typical in Java for the
	 * object that produced the event to be passed, so I don't see a problem. Just ignore it
	 * if you don't want to use it.
	 *  
	 * @param event
	 * @param p
	 */
	public void movementStarted(Movement event, MovementProvider p);
	
	public void movementStopped(Movement event, MovementProvider p);
}
