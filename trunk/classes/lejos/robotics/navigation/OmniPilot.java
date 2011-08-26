package lejos.robotics.navigation;

import lejos.geom.Point;
import lejos.nxt.Battery;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CruizcoreGyro;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import lejos.util.Matrix;


/*
 *          
 *       central wheel 
 * 			   |
 * 			   |
 *     120°    |    120°
 *            / \
 *           /   \         
 *          /120° \
 *   left wheel   right wheel
 * 
 * wi is the i-th wheel angular speed in [rad/s]
 * Vx and Vy are the components of the speed vector expressed in the robot local reference frame
 * thetadot is the robot angular speed
 * [Vx, Vy, thetadot]' = kMatrix*[w1, w2, w3]' 
 * [w1, w2, w3]' = ikMatrix*[Vx, Vy, thetadot]'	
 * [dx, dy, dh]' = kMatrix*[dr1, dr2, dr3]'
 */

/**
 * <p>Use the OmniPilot class to control holonomic vehicles with three omnidirectional wheels 
 * that allow the robot to move in any direction without changing heading. 
 * The robot can also spin while driving straight, and perform any kind of maneuvre the other steering and differential drive vehicles can do.
 * The odometry is computed by this class directly. 
 * For the class to work properly, take care to design the robot simmetrically, so that the three wheel axes meet in the center of the robot.</p>
 * @author Daniele Benedettelli
 * 
 */
public class OmniPilot implements PoseProvider {
    
    private Pose pose = new Pose();
	private float wheelBase = 7.0f; // units
	private float wheelDiameter = 4.6f; // units
	private double[][] ikPars;
	private double[][] kPars;
	private Matrix ikMatrix; // inverse kinematics matrix
	private Matrix kMatrix; // direct kinematics matrix
	private float linearSpeed = 10; //units/s
	private boolean reverse = false; // true when linearSpeed is negative
	private float speedVectorDirection = 0;
	private float angularSpeed = 0; // deg/s
	protected final NXTRegulatedMotor motor1; // central motor
	protected final NXTRegulatedMotor motor2; // left motor
	protected final NXTRegulatedMotor motor3; // right motor
	private int motor1Speed = 0; //deg/s
	private int motor2Speed = 0; //deg/s
	private int motor3Speed = 0; //deg/s
	private Odometer odo = new Odometer();
	private boolean spinningMode = false; 
	private float spinLinSpeed = 0; // units/s
	private float spinAngSpeed = 0; // deg/s
	private float spinTravelDirection = 0; // deg
	
	public CruizcoreGyro gyro;
	
	private boolean gyroEnabled = false;
	
	/**
	 * Instantiates a new omnidirectional pilot.
	 * This class also keeps track of the odometry
	 * Express the distances in the units you prefer (mm, in, cm ...)
	 * 
	 * @param wheelDistanceFromCenter the wheel distance from center
	 * @param wheelDiameter the wheel diameter 
	 * @param centralMotor the central motor
	 * @param the motor at 120 degrees clockwise from front
	 * @param the motor at 120 degrees counter-clockwise from front
	 * @param centralWheelFrontal if true, the central wheel frontal else it is facing back
	 * @param motorReverse if motors are mounted reversed
	 */
	public OmniPilot (float wheelDistanceFromCenter, float wheelDiameter, 
					NXTRegulatedMotor centralMotor, NXTRegulatedMotor CW120degMotor, NXTRegulatedMotor CCW120degMotor,  
					boolean centralWheelFrontal, boolean motorReverse) {
		this.wheelBase = wheelDistanceFromCenter;
		this.wheelDiameter = wheelDiameter;
		this.motor1 = centralMotor;
		this.motor2 = CCW120degMotor;
		this.motor3 = CW120degMotor;
		initMatrices(centralWheelFrontal, motorReverse);
	    odo.setDaemon(true);
//	    odo.setPriority(6);
	    odo.start();
	}
	
	/**
	 * Instantiates a new omnidirectional pilot.
	 * This class also keeps track of the odometry
	 * Express the distances in the units you prefer (mm, in, cm ...)
	 * This constructor allows you to add a cruizcore gyro for accurate odometry and spinning
	 *
	 * @param wheelDistanceFromCenter the wheel distance from center
	 * @param wheelDiameter the wheel diameter 
	 * @param centralMotor the central motor
	 * @param the motor at 120 degrees clockwise from front
	 * @param the motor at 120 degrees counter-clockwise from front
	 * @param centralWheelFrontal if true, the central wheel frontal else it is facing back
	 * @param motorReverse if motors are mounted reversed
	 * @param gyroPort the gyro port
	 */
	public OmniPilot(float wheelDistanceFromCenter, float wheelDiameter, 
			NXTRegulatedMotor centralMotor, NXTRegulatedMotor CW120degMotor, NXTRegulatedMotor CCW120degMotor,  
			boolean centralWheelFrontal, boolean motorReverse, SensorPort gyroPort) {
		this(wheelDistanceFromCenter, wheelDiameter,centralMotor, CW120degMotor, CCW120degMotor,  
				centralWheelFrontal, motorReverse);
		gyro = new CruizcoreGyro(gyroPort);
//		gyro = new CruizcoreGyro(gyroPort), I2CPort.HIGH_SPEED);
//		gyro = new CruizcoreGyro(gyroPort, I2CPort.LEGO_MODE);
		gyro.reset();
		
		gyroEnabled = true;
	}
	
/*	
	private void showMatrices() {
		LCD.clear();
		System.out.println("ikM row 1 ");
		System.out.println((float)ikMatrix.get(0, 0));
		System.out.println((float)ikMatrix.get(0, 1));
		System.out.println((float)ikMatrix.get(0, 2));
		Button.waitForPress();
		System.out.println("ikM row 2 ");
		System.out.println((float)ikMatrix.get(1, 0));
		System.out.println((float)ikMatrix.get(1, 1));
		System.out.println((float)ikMatrix.get(1, 2));
		Button.waitForPress();
		System.out.println("ikM row 3 ");
		System.out.println((float)ikMatrix.get(2, 0));
		System.out.println((float)ikMatrix.get(2, 1));
		System.out.println((float)ikMatrix.get(2, 2));		
		Button.waitForPress();
		LCD.clear();
		System.out.println("kM row 1 ");
		System.out.println((float)kMatrix.get(0, 0));
		System.out.println((float)kMatrix.get(0, 1));
		System.out.println((float)kMatrix.get(0, 2));
		Button.waitForPress();
		System.out.println("kM row 2 ");
		System.out.println((float)kMatrix.get(1, 0));
		System.out.println((float)kMatrix.get(1, 1));
		System.out.println((float)kMatrix.get(1, 2));
		Button.waitForPress();
		System.out.println("kM row 3 ");
		System.out.println((float)kMatrix.get(2, 0));
		System.out.println((float)kMatrix.get(2, 1));
		System.out.println((float)kMatrix.get(2, 2));		
		Button.waitForPress();	
	}
*/
	
	/**
 * Inits the matrices.
 *
 * @param centralWheelForward the central wheel forward
 * @param motorReverse the motor reverse
 */
private void initMatrices(boolean centralWheelForward, boolean motorReverse) {
		ikPars = new double[3][3];
		kPars = new double[3][3];
		int centralFwd = centralWheelForward? 1: -1;
		int rev = motorReverse? -1: 1;
		// front/back motor row
		ikPars[0][0] = 0;
		ikPars[0][1] = -1*centralFwd;
		ikPars[0][2] = -wheelBase;
		// left motor row
		ikPars[1][0] = Math.sqrt(3)*centralFwd/2;
		ikPars[1][1] = 0.5*centralFwd;
		ikPars[1][2] = -wheelBase;
		// right motor row
		ikPars[2][0] = -Math.sqrt(3)*centralFwd/2;
		ikPars[2][1] = 0.5*centralFwd;
		ikPars[2][2] = -wheelBase;
		ikMatrix = new Matrix(ikPars,3,3);
		ikMatrix.timesEquals(rev*2/wheelDiameter);
		
		// front/back motor column
		kPars[0][0] = 0;
		kPars[1][0] = -2*centralFwd;
		kPars[2][0] = -1/wheelBase;
		// left motor column
		kPars[0][1] = Math.sqrt(3)*centralFwd;
		kPars[1][1] = 1*centralFwd;
		kPars[2][1] = -1/wheelBase;
		// right motor column
		kPars[0][2] = -Math.sqrt(3)*centralFwd;
		kPars[1][2] = 1*centralFwd;
		kPars[2][2] = -1/wheelBase;
		kMatrix = new Matrix(kPars,3,3);
		kMatrix.timesEquals(rev*wheelDiameter/6);	
	}	
	
	
	/**
	 * Sets the speed.
	 *
	 * @param linSpeed the lin speed
	 * @param dir the dir
	 * @param angSpeed the ang speed
	 */
	private void setSpeed(double linSpeed, double dir, double angSpeed) {
		double ang = Math.toRadians(dir);
		double angspd = Math.toRadians(angSpeed);
		double[] spd = {linSpeed*Math.cos(ang), linSpeed*Math.sin(ang), angspd};
		Matrix speeds = new Matrix(spd, 3);
		Matrix commands = ikMatrix.times(speeds);
		
		motor1Speed = (int) Math.toDegrees(commands.get(0, 0));
		motor2Speed = (int) Math.toDegrees(commands.get(1, 0));
		motor3Speed = (int) Math.toDegrees(commands.get(2, 0));
//		LCD.drawString("Vx "+(float)spd[0]+"  ", 0, 0);
//		LCD.drawString("Vy "+(float)spd[1]+"  ", 0, 1);
//		LCD.drawString("ang spd "+angSpeed+"  ", 0, 2);
//		LCD.drawString("w1 "+motor1Speed+" deg/s ", 0, 3);
//		LCD.drawString("w2 "+motor2Speed+" deg/s ", 0, 4);
//		LCD.drawString("w3 "+motor3Speed+" deg/s ", 0, 5);
//		Button.waitForPress();
		motor1.setSpeed(motor1Speed);
		motor2.setSpeed(motor2Speed);
		motor3.setSpeed(motor3Speed);
	}
	
	/**
	 * Sets the acceleration.
	 *
	 * @param accel the new acceleration
	 */
	public void setAcceleration(int accel) {
		this.motor1.setAcceleration(accel);
		this.motor2.setAcceleration(accel);
		this.motor3.setAcceleration(accel);
	}
	
	/**
	 * Start motors.
	 */
	private synchronized void  startMotors() {
		if (motor1Speed>0) 
			motor1.forward(); 
		else 
			motor1.backward();
		if (motor2Speed>0) 
			motor2.forward(); 
		else motor2.backward();
		if (motor3Speed>0) 
			motor3.forward(); 
		else motor3.backward();	
	}
	
	/**
	 * Coast.
	 */
	public synchronized void coast() {
		motor1.flt();
		motor2.flt();
		motor3.flt();
		spinningMode = false;
	}
	
	/**
	 * Forward.
	 */
	public synchronized void forward() {
		spinningMode = false;
		setSpeed(linearSpeed, 0, angularSpeed);
		startMotors();
	}

	/**
	 * Backward.
	 */
	public synchronized void backward() {
		spinningMode = false;
		setSpeed(linearSpeed, 180, angularSpeed);
		startMotors();
	}
	
	/**
	 * Move straight.
	 *
	 * @param linSpeed the lin speed
	 * @param direction the direction
	 */
	public synchronized void moveStraight(float linSpeed, int direction) {
//		float dir = linSpeed>0? direction : direction+180;
		spinningMode = false;
		setSpeed(linSpeed, direction, 0);
		startMotors();
	}
	
	/**
	 * Spinning move.
	 *
	 * @param linSpeed the linear speed [units/s]
	 * @param angSpeed the angular speed [deg/s]
	 * @param direction the direction [deg]
	 */
	public synchronized void spinningMove(float linSpeed, int angSpeed, int direction) {
		spinLinSpeed = linSpeed;
		spinAngSpeed = angSpeed;
		spinTravelDirection = direction;
		spinningMode = true;
//		setSpeed(spinLinSpeed, spinTravelDirection-pose.getHeading(), spinAngSpeed);
//		startMotors();
	}
	
	/**
	 * Stop.
	 */
	public synchronized void stop() {
		motor1.stop();
		motor2.stop();
		motor3.stop();
		spinningMode = false;
	}

	/**
	 * Checks if is moving.
	 *
	 * @return true, if is moving
	 */
	public boolean isMoving() {
		return motor1.isMoving() || motor2.isMoving() || motor3.isMoving();
	}

	/**
	 * Sets the move speed.
	 *
	 * @param speed the new move speed
	 */
	public void setMoveSpeed(float speed) {
		linearSpeed = Math.abs(speed);
		reverse = speed<0;
	}

	/**
	 * Gets the move speed.
	 *
	 * @return the move speed
	 */
	public float getMoveSpeed() {
		return linearSpeed;
	}
	
	/**
	 * Sets the move direction.
	 *
	 * @param dir the new move direction
	 */
	public void setMoveDirection(int dir) {
		speedVectorDirection = dir;
	}
	
	/**
	 * Gets the move direction.
	 *
	 * @return the move direction
	 */
	public float getMoveDirection() {
		return speedVectorDirection;
	}

	/**
	 * Gets the move max speed in units/s.
	 *
	 * @return the move max speed
	 */
	public float getMoveMaxSpeed() {
		// it is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		double maxRadSec = Math.toRadians(Battery.getVoltage()*100f);
		double[] spd = {0, maxRadSec, -maxRadSec};
		Matrix wheelSpeeds = new Matrix(spd, 3);		
		Matrix robotSpeeds = kMatrix.times(wheelSpeeds);
		return (float) Math.sqrt(robotSpeeds.get(0,0)*robotSpeeds.get(0,0) + 
				robotSpeeds.get(1,0)*robotSpeeds.get(1,0)); 
		// max degree/second divided by degree/unit = unit/second
	}

	/**
	 * Sets the turning speed in deg/s
	 *
	 * @param speed the new turning speed
	 */
	public void setTurnSpeed(float speed) {
		angularSpeed = speed;
	}
	
	/**
	 * Gets the turning speed in deg/s 
	 *
	 * @return the turning speed
	 */
	public float getTurnSpeed() {
		return angularSpeed;
	}

	/**
	 * Gets the max turning speed.
	 *
	 * @return the turn max speed
	 */
	public float getTurnMaxSpeed() {
		// it is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		double maxRadSec = Math.toRadians(Battery.getVoltage()*100f);
		Matrix wheelSpeeds = new Matrix(3, 1, maxRadSec);
		Matrix robotSpeeds = kMatrix.times(wheelSpeeds);
		return (float) Math.abs(Math.toDegrees(robotSpeeds.get(2,0)));
		// max degree/second divided by degree/unit = unit/second
	}


	/**
	 * Move.
	 *
	 * @param distance the distance
	 * @param direction the direction
	 * @param angle the angle
	 * @param immediateReturn the immediate return
	 */
	private void move(double distance, double direction, double angle, boolean immediateReturn) {
		spinningMode = false;
		double[] dsp = {distance*Math.cos(Math.toRadians(direction)), distance*Math.sin(Math.toRadians(direction)), Math.toRadians(angle)};
		Matrix displacement = new Matrix(dsp, 3);
		Matrix distances = ikMatrix.times(displacement);
		int d1 = (int) Math.toDegrees(distances.get(0,0));
		int d2 = (int) Math.toDegrees(distances.get(1,0));
		int d3 = (int) Math.toDegrees(distances.get(2,0));
		
		if (angle==0) {
			setSpeed(linearSpeed, direction, 0);
		}
		if (distance==0) {
			setSpeed(0, 0, angularSpeed);
		}
//		LCD.drawString("dx "+(float)dsp[0]+"  ", 0, 0);
//		LCD.drawString("dy "+(float)dsp[1]+"  ", 0, 1);
//		LCD.drawString("d1 "+d1+" deg ", 0, 3);
//		LCD.drawString("d2 "+d2+" deg ", 0, 4);
//		LCD.drawString("d3 "+d3+" deg ", 0, 5);
//		Button.waitForPress();
		motor1.rotate(d1, true);
		motor2.rotate(d2, true);
		motor3.rotate(d3, immediateReturn);

		if (!immediateReturn) 
			while (isMoving()) {
				Thread.yield();
			}
	}
	
	/**
	 * Travel.
	 *
	 * @param distance the distance
	 */
	public void travel(float distance) {
		travel(distance, 0, false);
	}

	/**
	 * Travel.
	 *
	 * @param distance the distance
	 * @param direction the direction
	 */
	public void travel(float distance, float direction) {
		travel(distance, direction, false);
	}
	
	/**
	 * Travel.
	 *
	 * @param distance the distance
	 * @param immediateReturn if true, returns at once.
	 */
	public void travel(float distance, boolean immediateReturn) {
		travel(distance, 0, immediateReturn);
	}
	
	/**
	 * Travel.
	 *
	 * @param distance the distance
	 * @param direction the direction
	 * @param immediateReturn the immediate return
	 */
	public void travel(float distance, float direction, boolean immediateReturn) {
		move(distance, direction, 0, immediateReturn);
	}

	/**
	 * Rotate in place and return when finished.
	 *
	 * @param angle the angle
	 */
	public void rotate(float angle) {
		rotate(angle, false);
	}

	/**
	 * Rotate in place and return immediately
	 *
	 * @param angle the angle
	 * @param immediateReturn the immediate return
	 */
	public void rotate(float angle, boolean immediateReturn) {
		if (angularSpeed==0) angularSpeed = 90;
		move(0, 0, angle, immediateReturn);
	}


	/**
	 * Gets the angle.
	 *
	 * @return the angle
	 */
	public float getAngle() {
//		Sound.playTone(2000, 10);
		int t1 = motor1.getTachoCount();
		int t2 = motor2.getTachoCount();
		int t3 = motor3.getTachoCount();
		double[] dsp = {Math.toRadians(t1),Math.toRadians(t2),Math.toRadians(t3)};
		Matrix displacement = new Matrix(dsp, 3);
		Matrix distances = kMatrix.times(displacement);
		float ang = (float) Math.toDegrees(distances.get(2,0));
//		LCD.drawString(t1+" "+t2+" "+t3, 0, 5);
//		LCD.drawString("a "+ang+"   ", 0, 6);
		return ang;
	}

	/**
	 * Gets the travel distance since last tacho reset.
	 *
	 * @return the travel distance
	 */
	public float getTravelDistance() {
		int t1 = motor1.getTachoCount();
		int t2 = motor2.getTachoCount();
		int t3 = motor3.getTachoCount();
		double[] dsp = {Math.toRadians(t1),Math.toRadians(t2),Math.toRadians(t3)};
		Matrix displacement = new Matrix(dsp, 3);
		Matrix distances = kMatrix.times(displacement);
		float d = (float) Math.sqrt(distances.get(0,0)*distances.get(0,0) +	distances.get(1,0)*distances.get(1,0)); //TODO is this correct for omni odometry?
//		LCD.drawString(t1+" "+t2+" "+t3+"      ", 0, 5);
//		LCD.drawString("d "+d+"   ", 0, 7);
		return d;
	}

	/**
	 * Steer.
	 *
	 * @param turnRate the turn rate
	 */
	public void steer(float turnRate) {
		float angSpeed = turnRate*getTurnMaxSpeed()/200;
		float dir = reverse? speedVectorDirection : speedVectorDirection+180;
		spinningMode = false;
		setSpeed(linearSpeed, dir, angSpeed);
		startMotors();
	}

	/**
	 * Steer.
	 *
	 * @param linSpeed the lin speed
	 * @param angSpeed the ang speed
	 */
	public void steer(float linSpeed, float angSpeed) {
//		float dir = linSpeed>0? speedVectorDirection : speedVectorDirection+180;
		spinningMode = false;
		setSpeed(linSpeed, speedVectorDirection, angSpeed);
		startMotors();
	}

	/**
	 * Steer.
	 *
	 * @param turnRate the turn rate
	 * @param angle the angle
	 * @param immediateReturn the immediate return
	 */
	public void steer(float turnRate, float angle, boolean immediateReturn) {
		angularSpeed = turnRate*getTurnMaxSpeed()/200;
//		LCD.drawString("spd "+angularSpeed+" deg/s ", 0, 0);	
		float radius = (float) (linearSpeed/Math.toRadians(angularSpeed));
		arc(radius,angle,immediateReturn);
	}
	
	/**
	 * Travel arc.
	 *
	 * @param radius the radius
	 * @param distance the distance
	 */
	public void travelArc(double radius, double distance) {
		travelArc(radius, distance, false);
	}

	/**
	 * Travel arc.
	 *
	 * @param radius the radius
	 * @param distance the distance
	 * @param immediateReturn the immediate return
	 */
	public void travelArc(double radius, double distance, boolean immediateReturn) {
		travelArc(radius, distance, 0, false);
	}
	
	/**
	 * Travel arc.
	 *
	 * @param radius the radius
	 * @param distance the distance
	 * @param direction the direction
	 * @param immediateReturn the immediate return
	 */
	public void travelArc(double radius, double distance, float direction,  boolean immediateReturn) {
		float angle = (float) ((distance * 180) / (Math.PI * radius));
		arc(radius, angle, direction, immediateReturn);
	}

	/**
	 * Arc.
	 *
	 * @param radius the radius
	 */
	public void arc(double radius) {
		arc(radius, Float.POSITIVE_INFINITY, 0, true);
	}

	/**
	 * Arc.
	 *
	 * @param radius the radius
	 * @param angle the angle
	 */
	public void arc(float radius, float angle) {
		arc(radius, angle, 0, false);
	}
	
	/**
	 * Arc.
	 *
	 * @param radius the radius
	 * @param angle the angle
	 * @param direction the direction
	 */
	public void arc(double radius, double angle, double direction) {
		arc(radius,angle,direction, false);
	}	
	
	/**
	 * Arc.
	 *
	 * @param radius the radius
	 * @param angle the angle
	 * @param immediateReturn the immediate return
	 */
	public void arc(double radius, double angle, boolean immediateReturn) {
		arc(radius,angle,0,immediateReturn);
	}
	
	/**
	 * Arc.
	 *
	 * @param radius the radius
	 * @param angle the angle
	 * @param direction the direction
	 * @param immediateReturn the immediate return
	 */
	public void arc(double radius, double angle, double direction, boolean immediateReturn) {
		float angSpeed = (float) Math.toDegrees(linearSpeed/radius);
		spinningMode = false;
		setSpeed(linearSpeed, direction, angSpeed);
		if (Float.isInfinite((float)angle)) {
			startMotors();
		} else {
			float distance = (float) (Math.toRadians(angle)*radius);
//			LCD.drawString("R "+radius+" units ", 0, 0);
//			LCD.drawString("A "+angle+" deg ", 0, 1);
//			LCD.drawString("D "+distance+" units ", 0, 2);
//			Button.waitForPress();
			move(distance,direction, angle, immediateReturn);
		}
	}


	/**
	 * Reset all tacho counts.
	 */
	public void reset() {
		motor1.resetTachoCount();
		motor2.resetTachoCount();
		motor3.resetTachoCount();
		odo.reset();
	}
	
	/**
	 * Check if the motors are stalled.
	 *
	 * @return true, if any is stalled
	 */
	public boolean stalled() {
		return (0 == motor1.getRotationSpeed()) || (0 == motor2.getRotationSpeed()) || (0 == motor3.getRotationSpeed());
	}

	/**
	 * Sets drive motor speed.
	 *
	 * @param speed the new speed
	 * @deprecated in 0.8, use setTurnSpeed() and setMoveSpeed(). The method was deprecated, as this it requires knowledge
	 * of the robots physical construction, which this interface should hide!
	 */
	public void setSpeed(int speed) {
	}

	/**
	 * gets the current value of the X coordinate.
	 *
	 * @return current x
	 */
	public float getX()
	{
		return pose.getX();
	}
	
	/**
	 * gets the current value of the Y coordinate.
	 *
	 * @return current Y
	 */
	public float getY()
	{
		return pose.getY();
	}
	
	/**
	 * gets the current value of the robot heading.
	 *
	 * @return current heading
	 */
	public float getHeading()
	{
		return pose.getHeading();
	}

	/**
	 * Rotates the NXT robot to point in a specific direction, using the smallest
	 * rotation necessary.
	 *
	 * @param angle The angle to rotate to, in degrees.
	 */
	public void rotateTo(float angle)
	{
		rotateTo(angle, false);
	}

	/**
	 * Rotates the NXT robot to point in a specific direction relative to the x axis.  It make the smallest
	 * rotation  necessary .
	 * If immediateReturn is true, method returns immidiately
	 * @param angle The angle to rotate to, in degrees.
	 * @param immediateReturn if true,  method returns immediately
	 */
	public void rotateTo(float angle, boolean immediateReturn)
	{
		float turnAngle = angle - pose.getHeading();
		while (turnAngle < -180)turnAngle += 360;
		while (turnAngle > 180) turnAngle -= 360;
		rotate(turnAngle, immediateReturn);
	}
	/**
	 * Robot moves to grid coordinates x,y maintaining the orientation. Returns when arrived to point.
	 * @param x destination X coordinate
	 * @param y destination Y coordinate
	 * 
	 */
	public void goTo(float x, float y)
	{
		goTo(x, y, false);
	}
	
	/**
	 * Robot moves to grid coordinates x,y maintaining the orientation.
	 *
	 * @param x destination X coordinate
	 * @param y destination Y coordinate
	 * @param immediateReturn if true, returns immediately
	 */
	public void goTo(float x, float y, boolean immediateReturn)
	{
		float turnAngle = angleTo(x, y) - pose.getHeading();
		while (turnAngle < -180)turnAngle += 360;
		while (turnAngle > 180) turnAngle -= 360;
		travel(distanceTo(x, y), turnAngle, immediateReturn);
	}
	
	/**
	 * Returns the distance from robot current location to the point with coordinates x,y.
	 *
	 * @param x coordinate of destination
	 * @param y coordinate of destination
	 * @return the distance
	 */
	public float distanceTo(float x, float y)
	{
		return pose.distanceTo(new Point(x, y));
	}
	
	/**
	 * Returns the angle from robot current location to the point with coordinates x,y.
	 *
	 * @param x coordinate of destination
	 * @param y coordinate of destination
	 * @return angle
	 */
	public float angleTo(float x, float y)
	{
		return pose.angleTo(new Point(x, y));
	}
	
	/**
	 * Show pose.
	 *
	 * @param show if true shows the current odometric pose
	 * @param lcdLine the lcd line where to start displaying the pose (3 lines are needed)
	 */
	public void showPose(boolean show, int lcdLine) {
		odo.showPose(show, lcdLine);
	}

	public class Odometer extends Thread {
		
		private int t1old = 0;
		
		private int t2old = 0;
		
		private int t3old = 0;
		
		private boolean keepRunning = true;
		
		private int period = 10; //ms
		
		private boolean displayPose = false;
		
		private int displayLine = 0;
		
		/**
		 * Stop the odometry thread.
		 */
		public void shutDown() {
			keepRunning = false;
		}
		
		/**
		 * Show pose.
		 *
		 * @param show if true shows the current odometric pose
		 * @param lcdLine the lcd line where to start displaying the pose (3 lines are needed)
		 */
		public void showPose(boolean show, int lcdLine) {
			displayPose = show;
			if (lcdLine>4) lcdLine = 4;
			displayLine = lcdLine;
		}
		

		public void run() {
			long tick = period + System.currentTimeMillis(); 
			while(keepRunning) {
               if(System.currentTimeMillis()>= tick) { // simulate timer
                  tick += period;
                  updatePose();
                  if (gyroEnabled) {
                	  pose.setHeading((float)(gyro.getAngle())/100.0f);
                  }
                  if (spinningMode) {
                	  setSpeed(spinLinSpeed, spinTravelDirection-pose.getHeading(), spinAngSpeed);
                	  startMotors();
                	  LCD.drawString("t:"+tick, 0, 0);
                  } 
               } else {
            	   Delay.msDelay(5);
               }
			} 
		}
		
		/**
		 * Reset odometry.
		 */
		public void reset() {
			synchronized (pose) {
				pose.setLocation(new Point(0, 0));
				pose.setHeading(0);
				gyro.reset();
			}
		}

		private void updatePose()
		{
//			Sound.playTone(1000, 5);
			int t1 = motor1.getTachoCount();
			int t2 = motor2.getTachoCount();
			int t3 = motor3.getTachoCount();
			double[] angles = {Math.toRadians(t1-t1old),Math.toRadians(t2-t2old),Math.toRadians(t3-t3old)};
			Matrix wheelTachos = new Matrix(angles, 3);
			Matrix localDeltaPose = kMatrix.times(wheelTachos);
			float dXL = (float) localDeltaPose.get(0,0);
			float dYL = (float) localDeltaPose.get(1,0);
			float dH = (float) localDeltaPose.get(2,0);
			
	
			float H = (float) Math.toRadians(pose.getHeading());
			float dX = (float) (Math.cos(H)*dXL - Math.sin(H)*dYL);
			float dY = (float) (Math.sin(H)*dXL + Math.cos(H)*dYL);
			pose.translate(dX, dY);
			pose.rotateUpdate((float)Math.toDegrees(dH));
		    t1old = t1;
		    t2old = t2;
		    t3old = t3;
		    if (displayPose) {
		    	LCD.drawString("X: "+ Math.round(pose.getX()*1000f)/1000f+ "      ", 0, displayLine);
		    	LCD.drawString("Y: "+ Math.round(pose.getY()*1000f)/1000f+ "      ", 0, displayLine+1);
		    	LCD.drawString("H: "+ Math.round(pose.getHeading()*1000f)/1000f+ " deg     ", 0, displayLine+2);
		    }
		}
	}

	/** 
	 * @see lejos.robotics.localization.PoseProvider#getPose()
	 */
	public Pose getPose() {
		return pose;
	}

	/**
	 * @see lejos.robotics.localization.PoseProvider#setPose(lejos.robotics.navigation.Pose)
	 */
	public void setPose(Pose aPose) {
		pose = aPose;
	}


}
