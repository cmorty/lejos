package lejos.robotics.navigation;
 


import lejos.robotics.*;
import lejos.geom.Point;

/**
 * Represents the location and heading(direction angle) of a robot.<br>
 * This class includes  methods for updating the Pose to track common robot movements
 * @author Roger Glassey
 */
public class Pose
{
  /**
   * allocate a new Pose at the origin, heading  = 0:the direction  the positive X axis
   */
public Pose()
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
public Pose(float x, float y, float heading)
{
  _location = new Point(x,y);
  _heading = heading;
}
/**
 * rotate the heading through the specified angle
 * @param angle
 */
public void rotate(float angle)
{
  _heading += angle;
  while(_heading < 180)_heading += 360;
  while(_heading > 180)_heading -= 360;
}
/**
 * rotate to the specified new heading
 * @param new heading
 */
public void rotateTo(float newHeading)
{
  rotate(newHeading - _heading);
}
/**
 * move the specified distance in the direction of current heading.
 * @param distance to move
 */
public void move(float distance)
{
  float x = distance * (float)Math.cos(Math.toRadians(_heading));
  float y = distance * (float)Math.sin(Math.toRadians(_heading));
  translate(x,y);
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
  return new Point((float)(d.getX() - _location.getX()),
          (float) (d.getY() - _location.getY()));
}
/**
 * returns the heading (direction angle) of the Pose
 * @return
 */
public float getHeading() { return _heading ; }
/**
 * return X coordinate
 * @return
 */
public float getX(){ return (float) _location.getX();}
/**
 * return Y coordinate
 * @return
 */
public float getY() {return (float)_location.getY();}
/**
 * return the location as a Point
 * @return
 */
public Point getLocation() { return _location;}
public void setLocation(Point p)
{
  _location = p;
}
public void setHeading(float heading )
{
  _heading = heading;
}
private Point _location;
private float _heading;

}

