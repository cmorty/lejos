import java.io.IOException;

import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;
import lejos.geom.Point;
import lejos.robotics.navigation.*;
import lejos.util.PilotProps;

/**
 * Testing an algorithm for making arc turns to reach a destination
 *
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Roger Glassey
 */
public class ArcNavigator
{
 public ArcNavigator(DifferentialPilot pilot, float minimumRadius)
 {
   this.pilot = pilot;
   _radius = minimumRadius;

 }
 public void goTo(float x, float y)
 {
   calculatArc(new Point(x,y));
   pilot.reset();
   pilot.arc(_radius*_side,_turnAngle);
   _pose.arcUpdate(pilot.getMovementIncrement(),pilot.getAngleIncrement());
   pilot.reset();
   pilot.travel(_travelDistance);
   _pose.moveUpdate(pilot.getMovementIncrement());

 }
 /**
  * Calculates the parameters of the arc() method to turn the robot toward the
  * destination; also calculates the distance to travel along the tangent line
  * to the destination after the arc move is complete;
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
    // distance to destination from center
       float destDistance = (float)center.distance(destination);
       if(destDistance < _radius )return false;
       //angle between tangent to arc and true bearing of destination from center
       float tangentAngle = _side*(float)Math.asin(_radius/destDistance);// radians
       // true bearing of destination from center
       float centerToDestBearing = center.angleTo(destination);// degrees
      _turnAngle =centerToDestBearing + (float)Math.toDegrees(tangentAngle) -_pose.getHeading();
     _turnAngle = Math.abs(normalize(_turnAngle)); //delete with next revision of Pilot API
    _travelDistance = destDistance * (float)Math.cos(tangentAngle);
    return true;
  }
/**
 * returns the equivalent angle between -180 and 180
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
    public static void main(String[] args) throws IOException
    {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "2.16"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "5.42"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "B"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
      DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
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
    DifferentialPilot pilot;
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
