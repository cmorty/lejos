package lejos.localization;

/**
 * Represents a point using float coordinates 
 * 
 * @author Lawrie Griffiths
 * 
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
public class Point {
  public float x, y;

  /** 
   * Create a point from coordinates
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }
}
