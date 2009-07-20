package lejos.robotics;

public class MotorEvent {
	
	private int degrees;
	
	public MotorEvent(int degrees) {
		this.degrees = degrees; 
	}
	
	public int getRotationDegrees() {
		return this.degrees;
	}
}
