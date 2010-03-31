import lejos.robotics.proposal.DifferentialPilot;
import lejos.nxt.*;
import lejos.robotics.proposal.*;
import lejos.robotics.Pose;
import lejos.geom.Point;
import java.util.Random;

 
/**
 * BumpNavigator is a simple obstacle avoiding robot with a single destination.
 * Requires  two touch sensors.
 * Since it relies on dead reckoning to keep track of its
 * pose,  the accuracy of navigation degrades with each obstacle.  Does not
 * map the obstacles, but uses a randomized avoiding strategy..
 * 
 * @author Roger Glasssey
 */
public class BumpNavigator
{
  /**
   * allocates a BumpNavigator
   * @param pilot  construct this pilot first
   * @param leftTouch -  touch sensor in left side
   * @param rightTouch - touch sensor on right side
   */
  public BumpNavigator( final DifferentialPilot aPilot, final  SensorPort leftTouch, final SensorPort rightTouch)
  {   
    leftBump = new TouchSensor(leftTouch);
    rightBump = new TouchSensor(rightTouch);
    pilot = aPilot;
    drpp = new DeadReckonerPoseProvider(pilot);
  }

/**
 * attempt to reach a destinaton at coordinates x,y despite obstacles.
 * @param x
 * @param y
 */

public void goTo(float x, float y)
  {
    pilot.setRotateSpeed(180);
    Point destination = new Point(x, y);
    pose = drpp.getPose();
    while (pose.distanceTo(destination) > 5)  //close enough??
    {
      float angle = pose.angleTo(destination);
      pilot.rotate(angle - pose.getHeading());  // rotate to face destinaton
      pilot.travel(pose.distanceTo(destination), true);// init move to destination
      int hit = detect(); // returns if obstacle is hit or travel is complete
      while (hit != 0)
      {
          hit = avoid(hit);  // keep avoiding till no obstacle is hit
      }
      pose = drpp.getPose();
    }
  }

 /**
   * Monitors touch sensors while the robot is traveling
   * Returns when robot reavel is complete or obstacle is hit
  *  called in main loop and by avoid()
   * @return side on which hit was detected;  0 means none.
   */
  private int detect()
  {
    int hit = 0;
    while(pilot.isMoving()& hit == 0 )//quit if travel is complete
    {
      if(leftBump.isPressed())hit = 1;
      if(rightBump.isPressed())hit =-1;
      Thread.yield();
    }
    pilot.stop();
    return hit;
  }

/**
 * causes the robot to back up, turn away from the obstacle
 * returns when obstacle is cleared or if an obstacle is detected while traveling
 * @param side  on which hit was detected, 0 if avoiding was complete without hit
 * @return  side on which hit was detected during travel
 */
  public int  avoid(int side)
  {
    if(side == 0)return 0;
    pilot.travel(-5 - rand.nextInt(5));
    int angle = 60+rand.nextInt(60);
    pilot.rotate(-side * angle);
    pilot.travel(10 + rand.nextInt(60), true);
    return  detect();  // watch for hit while moving forward
  }

 
  /**
   * test of BumperNavitator. destination is 200 cm  directly ahead. 
   * @param args
   */
  public static void main(String[] args)
    {
      DifferentialPilot pilot = new DifferentialPilot(5.6f, 14.2f, Motor.A, Motor.C);
      BumpNavigator  robot  = new BumpNavigator(pilot,SensorPort.S1, SensorPort.S4);
      System.out.println("Any button");
      Button.waitForPress();
      robot.goTo(200,0);
    }
  
  private ArcRotateMoveController pilot;
  private DeadReckonerPoseProvider drpp;
  private Pose pose = new Pose();
  Random rand = new Random();
  TouchSensor leftBump;
  TouchSensor rightBump;

}