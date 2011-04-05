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

    public Point(float radians)
    {
        this.x = (float)Math.cos(radians);
        this.y = -(float)Math.sin(radians);
    }

/**
 * Returns the direction angle from this point to the Point p
 * @param p the Point to determine the angle to
 * @return the angle
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

    /*
     * Copy this vector to another vector
     */
    public Point copyTo(Point p)
    {
        p.x = x;
        p.y = y;
        return p;
    }

    @Override
    public Point clone()
    {
        return new Point(x, y);
    }

    public Point add(Point other)
    {
        return new Point(this.x + other.x, this.y + other.y);
    }

    public void moveTo(Point other)
    {
        x = other.x;
        y = other.y;
    }

    public Point subtract(Point other)
    {
        return new Point(this.x - other.x, this.y - other.y);
    }

    public Point subtract(float length)
    {
        return this.subtract(this.getNormalized().multiply(length));
    }

    public Point multiply(float length)
    {
        return new Point(this.x * length, this.y * length);
    }

    public Point getNormalized()
    {
        return new Point(this.x / length(), this.y / length());
    }

    public Point reverse()
    {
        return this.multiply(-1.0F);
    }

    public Point unproject(Point origin, Point xDir)
    {
        if(origin == null)
            origin = new Point(0, 0);
        xDir = xDir.getNormalized();
        Point yDir = xDir.leftOrth();
        Point offset = this.subtract(origin);
        Point np = new Point(0, 0);
        np.x = offset.dotProduct(xDir);
        np.y = offset.dotProduct(yDir);
        return np;
    }

    public Point project(Point origin, Point xDir)
    {
        Point yDir = xDir.leftOrth();
        Point p = origin.add(xDir.multiply(this.x));
        p = p.add(yDir.multiply(this.y));
        return p;
    }

    public float angle()
    {
        return (float)Math.atan2(this.y, this.x);
    }

    public Point leftOrth()
    {
        return new Point(-y, x);
    }

    public Point rightOrth()
    {
        return new Point(y, -x);
    }

    public Point addWith(Point other)
    {
        x += other.x;
        y += other.y;
        return this;
    }

    public Point subtractWith(Point other)
    {
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Point multiplyBy(float length)
    {
        x *= length;
        y *= length;
        return this;
    }

    /*
     * Returns the length of this vector
     */
    public float length()
    {
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /*
     * Sets this vector's length to 1 unit while retaining direction
     */
    public Point normalize()
    {
        float length = length();
        x /= length;
        y /= length;
        return this;
    }

    /*
     * Turns this vector into its left-handed cartesian orthagonal
     */
    public Point makeLeftOrth()
    {
        float temp = x;
        x = -y;
        y = temp;
        return this;
    }

    /*
     * Turns this vector into its right-handed cartesian orthagonal
     */
    public Point makeRightOrth()
    {
        float temp = x;
        x = y;
        y = -temp;
        return this;
    }

    public Point projectWith(Point xDir)
    {
        return projectWith(xDir, null);
    }

    public Point projectWith(Point xDir, Point origin)
    {
        float tx = 0;
        float ty = 0;
        if(origin != null)
        {
            tx = origin.x;
            ty = origin.y;
        }

        // Do projection
        this.x = tx + xDir.x * this.x - xDir.y * this.y;
        this.y = ty + xDir.y * this.x + xDir.x * this.y;
        return this;
    }

    public Point unProjectWith(Point xDir)
    {
        return unProjectWith(xDir, null);
    }

    public float dotProduct(Point other)
    {
        return this.x * other.x + this.y * other.y;
    }

    public Point unProjectWith(Point xDir, Point origin)
    {
        float tx = this.x;
        float ty = this.y;
        if(origin != null)
        {
            tx -= origin.x;
            ty -= origin.y;
        }

        // Unproject
        x = tx * xDir.x + ty * xDir.y;
        y = tx * -xDir.y + ty * xDir.x;
        return this;
    }

     /**
     * Returns a new point at the specified distance in the direction angle  from
     * this point.
     * @param distance the distance to the new point
     * @param angle the angle to the new point
     * @return the new point
     */
    public Point pointAt(float distance, float angle)
    {
      float xx = distance*(float)Math.cos(Math.toRadians(angle)) + (float)getX();
      float yy = distance*(float)Math.sin(Math.toRadians(angle)) + (float)getY();
      return new Point(xx,yy);
    }
}
