package lejos.navigation;
import lejos.nxt.*;

public class CompassNavigator extends TachoNavigator {

	public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		super(new CompassPilot(compass, wheelDiameter,trackWidth,leftMotor, rightMotor));
	}
	
	public CompassNavigator(CompassSensor compass, float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		super(new CompassPilot(compass, wheelDiameter,trackWidth,leftMotor, rightMotor,reverse));
	}
}

