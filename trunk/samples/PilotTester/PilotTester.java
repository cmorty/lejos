import lejos.nxt.*;
import lejos.robotics.navigation.*;

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
	static TachoPilot robot = new TachoPilot(5.6f,16.0f,Motor.A, Motor.C,true);
 
	public static void main(String[] args ) throws Exception
	{
        // Wait for user to press ENTER
		Button.ENTER.waitForPressAndRelease();

		robot.setSpeed(500);
		robot.forward();
		pause(1000);
		robot.stop();
		showCount(0);
		robot.backward();
		pause(1000);
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
		pause(500);
        robot.stop();
		robot.travel(-10);
		robot.rotate(720);
		
		// Exit after any button is pressed
		Button.waitForPress();
	}
		
    public static void pause(int time)
    {
    	try{ Thread.sleep(time);
    }
    	catch(InterruptedException e){}
    }
    
	public static void showCount(int i)
	{
		LCD.drawInt(robot.getLeftCount(),0,i);
		LCD.drawInt(robot.getRightCount(),7,i);
	}
}

