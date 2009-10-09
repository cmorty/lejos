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
  
    public float angleTo(Point p)
    {
      return (float)Math.toDegrees(Math.atan2(p.getY()-y,  p.getX()-x));
    }
    public void translate(float dx, float dy)
    {
      x += dx;
      y += dy;
    }
}
