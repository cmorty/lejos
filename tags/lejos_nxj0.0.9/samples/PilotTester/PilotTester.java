import lejos.nxt.*;
import lejos.util.Delay;
import lejos.util.PilotProps;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Test of the DifferentialPilot class.
 * 
 * Requires a wheeled vehicle with two independently controlled
 * motors to steer differentially, so it can rotate within its 
 * own footprint (i.e. turn on one spot).
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * The vehicle will go through a series of manoeuvres and
 * show the tachometer readings on the screen after each
 * manoeuvre. 
 * 
 * Press ENTER to start and any button to return to the menu
 * when the program has finished.
 *
 * @author Roger Glassey and Lawrie Griffiths
 *
 */ 
public class PilotTester
{
	static RegulatedMotor leftMotor;
	static RegulatedMotor rightMotor;
		
	public static void main(String[] args ) throws Exception
	{
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
    	leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	 
        // Wait for user to press ENTER
		Button.waitForPress();
        robot.setAcceleration(4000);
		robot.setTravelSpeed(20); // cm/sec
		robot.setRotateSpeed(180); // deg/sec
		robot.forward();
		Delay.msDelay(1000);;
		robot.stop();
		showCount(robot, 0);
		robot.backward();
		Delay.msDelay(1000);;
		robot.stop();
		showCount(robot, 1);
		robot.travel(10,true);
		while(robot.isMoving())Thread.yield();
		showCount(robot, 2);
		robot.travel(-10);
		showCount(robot, 3);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(90);
		}
		showCount(robot, 4);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(-90,true);
			while(robot.isMoving())Thread.yield();
		}
		showCount(robot, 5);
		robot.steer(-50,180,true);
		while(robot.isMoving())Thread.yield();
		robot.steer(-50,-180);
		showCount(robot, 6);
		robot.steer(50,180);
		robot.steer(50, -180);
		showCount(robot, 7);
		robot.travel(10,true);
		Delay.msDelay(500);
                robot.stop();
		robot.travel(-10);
		robot.rotate(720);
		
		// Exit after any button is pressed
		Button.waitForPress();
	}
   
	public static void showCount(DifferentialPilot robot, int i)
	{
		LCD.drawInt(leftMotor.getTachoCount(),0,i);
		LCD.drawInt(rightMotor.getTachoCount(),7,i);
	}
}

