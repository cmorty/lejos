package lejos.robotics;

/**
 * 
 * NOTE: Might want to have listener that notifies when arbitrary rotation is completed. 
 *
 */
public interface TachoMotorListener {
	public void rotationStarted(MotorEvent event);
	
	public void rotationEnded(MotorEvent event);
}
