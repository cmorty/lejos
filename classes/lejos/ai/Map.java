package lejos.ai;

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
  public float range(Pose pose);
  public boolean inside(Point p);
  public Rectangle getBoundingRect();
}
