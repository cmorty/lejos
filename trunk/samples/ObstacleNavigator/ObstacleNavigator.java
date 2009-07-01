import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.Random;

 
/**
 * ObstacleNavigator is a path planning robot that attempts to avoid obstacles and still
 * reach its destination.  Requires  two touch sensors.
 * Since it relies on dead reckoning to keep track of its
 * location, the accuracy of navigation degrades with each obstacle.  Does not
 * mep the obstacles, but uses a randomized strategy of avoidance.
 * @author Roger
 */
public class ObstacleNavigator
{
  public ObstacleNavigator( Pilot p, SensorPort leftTouch, SensorPort rightTouch)
  {
    nav = new SimpleNavigator(p);
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
  nav.setMoveSpeed(30);
  nav.setTurnSpeed(180);
  float destX = x;
  float destY = y;

  while (nav.distanceTo(destX,destY) > 5)
  {
    nav.goTo(destX, destY,true);
    int hit = move();
    if (hit != 0)
    {
      while (avoid(hit)!= 0) Thread.yield();
    }
  }
}
/**
 * @param side  on which hit was detected
 * @return  side on which hit was detected during move
 */
  public int  avoid(int side)
  {
    if(side == 0)return 0;
    nav.travel(-5 - rand.nextInt(5));
    int angle = 60+rand.nextInt(60);
    nav.rotate(-side * angle);
    nav.travel(10 + rand.nextInt(60), true);
    return  move ();  // watch for hit while moving forward
  }
  /**
   * stop when movement is completed or hit is detected
   * @return side on which hit was detected;  0 means none.
   */
  public int move()
  {
    int hit = 0;
    while( nav.isMoving()& hit == 0 )
    {
      if(leftBump.isPressed())hit = 1;
      if(rightBump.isPressed())hit =-1;
      Thread.yield();
    }
    nav.stop();
    return hit;
  }
  SimpleNavigator nav ;
  Random rand = new Random();
  TouchSensor leftBump;
  TouchSensor rightBump;

}