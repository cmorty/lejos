package lejos.robotics.proposal;

import lejos.robotics.navigation.Pilot;

public interface PilotListener {
	
	public void movementStarted(Movement event, Pilot p);
	
	public void movementStopped(Movement event, Pilot p);
}
