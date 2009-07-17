package lejos.robotics.proposal;

import lejos.robotics.navigation.Pilot;

public interface PilotListener {
	public void movementStarted(MoveEvent event, Pilot p);
	
	public void movementStopped(MoveEvent event, Pilot p);
}
