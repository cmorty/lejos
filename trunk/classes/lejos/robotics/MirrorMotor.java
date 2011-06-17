package lejos.robotics;

/**
 * <p>This class returns a motor that rotates in the reverse direction of a regular motor. All tachometer
 * readings are also reversed.</p> 
 * 
 * <p>Use the factory method MirrorMotor#invertMotor(RegulatedMotor) to retrieve an inverted motor.</p>
 * 
 * @author BB
 *
 */
public class MirrorMotor implements RegulatedMotor {
	// Dev Notes: ReverseMotor, AntiMotor, InverseMotor, OppositeMotor, ContraryMotor, MirrorMotor
	private RegulatedMotor motor;
	
	/**
	 * Returns an inverted regulated motor.
	 * @param motor A RegulatedMotor, such as Motor.A.
	 * @return An inverted regulated motor.
	 */
	public static RegulatedMotor invertMotor(RegulatedMotor motor) {
		if(motor instanceof MirrorMotor) {
			return ((MirrorMotor) motor).motor;
		} 
		return new MirrorMotor(motor);
	}
	
	private MirrorMotor(RegulatedMotor motor) {
		this.motor = motor;
	}
	
	public void addListener(RegulatedMotorListener listener) {
		motor.addListener(listener);
	}

	public void flt(boolean immediateReturn) {
		motor.flt(immediateReturn);
	}

	public int getLimitAngle() { 
		return -motor.getLimitAngle();// REVERSED
	}

	public float getMaxSpeed() {
		return motor.getMaxSpeed();
	}

	public int getSpeed() {
		return motor.getSpeed();
	}

	public boolean isStalled() {
		return motor.isStalled();
	}

	public void rotate(int angle) {
		motor.rotate(-angle);// REVERSED
	}

	public void rotate(int angle, boolean immediateReturn) {
		motor.rotate(-angle, immediateReturn);// REVERSED
	}

	public void rotateTo(int angle) {
		motor.rotateTo(-angle);// REVERSED
	}

	public void rotateTo(int angle, boolean immediateReturn) {
		rotateTo(-angle, immediateReturn);// REVERSED
	}

	public void setAcceleration(int acceleration) {
		motor.setAcceleration(acceleration);
	}

	public void setSpeed(int speed) {
		motor.setSpeed(speed);
	}

	public void setStallThreshold(int error, int time) {
		motor.setStallThreshold(error, time);
	}

	public void stop(boolean immediateReturn) {
		motor.stop();
	}

	public void waitComplete() {
		motor.waitComplete();
	}

	public void backward() {
		motor.forward();// REVERSED
	}

	public void flt() {
		motor.flt();
	}

	public void forward() {
		motor.backward(); // REVERSED
	}

	public boolean isMoving() {
		return motor.isMoving();
	}

	public void stop() {
		motor.stop();
	}

	public int getRotationSpeed() {
		return motor.getRotationSpeed();
	}

	public int getTachoCount() {
		return -motor.getTachoCount();// REVERSED
	}

	public void resetTachoCount() {
		motor.resetTachoCount();
	}
}