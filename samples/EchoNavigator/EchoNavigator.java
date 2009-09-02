import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.Random;

 
/**
 * EchoNavigator is a obstacle avoiding  robot that attempts reach its destination.
 *  Uses SimpleNavigator
 * Hareware rquirements:   an ultrasonic sensor mounted on a vertical axle
 * driven by the  third motor.
 * Since it relies on dead reckoning to keep track of its
 * location, the accuracy of navigation degrades with each obstacle.  Does not
 * mep the obstacles, but uses a randomized avoiding strategy.
 * @author Roger
 */
public class EchoNavigator
{
  public EchoNavigator(SimpleNavigator navigator, SensorPort echo, Motor scanMotor)
  {
    this.navigator = navigator;
    sonar= new UltrasonicSensor(echo);
    scanner = scanMotor ;
  }

/**
 * attempt to reach a destinaton at coordinates x,y despite obstacle.
 * @param x coordinate of destination
 * @param y coordinate of destination.
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
    boolean clear  = readDistance();
    if (!clear) //  obstacle found
    {
      while (!avoid()) Thread.yield();  // keeps calling avoid until no obstacle is in view
    }
  }
}
/**
 * backs up, rotates away from the obstacle, and travels forward;
 * returns true if no obstacle was discovered while traveling
 * calls readSensor()
 * @return
 */
  private  boolean  avoid()
  {
      int leftDist = 0;
      int rightDist = 0;
      byte turnDirection = 1;
      boolean more = true;
      while(more)
      {
         scanner.rotateTo(75);
         Sound.pause(20);
         leftDist = sonar.getDistance();
         scanner.rotateTo(-70);
         Sound.pause(20);
         rightDist = sonar.getDistance();
         if(leftDist>rightDist) turnDirection = 1;
         else turnDirection = -1;
         more = leftDist < _limit && _limit< _limit;
         if(more) navigator.travel(-4);
         LCD.drawInt(leftDist,4,0,5);
         LCD.drawInt(rightDist,4,8,5);
      }
      scanner.rotateTo(0);
    navigator.travel(-10 - rand.nextInt(10));
    int angle = 60+rand.nextInt(60);
    navigator.rotate(turnDirection * angle);
    navigator.travel(10 + rand.nextInt(60), true);
    return  readDistance ();  // watch for hit while moving forward
  }
  /**
   * Monitors the ultrasonic sensor while the robot is moving.
   * Returns if an obstacle is detected or if the robot stops
   * @return false if obstacle was detected
   */
  public boolean readDistance()
  {
    System.out.println(" Moving ");
    int distance = 255;
    boolean clear = true;
    while( navigator.isMoving()& distance > _limit )
    {
      distance = sonar.getDistance();
      LCD.drawString("D "+distance, 0, 0);

      clear = distance > _limit ;
      Thread.yield();
    }
    navigator.stop();
    return clear;
  }
    public static void main(String[] args)
    {
      System.out.println("Any Button");
      TachoPilot p = new TachoPilot(5.6f, 14.2f, Motor.A, Motor.C);
      EchoNavigator  robot  = new EchoNavigator( new SimpleNavigator(p),SensorPort.S3, Motor.B);
      Button.waitForPress();
      robot.goTo(200,0);
    }

  public SimpleNavigator navigator ;
  Random rand = new Random();
  UltrasonicSensor sonar;
  private Motor scanner;
  int _limit =20; //cm

}