import lejos.nxt.*;
import lejos.navigation.*;
import lejos.nxt.comm.*;

public class MonitorTest 
{
	static Pilot robot = new Pilot(2.1f,4.4f,Motor.A, Motor.C,true);
	
	public static void main(String[] args ) 
	{
        LCPBTResponder lcpThread = new LCPBTResponder();
        lcpThread.setDaemon(true);
        lcpThread.start();
        
        LightSensor light = new LightSensor(SensorPort.S1);
   
		LCP.messageWrite(0, "Waiting for ENTER to be pressed");
		
		try {
			Button.ENTER.waitForPressAndRelease();
		} catch (InterruptedException ie) {}
		
		Pilot robot = new Pilot(2.1f,4.4f,Motor.A, Motor.C,false);
		LCP.messageWrite(0, "PilotTester Started");

		robot.setSpeed(500);
		LCP.messageWrite(0, "Moving forward");
		robot.forward();
		pause(1000);
		robot.stop();
		showCount(0);
		robot.backward();
		pause(1000);
		robot.stop();
		LCP.messageWrite(0, "Stopped");
		showCount(1);
		LCP.messageWrite(0, "Traveling 10 units");
		robot.travel(10,true);
		while(robot.isMoving())Thread.yield();
		LCP.messageWrite(0, "Finished traveling");
		showCount(2);
		LCP.messageWrite(0, "Traveling back 10 units");
		robot.travel(-10);
		LCP.messageWrite(0, "Finished traveling back");
		showCount(3);
		for(int i = 0; i<4; i++)
		{
			LCP.messageWrite(0, "Rotating 90");
			robot.rotate(90);
		}
		LCP.messageWrite(0, "Finished rotating");
		showCount(4);
		for(int i = 0; i<4; i++)
		{
			LCP.messageWrite(0, "Rotating Back 90");
			robot.rotate(-90,true);
			while(robot.isMoving())Thread.yield();
		}
		LCP.messageWrite(0, "Finished rotating back");
		showCount(5);
		LCP.messageWrite(0, "Steering right");
		robot.steer(-50,180,true);
		while(robot.isMoving())Thread.yield();
		LCP.messageWrite(0, "Steering back");
		robot.steer(-50,-180);
		LCP.messageWrite(0, "Finished steering");
		showCount(6);
		LCP.messageWrite(0, "Steering left");
		robot.steer(50,180);
		LCP.messageWrite(0, "Steering back");
		robot.steer(50, -180);
		LCP.messageWrite(0, "Finished steering");
		showCount(7);
		LCP.messageWrite(0, "Traveling forward a bit");
		robot.travel(10,true);
		pause(500);
		LCP.messageWrite(0, "Stopped");
        robot.stop();
		LCP.messageWrite(0, "Traveling backwards a bit");
		robot.travel(-10);
		LCP.messageWrite(0, "A quick spin");
		robot.rotate(720);
		LCP.messageWrite(0, "Finished");
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
		LCD.refresh();
	}

}


