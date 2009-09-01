package lejos.robotics;

import lejos.geom.Point;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

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
 * 
 * @param x the X coordinate
 * @param y the Y coordinate
 * @param heading the heading
 */
public Pose(float x, float y, float heading)
{
  _location = new Point(x,y);
  _heading = heading;
}
/**
 * Rotate the heading through the specified angle
 * 
 * @param angle
 */
public void rotateUpdate(float angle)
{
  _heading += angle;
  while(_heading < 180)_heading += 360;
  while(_heading > 180)_heading -= 360;
}

/**
 * Move the specified distance in the direction of current heading.
 * 
 * @param distance to move
 */
public void moveUpdate(float distance)
{
  float x = distance * (float)Math.cos(Math.toRadians(_heading));
  float y = distance * (float)Math.sin(Math.toRadians(_heading));
  translate(x,y);
}
/**
 * Change the x and y coordinates of the pose by adding dx and dy.
 * 
 * @param dx  change in x coordinate
 * @param dy  change in y coordinate
 */
public void translate( float dx, float dy)
{
    _location.setLocation((float)_location.getX()+dx,(float)_location.getY()+dy);
}
/**
 * Sets the pose locatin and heading to the currect values resulting from travel
 * in a circular arc.  The radius is calculated from the distance and turn angle
 * 
 * @param distance the dtistance traveled
 * @param turnAngle the angle turned
 */
public void arcUpdate(float distance, float turnAngle)
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
    rotateUpdate(turnAngle);
}
/**
 * 
 * Calculates the absolute angle to destination from the current location of the pose
 * 
 * @param destination
 * @return angle in degrees
 */
public float angleTo(Point destination)
{
  Point d = delta(destination);
  return (float)Math.toDegrees(Math.atan2(d.getY(),d.getX()));
}
/**
 * Get the distance to the destination
 * 
 * @param destination
 * @return  the distance
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
 * 
 * @return the heading
 */
public float getHeading() { return _heading ; }
/**
 * Get the X coordinate
 * 
 * @return the X coordinate
 */
public float getX(){ return (float) _location.getX();}
/**
 * Get the Y coordinate
 * 
 * @return the Y coordinate
 */
public float getY() {return (float)_location.getY();}
/**
 * Get the location as a Point
 * 
 * @return the location as a point
 */
public Point getLocation() { return _location;}

/**
 * Set the location of the pose
 * 
 * @param p the new location
 */
public void setLocation(Point p)
{
  _location = p;
}
public void setHeading(float heading )
{
  _heading = heading;
}

protected  Point _location;
protected  float _heading;

}

