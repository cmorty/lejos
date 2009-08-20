package java.awt.geom;

/**
 * An abstract class for a point.
 * Subclasses implement float, double and integer coordinates.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Point2D {
	/**
	 * A point with float coordinates.
	 */
	public static class Float extends Point2D {
		public float x, y;
		
		public Float() {}
		
		public Float(float x, float y) {
			this.x = x;
			this.y = y;
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
			this.x = (float) x;
			this.y = (float) y;			
		}
		
		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;			
		}

		@Override
        public String toString() {
            return "Point2D.Float["+x+", "+y+"]";
        }
	}
	
	/**
	 * A point with double coordinates.
	 */
	public static class Double extends Point2D {
		public double x,y;
		
		public Double() {}
		
		public Double(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;			
		}
		
		@Override
        public String toString() {
            return "Point2D.Double["+x+", "+y+"]";
        }
	}
	
	/**
	 * Get the x co-ordinate as a double
	 * 
	 * @return the x co-ordinate (as a double) 
	 */
	public abstract double getX();
	
	/**
	 * Get the y co-ordinate as a double
	 * 
	 * @return the y co-ordinate (as a double)
	 */
	public abstract double getY();
	
	/**
	 * Set the location of this Point2D using double co-ordinates
	 * 
	 * @param x the x co-ordinate
	 * @param y the y co-ordinate
	 */
	public abstract void setLocation(double x, double y);
	
	/**
	 * Set the location of this Point2D to the same as a specified Point2D
	 * 
	 * @param p the specified Point2D
	 */
    public void setLocation(Point2D p) {
        setLocation(p.getX(), p.getY());
    }
    
    public static double distanceSq(double x1, double y1, double x2, double y2) {
    	double tx = x1 - x2;
    	double ty = y1 - y2;
    	return (tx * tx + ty * ty);
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
    	return Math.sqrt(distanceSq(x1,y1,x2,y2));
    }
    
    public double distanceSq(double px, double py) {
        double tx = px - getX();
        double ty = py - getY();
        return (tx * tx + ty * ty);
    }
    
    public double distanceSq(Point2D pt) {
        return distanceSq(pt.getX(), pt.getY());
    }
    
    public double distance(double px, double py) {
    	return Math.sqrt(distanceSq(px,py));
    }
    
    public double distance(Point2D pt) {
    	return Math.sqrt(distanceSq(pt));
    }
    
    public boolean equals(Object obj) {
	    if (obj instanceof Point2D) {
	        Point2D p2d = (Point2D) obj;
	        return (getX() == p2d.getX()) && (getY() == p2d.getY());
	    }
	    return super.equals(obj);
    }
}
