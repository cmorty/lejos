package lejos.localization;

/**
 * Represents a pose of the robot. 
 * The pose is its x,y position and its heading angle.
 * Also supports generation of a line representing a range
 * measurement from this pose.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class Pose {
  private static float maxRange = 550f;
  public float x, y, angle;

  public Pose(float x, float y, float angle) {
    this.x = x;
    this.y = y;
    this.angle = angle;
  }

  /**
   * Generate a line representing a range reading 
   * from a range sensor with this pose
   * 
   * @return the line
   */
  public Line getRangeLine() {
    return new Line(x, y, x + maxRange
        * (float) Math.cos(Math.toRadians(angle)), y + maxRange
        * (float) Math.sin(Math.toRadians(angle)));
  }
  
  /**
   * Set the maximum value for a range reading from this pose
   * 
   * @param range the maximum range
   */
  public void setMaxRange(float range) {
    maxRange = range;
  }
}
