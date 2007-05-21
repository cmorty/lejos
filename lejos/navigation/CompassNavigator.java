package lejos.navigation;
import lejos.nxt.*;

/**
 * 
 * Uses a CompassPilot to extend TachoNavigator.
 * 
 * The rotate methods use the CompassSensor to maitain accurate
 * alignment of the robot. 
 *
 */
public class CompassNavigator extends TachoNavigator {

	public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		this(compass, wheelDiameter,trackWidth,leftMotor, rightMotor, false);
	}
	
	public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		super(new CompassPilot(compass, wheelDiameter,trackWidth,leftMotor, rightMotor,reverse));
	}
	
	public CompassNavigator(CompassPilot cp) {
		super(cp);
	}
}

