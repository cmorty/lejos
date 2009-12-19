
package lejos.robotics.proposal;
/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

import lejos.geom.Point;

/**
 * Represents the location and heading(direction angle) of a robot.<br>
 * This class includes  methods for updating the Pose1 to track common robot movements.
 * It will updates itself for every move of a Provider if it registers as a listener
 * The Provider must implement the MoveProvider1 interface.
 * This class can report the current pose at any time, even while a move is in progress.
 * @author Roger Glassey
 */
public class Pose1
{
/**
   * allocate a new Pose1 at the origin, heading  = 0:the direction  the positive X axis <br>
 *  This Pose1 will no be automatically updated when the robot moves unless you
 * 
   */
  Pose1()
  {
    this(null,0,0,0);
  }
  /**
   * allocate a new Pose1 at the origin, heading  = 0:the direction  the positive X axis
   * @param moveController  the MoveProvider1 that will update this pose automatically
   */
  public Pose1(MoveProvider1 moveController )
  {
    this(moveController,0,0,0);
  }

  /**
   * Allocate a new pose at location (x,y) with specified heading in degrees.
   * Adds this pose as a listener to the MoveController
   * @param x
   * @param y
   * @param heading
   * @param moveController  the MoveProvider1 that will update this pose automatically
   */
  public Pose1(MoveProvider1 moveController, float x, float y, float heading)
  {
    _provider = moveController;
    _location = new Point(x, y);
    _heading = heading;
    if(_provider != null)_provider.addPose(this);
  }

/**
 * called by MoveProvider1 when it starts a movement
 * resets _current,_angle0,_distance0
 */
  public void movementStarted()
  { 
    _current = false;
    _angle0 = 0;
    _distance0 = 0;
  }

  /**
   * This method is called by a MoveProvider1 when requested and when a movement is complete.
   * Uses _angle0 and _distanc0
   * */
  public synchronized void update( float distance, float angle, boolean isMoving)
  {
    float dist = distance - _distance0;
    float angl = angle - _angle0;
    if(Math.abs(angl)< 1.5f) travelUpdate(dist);
    else if (Math.abs(dist )<.1f) rotateUpdate(angl);
    else  arcUpdate(dist,angl);
    _current = !isMoving;
    _angle0 = angle;
    _distance0 = distance;
  }

  /**
   * helper method called by update()
   * @param distance
   */
  public void travelUpdate(float distance)
  {
    float x =distance * (float) Math.cos(Math.toRadians(_heading));
    float y = distance * (float) Math.sin(Math.toRadians(_heading));
    _location.translate(x, y);
  }

  /**
   * helper method called by update()
   * @param angle
   */
 public void rotateUpdate(float angle)
  {
    _heading = normalize(_heading + angle);
  }

  /**
   * Helper method called by update();
   * Sets the pose location and heading to the currect values resulting from movement
   * in a circular arc.  The radius is calculated from the distance and turn angle
   *
   * @param distance the distance traveled
   * @param turnAngle the angle turned
   */
  public void arcUpdate(float distance, float turnAngle)
  {
    float dx = 0;
    float dy = 0;
    double heading = (Math.toRadians(_heading));
    float turn = (float) Math.toRadians(turnAngle);
    float radius = distance / turn;
    dy = radius * (float) (Math.cos(heading) - Math.cos(heading + turn));
    dx = radius * (float) (Math.sin(heading + turn) - Math.sin(heading));
    _location.translate(dx, dy);
    rotateUpdate(turnAngle);
  }

  /**
   * returns the heading (direction angle) of the Pose1
   * @return
   */
  public float getHeading()
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return _heading;
  }

  /**
   * return X coordinate
   * @return
   */
  public float getX()
  {  
    if (!_current && _provider !=null) _provider.updatePose();
    return (float) _location.getX();
  }

  /**
   * return Y coordinate
   * @return
   */
  public float getY()
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return (float) _location.getY();
  }

  /**
   * return the location as a Point
   * @return
   */
  public Point getLocation()
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return _location;
  }

  public void setLocation(Point p)
  {
    _location = p;
    _current = true;
  }

  public void setHeading(float heading)
  {
    _heading = heading;
    _current = true;
  }

  public void setprovider(MoveProvider1 moveController)
  {
    _provider = moveController;
  }

  public boolean isCurrent()
  {
    return _current;
  }

  /**
   * Returns the angle with respect to the X axis  to <code. destination </code> from the
   * current location of this pose.
   * @param destination
   * @return angle in degrees
   */
  public float angleTo(Point destination)
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return (float) _location.angleTo(destination);
  }

  /**
   * Returns the angle to <code>destination</code> relative to the pose heading;
   * @param destination  the target point
   * @return the relative bearing of the destination
   */
  public float relativeBearing(Point destination)
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return angleTo(destination) - _heading;
  }

  /**
   * Return the distance to the destination

   * @param destination
   * @return  the distance
   */
  public float distanceTo(Point destination)
  {
    if (!_current && _provider !=null) _provider.updatePose();
    return (float) _location.distance(destination);
  }

  /**
   * Returns the point at <code> distance </code> from the location of this pose,
   * in the direction  <code>bearing</code> relative to the X axis.
   * @param distance  the distance to the point
   * @param bearing  the true bearing of the point
   *  @return point
   */
  public Point pointAt(float distance, float bearing)
  {
    return _location.pointAt(distance, bearing);
  }

  public Point pointAtRelative(float distance, float relativeBearing)
  {
    return _location.pointAt(distance, relativeBearing + _heading);
  }

  protected float normalize(float angle)
  {
    while (angle < 180)angle += 360;
    while (angle > 180)  angle -= 360;
    return angle;

  }
  public  float _heading;
  public Point _location;
//  public enum Move {TRAVEL,ROTATE,ARC,NONE}

  /**
   * set by
   */
  protected boolean _current = false; //pose is up to date
  protected MoveProvider1 _provider;
  protected float _angle0;
  protected float _distance0;
}

