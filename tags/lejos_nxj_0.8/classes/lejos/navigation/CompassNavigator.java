package lejos.navigation;


import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.navigation.*;

/**
* The CompassNavigatort class, like its superclass, can keep track of the robot position and the direction angle it faces. It uses a CompassPilot object to control NXT robot movements.<br>
* The position and direction angle values are updated automatically when the movement command returns after the movement is complete and and after stop() command is issued.
* However, some commands optionally return immediately, to permit sensor monitoring in the main thread.  It is then the programmers responsibility to 
* call updateXY() when the robot motion is completed.  All angles are in degrees, distances in the units used to specify robot dimensions.
* As with pilot, the robot must be have two independently controlled drive wheels. 
* Uses the Compass Sensor to determine the robot heading. 
*/

public class CompassNavigator extends SimpleNavigator
{

	private CompassPilot compassPilot; //
	
	/**
	* Allocates a CompassNavigator objects and its  CompassPilot object and initializes it with the proper motors and dimensions.
	* This is a subclass of SimpleNavigator (see that API for other methods).
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

	public CompassNavigator(SensorPort compassPort, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		this(compassPort, wheelDiameter,trackWidth,leftMotor, rightMotor, false);
	}
		
	public CompassNavigator(SensorPort compassPort, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		this(new CompassSensor(compassPort), wheelDiameter,trackWidth,leftMotor, rightMotor,reverse);
	}
	
	public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		this(new CompassPilot(compass, wheelDiameter,trackWidth,leftMotor, rightMotor,reverse));
	}
	 public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth,
            Motor leftMotor, Motor rightMotor)
	{
		this(compass, wheelDiameter,trackWidth,leftMotor, rightMotor,false);

	}
/**
 * To use this constructor, you must first create a compass pilot.
 * @param compassPilot
 */
	public CompassNavigator(CompassPilot pilot)
    {
		 super( (Pilot) pilot);
          compassPilot = pilot;
	}
    
    /**
     * returns the pilot of this navigator
     * @return compass pilot
     */
    CompassPilot getCompassPilot(){return compassPilot;}

/**
 * Robot rotates 360 degrees while calibrating the compass sensor  
 */	
	public void calibrateCompass() {compassPilot.calibrate();}
	

}

