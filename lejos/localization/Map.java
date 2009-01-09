package lejos.localization;

import java.awt.Rectangle;

/**
 * The Map interface supports determining the range to a feature on the map
 * (such as a wall), from an object with a specific pose.
 * 
 * It also supports the a method to determine if a point is within the mapped
 * area.
 * 
 * @author Lawrie Griffiths
 * 
 */
public interface Map {
	/**
	 * The the range to the nearest wall (or other feature)_
	 * @param pose the pose of the robot
	 * @return the range
	 */
  public float range(Pose pose);
  
  /**
   * Test if a point is within the mapped area
   * 
   * @param p the point
   * @return true iff the point is within the mapped area
   */
  public boolean inside(Point p);
  
  /**
   * Get the bounding rectangle for the mapped area
   * 
   * @return the bounding rectangle
   */
  public Rectangle getBoundingRect();
}
