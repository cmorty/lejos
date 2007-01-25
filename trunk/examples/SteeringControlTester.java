
import lejos.nxt.*;
import lejos.robotics.*;


class SteeringControlTester 
{
	static SteeringControl robot = new SteeringControl(Motor.A, Motor.C,2.1f,4.4f,true);
		public static void main(String[] args ) 
	{

		Start.press("S C test");
		SteeringControl robot = new SteeringControl(Motor.A, Motor.C,2.1f,4.4f,true);

		robot.setSpeed(500);
		robot.forward();
		pause(1000);
		robot.stop();
		showCount(0);
		robot.backward();
		pause(1000);
		robot.stop();
		showCount(1);
		robot.travel(10);
		while(robot.isMoving())Thread.yield();
		showCount(2);
		robot.travel(-10,true);
		showCount(3);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(90,true);
		}
		showCount(4);
		for(int i = 0; i<4; i++)
		{
			robot.rotate(-90);
			while(robot.isMoving())Thread.yield();
		}
		showCount(5);
		robot.steer(-50,180);
		while(robot.isMoving())Thread.yield();
		robot.steer(-50,-180);
		while(robot.isMoving())Thread.yield();
		showCount(6);
		robot.steer(50,180,true);
		robot.steer(50, -180, true);
		showCount(7);
		robot.travel(10,true);
		Tools.pause(500);
		robot.travel(-10,true);
		robot.rotate(720);
		while(Button.readButtons()==0)Thread.yield();
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
