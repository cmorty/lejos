import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.Random;

 
/**
 * EchoNavigator is a path planning robot that attempts to avoid obstacles and still
 * reach its destination.  Requires  two touch sensors.
 * Since it relies on dead reckoning to keep track of its
 * location, the accuracy of navigation degrades with each obstacle.  Does not
 * mep the obstacles, but uses a randomized strategy of avoidance.
 * @author Roger
 */
public class EchoNavigator
{
  public EchoNavigator(Navigator navigator, SensorPort echo, Motor scanMotor)
  {
    this.navigator = navigator;
    sonar= new UltrasonicSensor(echo);
    scanner = scanMotor ;
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
    boolean clear  = move();
    if (!clear)
    {
      while (!avoid()) Thread.yield();
    }
  }
}
/**
 * @param side  on which hit was detected
 * @return  side on which hit was detected during move
 */
  public boolean  avoid()
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
         LCD.drawInt(leftDist,4,0,5);;
         LCD.drawInt(rightDist,4,8,5);
      }
      scanner.rotateTo(0);


    navigator.travel(-10 - rand.nextInt(10));
    int angle = 60+rand.nextInt(60);
    navigator.rotate(turnDirection * angle);
    navigator.travel(10 + rand.nextInt(60), true);
    return  move ();  // watch for hit while moving forward
  }
  /**
   * stop when movement is completed or hit is detected
   * @return side on which hit was detected;  0 means none.
   */
  public boolean move()
  {
    System.out.println(" MOVE ");
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
  Navigator navigator ;
  Random rand = new Random();
  UltrasonicSensor sonar;
  private Motor scanner;
  int _limit = 15;

}