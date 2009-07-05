package lejos.robotics;

/**
 * Exeprimental  Pose  Uses a Vector2D to contntain the location
 *
 * @author owner
 *
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.

 */
public class PoseV
{

public PoseV(float x, float y, float heading)
{
  _location = new Vector2D(x,y);
  _heading = heading;

}
/**
 * return distance to destination
 * @param destination
 * @return distance to destination
 */
public float distanceTo(Vector2D destination)
{
  return destination.subtract(_location).getR();
}
/**
 * return the true bearing (direction angle) of the  destination
 * @param destination
 * @return angle to destination
 */
public float angleTo(Vector2D destination)
{
  return destination.subtract(_location).getTheta();
}
/**
 * rotate the pose through  angle  degrees 
 * @param angle
 */
public void rotate(float angle)
{
  _heading += angle;
}
/**
 * rotate the pose so the new heading is  angle
 * @param angle
 */
public void rotateTo(float newAngle)
{
  rotate(newAngle - _heading);
}
/**
 * translate the pose by the move vector
 * @param move
 */
public void translate(Vector2D move)
{
  _location = _location.add(move);
}
/**
 * move  the pose in the directin of its heading by an amount distance
 */
public void move(float distance)
{
  Vector2D  move = new Vector2D();
  move.setPolarValues(distance, _heading);
  translate(move);
}
/**
 * return X coordinate of the pose
 * @return
 */
public float getX(){ return _location.getX();}
/**
 * return Y coordinate of the pose
 * @return
 */
public float getY(){return _location.getY();}
/**
 * return the heading (direction angle) of the pose
 * @return
 */
public float getHeading(){ return _heading ;}

private Vector2D _location;
private float _heading;

}

