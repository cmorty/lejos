package lejos.robotics.proposal;

public interface PilotListener {
	
	/**
	 * TODO: Lawrie doesn't want Pilot passed in this method. It's pretty typical in Java for the
	 * object that produced the event to be passed, so I don't see a problem. Just ignore it
	 * if you don't want to use it.
	 *  
	 * @param event
	 * @param p
	 */
	public void movementStarted(Movement event, Object p);
	
	public void movementStopped(Movement event, Object p);
}
