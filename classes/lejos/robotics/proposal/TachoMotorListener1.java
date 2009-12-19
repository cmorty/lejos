package lejos.robotics.proposal;

import lejos.robotics.*;


/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This interface defines a listener that is notified by the tachomMotor when the
 * motor stops  rotating gecause an immediate return rotation has rached its desired limit.
 * IT  used by a MovementController to update itself an immediate Return movement
 * has reached its limit.
 * If the others agree, it will replace TachoMotorListener1
 */
public interface TachoMotorListener1 {
	/**
	 * Called when the motor starts rotating.
	 * @param event
	 */

	
	/**
	 * Called when the motor complete a rotation with immidiate return.
	 * 
	 * @param event
	 */
	public void rotationStopped(TachoMotor1 motor, int tachoCount, long timeStamp);
}
