import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.Random;

 
/**
 * BumpNavigator is a simple obstacle avoiding robot with a single destination.
 * Requires  two touch sensors.
 * Since it relies on dead reckoning to keep track of its
 * location, the accuracy of navigation degrades with each obstacle.  Does not
 * map the obstacles, but uses a randomized avoiding strategy..
 * 
 * @author Roger Glasssey
 */
public class BumpNavigator
{
  public BumpNavigator(SimpleNavigator navigator, SensorPort leftTouch, SensorPort rightTouch)
  {
    this.navigator = navigator;
    leftBump = new TouchSensor(leftTouch);
    rightBump = new TouchSensor(rightTouch);
  }

/**
 * attempt to reach a destinaton at coordinates x,y despite obstacle.
 * @param x
 * @param y
 */

public void goTo(float x, float y)
{
  navigator.setMoveSpeed(20);
  navigator.setTurnSpeed(180);
  float destX = x;
  float destY = y;

  while (navigator.distanceTo(destX,destY) > 5)
  {
    navigator.goTo(destX, destY,true);
    int hit = readSensors();
    if (hit != 0)
    {
      while (avoid(hit)!= 0) Thread.yield();
    }
  }
}
/**
 * causes the robot to back up, turn away from the obstacle
 * @param side  on which hit was detected
 * @return  side on which hit was detected during move
 */
  public int  avoid(int side)
  {
    if(side == 0)return 0;
    navigator.travel(-5 - rand.nextInt(5));
    int angle = 60+rand.nextInt(60);
    navigator.rotate(-side * angle);
    navigator.travel(10 + rand.nextInt(60), true);
    return  readSensors();  // watch for hit while moving forward
  }
  /**
   * Monitors touch sensors while the robot is moving.
   * Returns when robot stops or hit is detected
   * @return side on which hit was detected;  0 means none.
   */
  private int readSensors()
  {
    int hit = 0;
    while(navigator.isMoving()& hit == 0 )
    {
      if(leftBump.isPressed())hit = 1;
      if(rightBump.isPressed())hit =-1;
      Thread.yield();
    }
    navigator.stop();
    return hit;
  }
  /**
   * test of BumperNavitator. destination is 200 cm  directly ahead. 
   * @param args
   */
  public static void main(String[] args)
    {
      TachoPilot p = new TachoPilot(5.6f, 14.2f, Motor.A, Motor.C);
      BumpNavigator  robot  = new BumpNavigator( new SimpleNavigator(p),SensorPort.S1, SensorPort.S4);
      System.out.println("Any button");
      Button.waitForPress();
      robot.goTo(200,0);
    }
  SimpleNavigator navigator ;
  Random rand = new Random();
  TouchSensor leftBump;
  TouchSensor rightBump;

}