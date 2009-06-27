package lejos.robotics.localization;

import lejos.geom.*;

/**
 * Represents a pose of the robot. 
 * The pose is its x,y position and its heading angle.
 * Also supports generation of a line representing a range
 * measurement from this pose.
 * 
 * @author Lawrie Griffiths
 * 
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
public class Pose {
  private static float maxRange = 254f;
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
