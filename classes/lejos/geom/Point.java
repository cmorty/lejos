package lejos.geom;



import java.awt.geom.*;

/**
 * Point with float co-ordinates for use in navigation.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Point extends Point2D.Float {	
	public Point(float x, float y) {
		super(x,y);
	}

 /**
 * Returns the direction angle from this point to the Point p p
 * @param p
 * @return
 */
    public float angleTo(Point p)
    {
      return (float)Math.toDegrees(Math.atan2(p.getY()-y,  p.getX()-x));
    }

     /**
   *Translates this point, at location (x, y), by dx along the x axis and
   * dy along the y axis so that it now represents the point (x + dx, y + dy).
   * @param dx
   * @param dy
   */
    public void translate(float dx, float dy)
    {
      x += dx;
      y += dy;
    }

       /**
     * Returns a new point at the specified distance in the direction angle  from
     * this point.
     * @param distance
     * @param angle
     * @return
     */
    public Point pointAt(float distance, float angle)
    {
      float xx = distance*(float)Math.cos(Math.toRadians(angle)) + (float)getX();
      float yy = distance*(float)Math.sin(Math.toRadians(angle)) + (float)getY();
      return new Point(xx,yy);
    }
}
