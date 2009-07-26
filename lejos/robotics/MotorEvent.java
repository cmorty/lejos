package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public class MotorEvent {
	
	private int degrees;
	
	public MotorEvent(int degrees) {
		this.degrees = degrees; 
	}
	
	public int getRotationDegrees() {
		return this.degrees;
	}
}
