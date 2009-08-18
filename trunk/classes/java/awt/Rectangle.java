package java.awt;

import java.awt.geom.*;

/**
 * Minimal Rectangle implementation.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Rectangle extends Rectangle2D implements Shape {
	public int height, width, x, y;
	
	/**
	 * Creates a rectangle with top left corner at (x,y) and with specified
	 * width and height.
	 * @param x the x coordinate of the top left corner
	 * @param y the y coordinate of the top left corner
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Creates a rectangle with top left corner at (0,0) and with specified
	 * width and height.
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public Rectangle(int width, int height) {
		this(0,0,width,height);
	}
	
	/**
	 * Creates an empty rectangle at (0,0).
	 */
	public Rectangle() {
		this(0,0);
	}
	
	/**
	 * Get the x coordinate as a double
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Get the y coordinate as a double
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Get the width as a double
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Get the height as a double
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	
	/**
	 * Move the rectangle to (x,y)
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Test if the rectangle is empty
	 * @return true iff the rectangle is empty
	 */
	public boolean isEmpty() {
		return (width <= 0 || height <= 0);
	}
	
	/**
	 * Test if a point given by (x,y) coordinates are with the rectangle
	 * @param pointX the x coordinate
	 * @param pointY the y coordinate
	 * @return true iff the point is within the rectangle
	 */
	public boolean contains(int pointX, int pointY) {
		if (isEmpty()) return false;
		if (pointX < this.x || pointX > this.x + width) return false;
		if (pointY < this.y || pointY > this.y + height) return false;
		return true;
	}
	
	/**
	 * Test if this rectangle contains a specified rectangle
	 * @param r the specified rectangle
	 * @return true iff the specified rectangle is contained within this rectangle
	 */
	public boolean contains(Rectangle r) {
		if (isEmpty() || r.isEmpty()) return false;
		return contains(r.x, r.y) && contains(r.x + r.width, r.y + r.height);
	}
	
	/** 
	 * Test if this rectangle intersects a specified rectangle
	 * @param r the given rectangle
	 * @return true iff this rectangle intersects the given rectangle
	 */
	public boolean intersects(Rectangle r) {
		if (isEmpty() || r.isEmpty()) return false;
		if (r.x + r.width < x) return false;
		if (r.x > x + width) return false;
		if (r.y + r.height < y) return false;
		if (r.y > y + height) return false;
		return true;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public Rectangle2D getBounds2D() {
		return new Rectangle(x, y, width, height);
	}
	
    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }
    
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

	@Override
	public void setRect(double x, double y, double w, double h) {
        int newx, newy, neww, newh;

        if (x > 2.0 * Integer.MAX_VALUE) {
            // Cannot be sensibly represented with integers
            newx = Integer.MAX_VALUE;
            neww = -1;
        } else {
            newx = doubleToInt(x, false);
            if (width >= 0) width += x-newx;
            neww = doubleToInt(width, width >= 0);
        }

        if (y > 2.0 * Integer.MAX_VALUE) {
        	// Cannot be sensibly represented with integers
            newy = Integer.MAX_VALUE;
            newh = -1;
        } else {
            newy = doubleToInt(y, false);
            if (height >= 0) height += y-newy;
            newh = doubleToInt(height, height >= 0);
        }

        reshape(newx, newy, neww, newh);		
	}
	
    @Deprecated
    public void reshape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    private static int doubleToInt(double x, boolean high) {
        if (x <= Integer.MIN_VALUE) return Integer.MIN_VALUE;  
        if (x >= Integer.MAX_VALUE) return Integer.MAX_VALUE;

        return (int) (high ? Math.ceil(x) : Math.floor(x));
    }
}
