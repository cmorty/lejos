package lejos.navigation;

import lejos.nxt.*;

/**
* The CompassPilot class, like its superclass, can keep track of the robot position and the direction angle it faces; It uses a compassPpilot bject to control NXT robot movements.<br>
* The position and direction angle values are updated automatically when the movement command returns after the movement is complete and and after stop() command is issued.
* However, some commands optionally return immediately, to permit sensor monitoring in the main thread.  It is then the programmers responsibility to 
* call updateXY() when the robot motion is completed.  All angles are in degrees, distances in the units used to specify robot dimensions.
* As with pilot, the robot must be have two independently controlled drive wheels. 
* Uses the Compass Sensor to determine the robot heading. 
*/

public class CompassNavigator extends TachoNavigator 
{

	public CompassPilot compassPilot; //
	
	/**
	* Allocates a CompassNavigator objects and its  CompassPilot object and initializes it with the proper motors and dimensions.
	* This is a subclass of TachoNavigator (see that API for other methods).  
	* The x and y values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
	* the x axis. <BR>
	* @param compassPort  the sensor port connected to the compass sensor   e.g SensorPort.S1
	* @param wheelDiameter The diameter of the wheel, usually printed right on the
	* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm = 1.95 in) 
	* @param trackWidth The distance from the center of the left tire to the center
	* of the right tire, in units of your choice
	* @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
	* @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
	*/

	public CompassNavigator(SensorPort compassPort, double wheelDiameter, double trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		this(compassPort, wheelDiameter,trackWidth,leftMotor, rightMotor, false);
	}
	
	public CompassNavigator(SensorPort compassPort, double wheelDiameter, double trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		super(new CompassPilot(compassPort, wheelDiameter,trackWidth,leftMotor, rightMotor,reverse));
		this.compassPilot = (CompassPilot) super._pilot;
		_heading = getAngle();
	}
	
/**
 * To use this constructor, you must first create a compass pilot.
 * @param compassPilot
 */
	public CompassNavigator(CompassPilot compassPilot) 
	{
		super(compassPilot);
		this.compassPilot = (CompassPilot) super._pilot;
		_heading = getAngle();
	}
/**
 * Robot rotates 360 degrees while calibrating the compass sensor  
 */	
	public void calibrateCompass() {compassPilot.calibrate();}
	
 /**
    * Rotates the NXT robot to point in a specific direction. It will take the shortest
    * path necessary to point to the desired angle. 
    * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
    * when the robot has stopped.  Otherwise, the robot position is lost. 
    * @param angle The angle to rotate to, in degrees.
    * @param immediateReturn iff true,  method returns immediately and the programmer is responsible for calling 
    * updatePosition() before the robot moves again. 
    */	
	public void rotateTo(double angle, boolean immediateReturn)
	{
		compassPilot.rotateTo((int)angle,false);  //???
		/*
		if(immediateReturn)return;
		while(compassPilot.isRotating())Thread.yield();
		*/
		updateHeading();
	}

/**
 * Rotates the NXT robot by a specified angle.
 * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
 * when the robot has stopped.  Otherwise, the robot position is lost. 
 * @param angle The angle to rotate to, in degrees.
 * @param immediateReturn iff true,  method returns immediately and the programmer is responsible for calling 
 * updatePosition() before the robot moves again. 
 */
	public void rotate(double angle, boolean immediateReturn)
	{
		compassPilot.rotate((int)angle,immediateReturn);	
		updateHeading();
	}
	
	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards. 
	 *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
	 * when the robot has stopped.  Otherwise, the robot position is lost. 
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
	 *  is responsible for calling updatePosition() before the robot moves again. 
	 */	
	public void travel(double distance,boolean immediateReturn) 
	{
		compassPilot.resetTachoCount();
		compassPilot.travel(distance,immediateReturn);
		/*
		if(immediateReturn)return;
		while(compassPilot.isTraveling())Thread.yield();
		*/
		updateXY();		
	}
	
	/**
	 * Halts the NXT robot and calculates new x, y coordinates.
	 */
	public void stop()
	{
		compassPilot.stop();
		updateXY();
	}
	
/**
 * Direction of robot facing is set equal to the current compass reading
 */		
	public void updateHeading()
	{
		_heading = (int)compassPilot.compass.getDegreesCartesian();
	}
	
 /**
 * Updates x,y coordinates; assumes last compass angle was constant during travel
 */
	public void updateXY()
	{
		updateHeading();
		double angle = (double)Math.toRadians(_heading);		
		double dx = compassPilot.getTravelDistance() *(double) Math.cos(angle); 
		double dy = compassPilot.getTravelDistance() * (double)Math.sin(angle);
		setPosition(dx + getX(),dy+getY(),_heading);
	}
}