package java.awt;

import java.awt.geom.*;

/**
 * Represents a point in two dimensional space using integer co-ordinates
 * 
 * @author Lawrie Griffiths
 *
 */
public class Point extends Point2D {
	public int x,y;
	
	public Point() {
		this(0,0);
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
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
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
    public void move(int x, int y) {
        setLocation(x,y);
    }
    
    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
    
    public Point getLocation() {
        return new Point(x, y);
    }
	
	@Override
    public String toString() {
        return "Point[x=" + x + ",y=" + y + "]";
    }
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point pt = (Point)obj;
            return (x == pt.x) && (y == pt.y);
        }
        return super.equals(obj);
    }
}
