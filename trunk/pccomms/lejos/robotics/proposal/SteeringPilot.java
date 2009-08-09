package lejos.robotics.proposal;

import lejos.robotics.TachoMotor;

/**
 * The AnalogPilot (provisional name) is a car with analog steering. It implements the changeHeading() method
 * by making a call to its own arc() methods. This will change the x, y coordinates, but that is fine because anything
 * that needs to listen to the Pilot movements (such as a Dead Reckoner or PathFinder) will be informed and updated.
 * 
 * The implications of this interface are that:
 * 1) AnalogPilot can be used in all PathFinders that uses Pilot, although the results will probably be okay but
 * not spectacular since the PathFinder isn't designed around the movement abilities of a steering car.
 * 2) A more advanced PathFinder could be made that accepts only AnalogPilot. This could be more tailored just for
 * steering vehicles.
 *
 */
public class SteeringPilot implements ArcPilot {
	
	/**
	 * Create a steeering pilot
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

	public void addPilotListener(PilotListener p) {
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
}
