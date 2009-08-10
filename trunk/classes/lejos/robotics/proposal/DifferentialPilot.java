package lejos.robotics.proposal;

import lejos.robotics.TachoMotor;

public class DifferentialPilot implements ArcRotatePilot {
	
	public DifferentialPilot(final float wheelDiameter, final float trackWidth,
			final TachoMotor leftMotor, final TachoMotor rightMotor) {
		this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}

	/**
	 * Allocates a DifferentialPilot object, and sets the physical parameters of the
	 * NXT robot.<br>
	 * 
	 * @param wheelDiameter
	 *            Diameter of the tire, in any convenient units (diameter in mm
	 *            is usually printed on the tire).
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public DifferentialPilot(final float wheelDiameter, final float trackWidth,
			final TachoMotor leftMotor, final TachoMotor rightMotor,
			final boolean reverse) {
		this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor,
				reverse);
	}

	/**
	 * Allocates a DifferentialPilot object, and sets the physical parameters of the
	 * NXT robot.<br>
	 * 
	 * @param leftWheelDiameter
	 *            Diameter of the left wheel, in any convenient units (diameter
	 *            in mm is usually printed on the tire).
	 * @param rightWheelDiameter
	 *            Diameter of the right wheel. You can actually fit
	 *            intentionally wheels with different size to your robot. If you
	 *            fitted wheels with the same size, but your robot is not going
	 *            straight, try swapping the wheels and see if it deviates into
	 *            the other direction. That would indicate a small difference in
	 *            wheel size. Adjust wheel size accordingly. The minimum change
	 *            in wheel size which will actually have an effect is given by
	 *            minChange = A*wheelDiameter*wheelDiameter/(1-(A*wheelDiameter)
	 *            where A = PI/(moveSpeed*360). Thus for a moveSpeed of 25
	 *            cm/second and a wheelDiameter of 5,5 cm the minChange is about
	 *            0,01058 cm. The reason for this is, that different while sizes
	 *            will result in different motor speed. And that is given as an
	 *            integer in degree per second.
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public DifferentialPilot(final float leftWheelDiameter,
			final float rightWheelDiameter, final float trackWidth,
			final TachoMotor leftMotor, final TachoMotor rightMotor,
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

	public void rotate(float angle) {
		// TODO Auto-generated method stub
		
	}

	public void rotate(float angle, boolean immediateReturn) {
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

	public float getAngleIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

}
