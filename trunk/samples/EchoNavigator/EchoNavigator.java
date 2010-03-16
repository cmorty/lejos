import lejos.nxt.*;
import lejos.robotics.proposal.*;
import lejos.robotics.Pose;
import lejos.geom.Point;
import java.util.Random;
import lejos.util.Delay;

 
/**
 * EchoNavigator is a obstacle avoiding  robot that attempts reach its destination.
 * uses DiffertntialPilot
 * Hareware rquirements:   an ultrasonic sensor facing foward
 * Since it relies on dead reckoning to keep track of its
 * location, the accuracy of navigation degrades with each obstacle.  Does not
 * mep the obstacles, but uses a randomized avoiding strategy.
 * @author Roger
 */
public class EchoNavigator
{
  public EchoNavigator(final MoveControl aPilot, SensorPort echo)
  {
    sonar= new UltrasonicSensor(echo);
    pilot = aPilot;
    drpp = new DeadReckonerPoseProvider(pilot);
  }

/**
 * attempt to reach a destinaton at coordinates x,y despite obstacles.
 * uses detect() and avoid()
 * @param x coordinate of destination
 * @param y coordinate of destination.
 */
public void goTo(float x, float y)
{
  pilot.setTravelSpeed(20);
  pilot.setRotateSpeed(180);
  Point destination = new Point(x, y);
  pose = drpp.getPose();

  while (pose.distanceTo(destination) > 5)
  {
    float angle = pose.angleTo(destination);
    pilot.rotate(angle - pose.getHeading());  // rotate to face destinaton
    pilot.travel(pose.distanceTo(destination), true);// init move to destination
    boolean clear = detect();// returns if obstacle found or travel is complete
    while (!clear) //  obstacle found
    {
      clear = avoid();
    }
    pose = drpp.getPose();
  }
}
/**
 * backs up, rotates away from the obstacle, and travels forward;
 * returns true if no obstacle was discovered while traveling<br>
 * uses readSensor()
 * @return
 */
  private  boolean  avoid()
  {
    int leftDist = 0;
    int rightDist = 0;
    byte turnDirection = 1;
    boolean more = true;
    while (more)
    {
      pilot.rotate(75);
      Delay.msDelay(50);
      leftDist = sonar.getDistance();
      pilot.rotate(-150);
      Delay.msDelay(50);
      rightDist = sonar.getDistance();
      pilot.rotate(75);
      if (leftDist > rightDist)turnDirection = 1;
      else  turnDirection = -1;
      more = leftDist < _limit && _limit < _limit;
      if (more)
      {
        pilot.travel(-4);
      }
      LCD.drawInt(leftDist, 4, 0, 5);
      LCD.drawInt(rightDist, 4, 8, 5);
    }
    pilot.travel(-10 - rand.nextInt(10));
    int angle = 60+rand.nextInt(60);
    pilot.rotate(turnDirection * angle);
    pilot.travel(10 + rand.nextInt(60), true);
    return  detect ();  // watch for hit while moving forward
  }
  /**
   * Monitors the ultrasonic sensor while the robot is moving.
   * Returns if an obstacle is detected or if the travel is complete
   * @return false if obstacle was detected
   */
  public boolean detect()
  {
    int distance = 255;
    boolean clear = true;
    while( pilot.isMoving()& distance > _limit )
    {
      distance = sonar.getDistance();
      LCD.drawInt(distance, 4,0,1);
      clear = distance > _limit ;
      Thread.yield();
    }
    pilot.stop();
    return clear;
  }

  /**
   * assumes UltrasonicSensor is on port S3;
   * @param args
   */
    public static void main(String[] args)
    {
      System.out.println("Any Button");
     DifferentialPilot pilot = new DifferentialPilot(5.6f, 12.5f, Motor.A, Motor.C);
      EchoNavigator  robot  = new EchoNavigator(pilot,SensorPort.S3);
      Button.waitForPress();
      robot.goTo(200,0);
    }

  private MoveControl pilot;
  private DeadReckonerPoseProvider drpp;
  private Pose pose = new Pose();
  Random rand = new Random();
  UltrasonicSensor sonar;
  int _limit =20; //cm

}