package java.awt;

import java.awt.geom.*;

/**
 * Represents a point in two dimensional space using integer co-ordinates
 * 
 * @author Lawrie Griffiths
 *
 */
public class Point extends Point2D {
	/**
	 * The x coordinate of the point
	 */
	public int x;
	/**
	 * The y coordinate of the point
	 */
	public int y;
	
	/**
	 * Create a point at (0,0)
	 */
	public Point() {
		this(0,0);
	}
	
	/**
	 * Create a point at (x,y)
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Create a point from a given point
	 * 
	 * @param p the given point
	 */
	public Point(Point p) {
		this(p.x, p.y);
	}

	@Override
	public double getX() {
		return (double) x;
	}

	@Override
	public double getY() {
		return (double) y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	
	/**
	 * Set the location of the point using integer coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the location of the point to a given point
	 * 
	 * @param p the given point
	 */
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
	/**
	 * Move the location of the point to (x,y)
	 * 
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 */
    public void move(int x, int y) {
        setLocation(x,y);
    }
    
    /**
     * Move the point by the vector (dx, dy)
     * 
     * @param dx the increment of the x coordinate
     * @param dy the increment of the y coordinate
     */
    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
    
    /**
     * Get the location of the point
     * 
     * @return the location (i.e. the point itself)
     */
    public Point getLocation() {
        return new Point(x, y);
    }
	
    /** 
     * Represent the point as a String
     */
	@Override
    public String toString() {
        return "Point[x=" + x + ",y=" + y + "]";
    }
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point pt = (Point)obj;
            return x == pt.x && y == pt.y;
        }
        return super.equals(obj);
    }
}
