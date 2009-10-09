import lejos.nxt.*;
import lejos.robotics.Pose;
import lejos.geom.Point;
import lejos.robotics.navigation.*;

/**
 *Testing an algorithm for making arc turns to reach a destination
 *
 * @author roger
 */
public class ArcNavigator
{
 public ArcNavigator(TachoPilot pilot, float minimumRadius)
 {
   this.pilot = pilot;
   _radius = minimumRadius;

 }
 public void goTo(float x, float y)
 {
   calculatArc(new Point(x,y));
   pilot.reset();
   pilot.arc(_radius*_side,_turnAngle);
   _pose.arcUpdate(pilot.getTravelDistance(),pilot.getAngle());
   pilot.reset();
   pilot.travel(_travelDistance);
   _pose.moveUpdate(pilot.getTravelDistance());

 }
 /**
  * calculaters the parameters of the arc() method to turn the robot toward the
  * destinatiion; also calculates the distance to travel along the tangent line
  * to the destinatin after the arc move is complete;
  * Returns  false  if the point is inside the turning circle and cannot be reached.
  * @param destination
  * @return true if the destination can be reached.
  */
 protected boolean calculatArc(Point destination)
 {
    Point location = _pose.getLocation();
    // relative bearing of  destination  from robot
    float destinationRelativeBearing = normalize(location.angleTo(destination) - _pose.getHeading());
    //side of the turning center
    _side = Math.signum(destinationRelativeBearing);
    //true bearing of center
    float centerBearing = _pose.getHeading() + 90 * _side;
    // center true bearing in radians
    float centerRad = (float) Math.toRadians(centerBearing);
    Point center = new Point(
            _pose.getX() + _radius * (float) Math.cos(centerRad),
            _pose.getY() + _radius * (float) Math.sin(centerRad));
    // distance to destinatin from center
       float destDistance = (float)center.distance(destination);
       if(destDistance < _radius )return false;
       //angle between tangent to arc and true bearing of destinatin from center
       float tangentAngle = _side*(float)Math.asin(_radius/destDistance);// radians
       // true bearing of destinatin from center
       float centerToDestBearing = center.angleTo(destination);// degrees
      _turnAngle =centerToDestBearing + (float)Math.toDegrees(tangentAngle) -_pose.getHeading();
     _turnAngle = Math.abs(normalize(_turnAngle)); //delete with next revision of Pilot API
    _travelDistance = destDistance * (float)Math.cos(tangentAngle);
    return true;
  }
/**
 * returns the eqivalent angle between -180 and 180
 * @param angle
 * @return
 */
 protected float normalize(float angle)
{
  if(angle<180)angle +=360;
  if(angle > 180)angle -=360;
  return angle;
}
    /**
     * Tests the arc steering code
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
      TachoPilot pilot = new TachoPilot(2.16f, 5.42f, Motor.A, Motor.B);
      float minimumRadius = 5;
      ArcNavigator nav = new ArcNavigator(pilot,minimumRadius);
      Button.waitForPress();
      nav.goTo(10,10);
      nav.goTo(10,-10);
      nav.goTo(-10,10);
      nav.goTo(-10,-10);
      nav.goTo(10,10);
      Button.waitForPress();

    }
    TachoPilot pilot;
    /**
     * the radius to use in the arc method
     */
    protected  float _radius;
    /**
     * distance to travel after the arc is complete
     */
    protected float _travelDistance;
/**
 * angle of the arc
 */
    protected float _turnAngle;
    Pose _pose = new Pose(0,0,0);
    /**
     * +1 if the center of the turn is on the left side of the robot;
     */
    protected float _side;

}
