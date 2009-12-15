package lejos.robotics.proposal;

import lejos.robotics.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

import lejos.geom.Point;
import lejos.robotics.MoveListener;
import lejos.robotics.Move;
import lejos.robotics.MoveProvider;
import lejos.robotics.Pose;



/**
 * Experimental Pose
 * Represents the location and heading(direction angle) of a robot.<br>
 * This class includes  methods for updating the UpdateablePose to track common robot movements.
 * It will updates itself for every move of a Provider if it registers as a MoveListener.
 * The Provider must implement the MoveProvider interface.
 * It can report the current pose at any time, even while a move is in progress.
 * @author Roger Glassey
 */
public class UpdateablePose extends Pose implements MoveListener
{
  /**
   * allocate a new UpdateablePose at the origin, heading  = 0:the direction  the positive X axis
   */
public UpdateablePose()
{
  super();
}
/**
 * Allocate a new pose at location (x,y) with specified heading in degrees.
 * @param x
 * @param y
 * @param heading
 */
public UpdateablePose(float x, float y, float heading)
{
  super(x,y,heading);
}

public  void  moveStarted(Move move, MoveProvider mp)
  {
    _current = false;
    _angle0 = 0;
    _distance0 = 0;
  }

  public void moveStopped(Move move, MoveProvider mp)
  {
    update(move);
  }
/**
 * Updates can occur while the robot is moving.
 * Update of pose required the change in angle and distance since last update.
 * provider returns angle and distance since the start of movement.
 * UpdateablePose uses angle0 and distance0 to calculate the change since last update.
 * */
    protected  synchronized void  update(Move move)
    {

      float angle = move.getAngleTurned();
      float distance = move.getDistanceTraveled();

      Move.MoveType type = move.getMoveType();
       if(type == Move.MoveType.ROTATE)
      {
        rotateUpdate(angle -_angle0);
      }
      else if(type == Move.MoveType.TRAVEL)
      {
        moveUpdate(distance - _distance0);
      }
      else if(type == Move.MoveType.ARC)
      {
        arcUpdate(distance - _distance0, angle -_angle0) ;
      }
      _current = !move.isMoving();
      _angle0 = angle;
      _distance0 = distance;
    }

/**
 * returns the heading (direction angle) of the UpdateablePose
 * @return
 */
public float getHeading()
{
  if(!_current) update(provider.getMovement());
   return _heading ;
}
/**
 * return X coordinate
 * @return
 */
public  float getX()
{
  if(!_current) update(provider.getMovement());
  return (float) _location.getX();}
/**
 * return Y coordinate
 * @return
 */
public   float getY()
{
  if(!_current) update(provider.getMovement());
  return (float)_location.getY();
}
/**
 * return the location as a Point
 * @return
 */
public Point getLocation()
{
  if(!_current) update(provider.getMovement());
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
public void setprovider(MoveProvider aprovider)
{
  provider =  aprovider;
}
public boolean isCurrent() { return _current;}

protected  boolean _current = true; //pose is up to date
protected  MoveProvider provider;
protected float _angle0;
protected float _distance0;


}

