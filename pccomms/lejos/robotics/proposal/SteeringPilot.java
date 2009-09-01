package lejos.robotics.proposal;

import lejos.robotics.MoveListener;
import lejos.robotics.Movement;
import lejos.robotics.TachoMotor;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The SteeringPilot uses a similar steering mechanism to a car.
 *
 */
public class SteeringPilot implements ArcPilot {
	
	/**
	 * Create a steering pilot
	 * 
	 * @param wheelDiameter
	 * @param trackWidth
	 * @param driveMotor
	 * @param steeringMotor
	 * @param steerRatio
	 * @param reverse
	 */
	 public SteeringPilot(float wheelDiameter, float trackWidth,
			TachoMotor driveMotor, TachoMotor steeringMotor, float steerRatio,
			final boolean reverse) {
	}

	public void addMoveListener(MoveListener p) {
		// TODO Auto-generated method stub
		
	}

	public void arc(float radius) {
		// TODO Auto-generated method stub
		
	}

	public Movement arc(float radius, float angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Movement arc(float radius, float angle, boolean immediateReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public void forward() {
		// TODO Auto-generated method stub
		
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

	public void setMoveSpeed(float speed) {
		// TODO Auto-generated method stub
		
	}

	public void setTurnSpeed(float speed) {
		// TODO Auto-generated method stub
		
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

	public void setMinRadius(float radius) {
		// TODO Auto-generated method stub
		
	}

	public void backward() {
		// TODO Auto-generated method stub
		
	}

	public float getMinRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Movement getMovement() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getMovementIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}
}
