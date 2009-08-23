package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * An abstract class representing a line in two dimensional space
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Line2D implements Shape, Cloneable {
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Float extends Line2D {
		/**
		 * The x coordinate of the start of the line
		 */
		public float x1;
		/**
		 * The y coordinate of the start of the line
		 */
		public float y1;
		/**
		 * The x coordinate of the end of the line
		 */
		public float x2;
		/**
		 * The y coordinate of the end of the line
		 */
		public float y2;
		
		/**
		 * Creates a zero length line at (0,0)
		 */
		public Float() {};
		
		/**
		 * Create a line from (x1,y1) to (x2,y2)
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public Float(float x1, float y1, float x2, float y2) {
			setLine(x1, y1, x2, y2);
		}
		
		/**
		 * Set the float coordinates of the start and end of the line
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public void setLine(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		/**
		 * Get the bounds of the line as a Rectangle2D
		 */
		public Rectangle2D getBounds2D() {
            float x, y, w, h;
            if (x1 < x2) {
                x = x1;
                w = x2 - x1;
            } else {
                x = x2;
                w = x1 - x2;
            }
            if (y1 < y2) {
                y = y1;
                h = y2 - y1;
            } else {
                y = y2;
                h = y1 - y2;
            }
            return new Rectangle2D.Float(x, y, w, h);
		}
	
		@Override
        public double getX1() {
            return (double) x1;
        }

		@Override
        public double getY1() {
            return (double) y1;
        }

		@Override
        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

		@Override
        public double getX2() {
            return (double) x2;
        }

		@Override
        public double getY2() {
            return (double) y2;
        }

		@Override
        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }

		@Override
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = (float) x1;
			this.y1 = (float) y1;
			this.x2 = (float) x2;
			this.y2 = (float) y2;			
		}
	}
	
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Double extends Line2D {
		/**
		 * the x coordinate of the start of the line
		 */
		public double x1;
		
		/**
		 * The y coordinate of the sztart of the line
		 */
		public double y1;
		
		/**
		 * The x coordinate of the end of the line
		 */
		public double x2;
		
		/**
		 * The y coordinate of the start of the line
		 */
		public double y2;
		
		/**
		 * Create a zero length line at (0,0) with double coordinates
		 */
		public Double() {};
		
		/**
		 * Create a line from (x1,y1) to (x2,y2) with double coordinate
		 * 
		 * @param x1 the x coordinate of the start of the line
		 * @param y1 the y coordinate of the start of the line
		 * @param x2 the x coordinate of the end of the line
		 * @param y2 the y coordinate of the end of the line
		 */
		public Double(double x1, double y1, double x2, double y2) {
			setLine(x1, y1, x2, y2);
		}
		
		@Override
	    public double getX1() {
	    	return x1;
	    }

		@Override
        public double getY1() {
            return y1;
        }

		@Override
        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

		@Override
        public double getX2() {
            return x2;
        }

		@Override
        public double getY2() {
            return y2;
        }

		@Override
        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

		@Override
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		/**
		 * Get the bounds of the line as a Rectangle2D
		 */
		public Rectangle2D getBounds2D() {
            double x, y, w, h;
            if (x1 < x2) {
                x = x1;
                w = x2 - x1;
            } else {
                x = x2;
                w = x1 - x2;
            }
            if (y1 < y2) {
                y = y1;
                h = y2 - y1;
            } else {
                y = y2;
                h = y1 - y2;
            }
            return new Rectangle2D.Double(x, y, w, h);
		}
	}

	/**
	 * Get the x coordinate of the start of the line
	 * 
	 * @return the x coordinate as a double
	 */
    public abstract double getX1();

    /**
     * Get the y coordinate of the start of the line
     * 
     * @return the y coordinate as a double
     */
    public abstract double getY1();

    /**
     * Get the start point of the line as a Point2D
     * 
     * @return the Point2D
     */
    public abstract Point2D getP1();

    /**
     * Get the x coordinate of the end of the line
     * 
     * @return the x coordinate as a double
     */
    public abstract double getX2();

    /**
     * Get the y coordinate of the end of the line
     * 
     * @return the y coordinate as a double
     */
    public abstract double getY2();

    /**
     * Get the end point of the line as a Point2D
     * 
     * @return the Point2D
     */
    public abstract Point2D getP2();

    
    /**
     * Sets the end points of the line using double coordinates.
     * 
     * @param x1 the x coordinate of the start point
     * @param y1 the y coordinate of the start point
     * @param x2 the x coordinate of the end point
     * @param y2 the y coordinate of the end point
     * @since 1.2
     */
    public abstract void setLine(double x1, double y1, double x2, double y2);
    
    public boolean contains(double x, double y) {
        return false;
    }
    
	public boolean contains(Point2D p) {
		return false;
	}

	public boolean contains(double x, double y, double w, double h) {
		return false;
	}

	public boolean contains(Rectangle2D r) {
		return false;
	}
	
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(new Rectangle2D.Double(x, y, w, h));
    }
    
    /**
     * Tests if this line intersects a given line
     * 
     * @param l the given line
     * @return true iff the lines intersect
     */
    public boolean intersectsLine(Line2D l) {
        return linesIntersect(l.getX1(), l.getY1(), l.getX2(), l.getY2(),
                              getX1(), getY1(), getX2(), getY2());
    }
    
    /**
     * Test if one line intersects another line
     * 
     * @param x1 the x coordinate of the start of the first line
     * @param y1 the y coordinate of the start of the first line
     * @param x2 the x coordinate of the end of the first line
     * @param y2 the y coordinate of the end of the first line
     * @param x3 the x coordinate of the start of the second line
     * @param y3 the y coordinate of the start of the second line
     * @param x4 the x coordinate of the end of the second line
     * @param y4 the y coordinate of the end of the second line
     * @return true iff the lines intersect
     */
    public static boolean linesIntersect(
    		double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            double x4, double y4)
    {
    	// TODO
		return false;
    }
    
	public boolean intersects(Rectangle2D r) {
        //TODO
		return false;
	}

	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}
	
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new RuntimeException();
        }
    }
}
