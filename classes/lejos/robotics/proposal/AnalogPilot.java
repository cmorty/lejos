package lejos.robotics.proposal;

import lejos.robotics.TachoMotor;

public class AnalogPilot implements Pilot {
	
	/**
	 * 
	 * @param wheelDiameter
	 * @param trackWidth
	 * @param driveMotor
	 * @param steeringMotor
	 * @param steerRatio
	 * @param reverse
	 */
	 public AnalogPilot(float wheelDiameter, float trackWidth,
			TachoMotor driveMotor, TachoMotor steeringMotor, float steerRatio,
			final boolean reverse) {
	}

	public void addPilotListener(PilotListener p) {
		// TODO Auto-generated method stub
		
	}

	public void arc(float radius) {
		// TODO Auto-generated method stub
		
	}

	public Movement arc(float radius, int angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement arc(float radius, int angle, boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public void backward() {
		// TODO Auto-generated method stub
		
	}

	public void forward() {
		// TODO Auto-generated method stub
		
	}

	public float getHeading() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMoveMaxSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMoveSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getTurnMaxSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getTurnSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}

	public Movement move(Movement vector) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement changeHeading(float angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement changeHeading(float angle, boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMoveSpeed(float speed) {
		// TODO Auto-generated method stub
		
	}

	public void setTurnSpeed(float speed) {
		// TODO Auto-generated method stub
		
	}

	public void steer(int turnRate) {
		// TODO Auto-generated method stub
		
	}

	public Movement steer(int turnRate, int angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement steer(int turnRate, int angle, boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement stop() {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement travel(float distance) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement travel(float distance, boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement travelArc(float radius, float distance) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement travelArc(float radius, float distance,
			boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

}
