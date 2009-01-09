package lejos.localization;

/**
 * Represents a point using float coordinates 
 * 
 * @author Lawrie Griffiths
 * 
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
