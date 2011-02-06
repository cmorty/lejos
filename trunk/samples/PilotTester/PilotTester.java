import lejos.nxt.*;
import lejos.util.Delay;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Test of the Pilot class.
 * 
 * Requires a wheeled vehicle with two independently controlled
 * motors to steer differentially, so it can rotate within its 
 * own footprint (i.e. turn on one spot).
 * 
 * Adjust the parameters of the Pilot to the dimensions
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
	static DifferentialPilot robot = new DifferentialPilot(4.3f,13.8f,Motor.B, Motor.C,false);
 
	public static void main(String[] args ) throws Exception
	{
        // Wait for user to press ENTER
        LCD.drawString("M " + robot.getMaxTravelSpeed(), 0, 0);
		Button.waitForPress();
		robot.setTravelSpeed(20); // cm/sec
		robot.setRotateSpeed(180); // deg/sec
		robot.forward();
		Delay.msDelay(1000);;
		robot.stop();
		showCount(0);
		robot.backward();
		Delay.msDelay(1000);;
		robot.stop();
		showCount(1);
		robot.travel(10,true);
		while(robot.isMoving())Thread.yield();
		showCount(2);
		robot.travel(-10);
		showCount(3);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(90);
		}
		showCount(4);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(-90,true);
			while(robot.isMoving())Thread.yield();
		}
		showCount(5);
		robot.steer(-50,180,true);
		while(robot.isMoving())Thread.yield();
		robot.steer(-50,-180);
		showCount(6);
		robot.steer(50,180);
		robot.steer(50, -180);
		showCount(7);
		robot.travel(10,true);
		Delay.msDelay(500);
                robot.stop();
		robot.travel(-10);
		robot.rotate(720);
		
		// Exit after any button is pressed
		Button.waitForPress();
	}
   
	public static void showCount(int i)
	{
		LCD.drawInt(robot.getLeftCount(),0,i);
		LCD.drawInt(robot.getRightCount(),7,i);
	}
}

