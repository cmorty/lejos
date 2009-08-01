package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * 
 * NOTE: Might want to have listener that notifies when arbitrary rotation is completed. 
 *
 */
public interface TachoMotorListener {
	public void rotationStarted(MotorEvent event);
	
	public void rotationEnded(MotorEvent event);
}
