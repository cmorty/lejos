package lejos.robotics.proposal;

import lejos.nxt.Motor;
import lejos.robotics.MotorEvent;
import lejos.robotics.Move;
import lejos.robotics.MoveListener;
import lejos.robotics.TachoMotorListener;;


/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The SteeringPilot uses a similar steering mechanism to a car.
 * If you issue a command for travel(1000) and then issue a command travel(-500) before
 * it completes the travel(1000) movement, it will call stop, properly inform movement listeners that 
 * the forward movement was halted, and then start moving backward 500 units. This makes movements from the SteeringPilot
 * leak-proof and incorruptible. 
 *
 * @author BB
 *
 */
public class SteeringPilot implements ArcMoveController, TachoMotorListener {

	private lejos.robotics.TachoMotor driveMotor;
	private float minTurnRadius;
	private float driveWheelDiameter;
	private boolean reverseDriveMotor;
	
	private boolean isMoving;
	private int oldTacho;
	
	/**
	 * Rotate motor to this tacho value in order to achieve minimum left hand turn. 
	 */
	private int minLeft;
	
	/**
	 * Rotate motor to this tacho value in order to achieve minimum right hand turn. 
	 */
	private int minRight;
	
	/**
	 * Indicates the type of movement (arc, travel) that vehicle is engaged in.
	 */
	private Move moveEvent = null;
	
	// TODO: Possibly will need to allow multiple listeners
	private MoveListener listener = null;
	
	/**
	 * Creates an instance of the SteeringPilot. The drive wheel measurements are written on the side of the LEGO tire, such
	 * as 56 x 26. In this case, the diameter is 56 mm or 5.6 centimeters.
	 * 
	 * The accuracy of this class is dependent on physical factors:
	 * <li> the surface the vehicle is driving on (hard smooth surfaces are much better than carpet)
	 * <li> the accuracy of the steering vehicle (backlash in the steering mechanism will cause turn-angle accuracy problems)
	 * <li> the ability of the steering robot to drive straight (if you see your robot trying to drive straight and it is driving
	 * a curve instead, accuracy will be thrown off significantly) 
	 * <li> When using SteeringPilot with ArcPoseController, the starting position of the robot is also important. Is it truly
	 * lined up with the x axis? Are your destination targets on the floor accurately measured? 
	 * 
	 * @param driveWheelDiameter The diameter of the wheel(s) used to propel the vehicle.
	 * @param driveMotor The motor used to propel the vehicle, such as Motor.B
	 * @param reverseDriveMotor Use true if rotating the drive motor forward causes the vehicle to drive backward. 
	 * @param steeringMotor The motor used to steer the steering wheels, such as Motor.C
	 * @param minTurnRadius The smallest turning radius the vehicle can turn. e.g. 41 centimeters
	 * @param leftTurnTacho The tachometer the steering motor must turn to in order to turn left with the minimum turn radius.
	 * @param rightTurnTacho The tachometer the steering motor must turn to in order to turn right with the minimum turn radius.
	 */
	public SteeringPilot(float driveWheelDiameter, lejos.robotics.TachoMotor driveMotor, boolean reverseDriveMotor, 
			TachoMotor steeringMotor, float minTurnRadius, int leftTurnTacho, int rightTurnTacho) {
		this.driveMotor = driveMotor;
		this.driveMotor.addListener(this);
		this.driveWheelDiameter = driveWheelDiameter;
		this.reverseDriveMotor = reverseDriveMotor;
		
		this.minTurnRadius = minTurnRadius;
		this.minLeft = leftTurnTacho;
		this.minRight = rightTurnTacho;
		
		this.isMoving = false;	
	}
	
	/**
	 * In practice, a robot might steer tighter with one turn than the other.
	 * Currently returns minimum steering radius for the least tight turn direction.  
	 * @return minimum turning radius, in centimeters
	 */
	public float getMinRadius() {
		return minTurnRadius;
	}
	
	// NOTE: Currently the steer method locks this SteeringPilot into one proprietary LEGO robot design.
	// Tach values for left and right should be in constructor.
	// Should be able to use this class with a variety of steering robots.
	// NOTE: Doesn't actually have variable turn radius. Just minTurnRadius for now.
	// Note: Perhaps it should return the actual radius/arc it achieves, in case can't do the one it is called to do.
	// Although this might really screw things up for the algorithm. Shouldn't necessarily attempt arc it wasn't asked to perform.
	// Perhaps it should check if radius is < minRadius, then throw exception or return failed if it can't do it.
	/**
	 * Positive radius = left turn
	 * Negative radius = right turn
	 */
	private float steer(float radius) {
		if(radius == Float.POSITIVE_INFINITY) {
			Motor.C.rotateTo(0);
			return Float.POSITIVE_INFINITY;
		} else if(radius > 0) {
			Motor.C.rotateTo(minLeft);
			return getMinRadius();
		} else { // if(radius <= 0)
			Motor.C.rotateTo(minRight);
			return -getMinRadius();
		}
	}
	
	public boolean arcForward(float turnRadius) {
		return arc(turnRadius, Float.POSITIVE_INFINITY, true);
	}
	
	public boolean arcBackward(float turnRadius) {
		return arc(turnRadius, Float.NEGATIVE_INFINITY, true);
	}
	
	public boolean arc(float turnRadius, float arcAngle) {
		return arc(turnRadius, arcAngle, false);
	}

	public boolean arc(float turnRadius, float arcAngle, boolean immediateReturn) {
		double distance = Move.convertAngleToDistance(arcAngle, turnRadius);
		return travelArc(turnRadius, (float)distance, immediateReturn);
	}

	public void setMinRadius(float minTurnRadius) {
		this.minTurnRadius = minTurnRadius;
	}

	public boolean travelArc(float turnRadius, float distance) {
		return travelArc(turnRadius, distance, false);
	}

	public boolean travelArc(float turnRadius, float distance, boolean immediateReturn) {
		
		if(turnRadius < this.getMinRadius()) return false;
		
		// 1. Check if moving. If so, call stop.
		if(isMoving) stop();
		
		// 2. Change wheel steering:
		float actualRadius = steer(turnRadius);
		
		// 3 Create new Move object:
		float angle = Move.convertDistanceToAngle(distance, actualRadius);
		Move.MoveType mt = Move.MoveType.ARC;
		if(turnRadius == Float.POSITIVE_INFINITY) mt = Move.MoveType.TRAVEL;
		moveEvent = new Move(mt, distance, angle, true);
		
		// TODO: This if() block is a temporary kludge due to Motor.rotate() bug with Integer.MIN_VALUE:
		// Remove this if Roger fixes Motor.rotate() bug.
		if((distance == Float.NEGATIVE_INFINITY & !this.reverseDriveMotor) | (distance == Float.POSITIVE_INFINITY & this.reverseDriveMotor)) {
			driveMotor.backward();
			//return moveEvent;
			return true;
		}
		
		// 4. Start moving
		// Convert Float infinity to Integer maximum value.
		int tachos = (int)((distance * 360) / (driveWheelDiameter * Math.PI));
		if(this.reverseDriveMotor) tachos = -tachos;
		driveMotor.rotate(tachos, immediateReturn);
		
		//return moveEvent;
		return true;
	}
	
	public void backward() {
		travel(Float.NEGATIVE_INFINITY, true);
	}

	public void forward() {
		travel(Float.POSITIVE_INFINITY, true);
	}

	public float getMoveMaxSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	// TODO: This method should indicate it is not live speed. Such as getSpeedSetting(), setSpeedSetting()
	// TODO: Many methods in MoveController have no documentation and unit specification, incl. this.
	public float getMoveSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMovementIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoveSpeed(float speed) {
		// TODO This should set the motor speed for the drive motor, perhaps also calculates based on wheel diameter?
		
	}

	public boolean stop() {
		// 1. Check if moving. If not, return?
		if(!isMoving()) return false; // Should return no movement? Or moveEvent? Null might be appropriate.
		
		// 2. Get instance of moveEvent here. Used to check when rotationStopped() completes
		Move oldMove = moveEvent;
		
		// 3. Stop motor
		driveMotor.stop();
		
		// 4. Compare oldMove with moveEvent, only proceed when it changes
		while(oldMove == moveEvent) {Thread.yield();}
		
		// 5. Return newly created moveEvent
		//return moveEvent;
		return true;
	}

	public boolean travel(float distance) {
		return travel(distance, false);
	}

	public boolean travel(float distance, boolean immediateReturn) {
		return travelArc(Float.POSITIVE_INFINITY, distance, immediateReturn);
	}

	public void addMoveListener(MoveListener listener) {
		this.listener = listener;		
	}

	public Move getMovement() {
		// TODO This is probably supposed to provide the movement that has occurred since starting.
		return null;
	}

	public void rotationStarted(MotorEvent e) {
		isMoving = true;
		oldTacho = e.getTachoCount();
		
		// Notify MoveListener
		if(listener != null) {
			listener.moveStarted(moveEvent, this);
		}
	}

	public void rotationStopped(MotorEvent e) {
		isMoving = false;
		int tachoTotal = e.getTachoCount() - oldTacho ;
		float distance = (float)((tachoTotal/360f) * Math.PI * driveWheelDiameter);
		if(reverseDriveMotor) distance = -distance;
		
		float angle = Move.convertDistanceToAngle(distance, moveEvent.getArcRadius()); 
		
		moveEvent = new Move(moveEvent.getMoveType(), distance ,angle, isMoving);
		
		// Notify MoveListener
		if(listener != null) {
			listener.moveStopped(moveEvent, this);
		}
		
	}
}