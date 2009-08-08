package lejos.robotics.proposal;

//package lejos.robotics;
/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
import lejos.nxt.Button;
import lejos.geom.Point;



/**
 * Experimental Pose
 * Represents the location and heading(direction angle) of a robot.<br>
 * This class includes  methods for updating the PoseX to track common robot movements.
 * It will updates itself for every move of a Pilot if it registeres as a PilotListenerX.
 * It can report the current pose at any time, even whild a move is in progress.
 * @author Roger Glassey
 */
public class PoseX implements PilotListenerX
{
  /**
   * allocate a new PoseX at the origin, heading  = 0:the direction  the positive X axis
   */
public PoseX()
{
  _location = new Point(0,0);
  _heading = 0;
}
/**
 * Allocate a new pose at location (x,y) with specified heading in degrees.
 * @param x
 * @param y
 * @param heading
 */
public PoseX(float x, float y, float heading)
{
  _location = new Point(x,y);
  _heading = heading;
}

public  void movementStarted(Movement move, Pilot p)
  {
    _current = false;
    _angle0 = 0;
    _distance0 = 0;
    pilot = (DifferentialPilotX) p;
  }

	public  void movementStopped(Movement move, Pilot p)
    {
      update(move);
    }
/**
 * Updates can occur while the robot is moving.
 * Update of pose required the change in angle and distance since last update.
 * Pilot returns angle and distance since the start of movement.
 * PoseX uses angle0 and distance0 to calculate the change since last update.
 * */
    public synchronized void  update(Movement move)
    {
  
      float angle = move.getAngleTurned();
      float distance = move.getDistanceTraveled();
      Movement.MovementType type = move.getMovementType();
       if(type == Movement.MovementType.ROTATE)
      {
        rotate(angle -_angle0);
      }
      else if(type == Movement.MovementType.TRAVEL)
      {
        move(distance - _distance0);
      }
      else if(type == Movement.MovementType.ARC)
      {
        arc(distance - _distance0, angle -_angle0) ;
      }
      _current = !pilot.isMoving();
      _angle0 = angle;
      _distance0 = distance;

    }
/**
 * Adds  angle to the pose heading
 * new heading is between -180 and 180
 * @param angle
 */
public void rotate(float angle)
{
  _heading += angle;
  while(_heading < 180)_heading += 360;
  while(_heading > 180)_heading -= 360;
}
/**
 * sets heading to the paramete new heading
 * @param new heading
 */
public void rotateTo(float newHeading)
{
  _heading = newHeading;
}
/**
 * Updates the pose location by moving it by  the parameter distance in the
 * direction of the current heading
 * @param distance to move
 */
public void move(float distance)
{
  float x = distance * (float)Math.cos(Math.toRadians(_heading));
  float y = distance * (float)Math.sin(Math.toRadians(_heading));
  translate(x,y);
}

/**
 * sets the pose locatin and heading to the currect values resulting from travel
 * in a circular arc.  The radius is calculated from the distance and turn angle
 * @param distance
 * @param turnAngle
 */
public void arc(float distance, float turnAngle)
{
  float dx = 0;
    float  dy = 0;
    double heading = (Math.toRadians(_heading));
    if (Math.abs(turnAngle) > .5)
    { 
      float turn = (float)Math.toRadians(turnAngle);
     float radius = distance / turn;
      dy = radius * (float) (Math.cos(heading) - Math.cos(heading + turn));
      dx = radius * (float)(Math.sin(heading + turn) - Math.sin(heading));
    } else if (Math.abs(distance) > .01)
    { 
      dx = distance * (float) Math.cos(heading);
      dy = distance * (float) Math.sin(heading);
    }
    translate((float) dx, (float) dy);
    rotate(turnAngle);
}
/**
 * Change the x and y coordinates of the pose by adding dx and dy.
 * @param dx  change in x coordinate
 * @param dy  change in y coordinate
 */
public void translate( float dx, float dy)
{
    _location.setLocation((float)_location.getX()+dx,(float)_location.getY()+dy);
}
/**
 * calculates the absolute angle to destination from the current location of the pose
 * @param destination
 * @return angle in degrees
 */
public float angleTo(Point destination)
{
  Point d = delta(destination);
  return (float)Math.toDegrees(Math.atan2(d.getY(),d.getX()));
}
/**
 * return the distance to the destination
 * @param destination
 * @return sistance
 */
public float distanceTo(Point destination)
{
   Point d = delta(destination);
  return (float) Math.sqrt( d.getX()*d.getX() + d.getY()*d.getY());
}
private Point delta(Point d)
{
  if(!_current) update(pilot.getMovement());
  return new Point((float)(d.getX() - _location.getX()),
          (float) (d.getY() - _location.getY()));
}
/**
 * returns the heading (direction angle) of the PoseX
 * @return
 */
public float getHeading()
{ 
  if(!_current) update(pilot.getMovement());
   return _heading ;
}
/**
 * return X coordinate
 * @return
 */
public  float getX()
{
  if(!_current) update(pilot.getMovement());
  return (float) _location.getX();}
/**
 * return Y coordinate
 * @return
 */
public   float getY()
{
  if(!_current) update(pilot.getMovement());
  return (float)_location.getY();
}
/**
 * return the location as a Point
 * @return
 */
public Point getLocation()
{ 
  if(!_current) update(pilot.getMovement());
  return _location;
}
public void setLocation(Point p)
{
  _location = p;
  _current = true;
}
public void setHeading(float heading )
{
  _heading = heading;
  _current = true;
}
public void setPilot(DifferentialPilotX aPilot)
{
  pilot = aPilot;
}
    private void pause(int time)
    {
    try
    {
      Thread.sleep(time);
    } catch (InterruptedException ex){}
    }
protected Point _location;
protected float _heading;
public  boolean _current = true; //pose is up to date
protected DifferentialPilotX pilot;
//public Movement _move = new Movement(Movement.MovementType.TRAVEL,0,0);
protected float _angle0;
protected float _distance0;


}

