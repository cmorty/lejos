package java.awt.geom;

/**
 * An abstract class for a Rectangle.
 * Subclasses use float, double or integer coordinates.
 * 
 * @author Lawrie
 *
 */
public abstract class Rectangle2D extends RectangularShape {
	/**
	 * A Rectangle2D with float coordinates.
	 */
	public static class Float extends Rectangle2D {
		/**
		 * The x coordinate of the top left corner
		 */
		public float x;
		
		/**
		 * The y coordinate of the top right corner
		 */
		public float y;
		
		/**
		 * The width of the rectangle
		 */
		public float width;
		
		/**
		 * The height of the rectangle;
		 */
		public float height;
		
		/**
		 * Create a rectangle with float coordinates
		 * 
		 * @param x the x coordinate of the top left corner
		 * @param y the y coordinate of the top left corner
		 * @param width the width of the rectangle
		 * @param height the height of the rectangle
		 */
		public Float(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public double getX() {
			return (double) x;
		}

		@Override
		public double getY() {
			return (double) x;
		}
		
		@Override
		public double getWidth() {
			
			return (double) width;
		}
		@Override
		public double getHeight() {
			return (double) height;
		}

		@Override
		public boolean isEmpty() {
            return (width <= 0.0f) || (height <= 0.0f);
		}

		/**
		 * Get the bounds as a Rectangle2D with float coordinates
		 * @return the bounding rectangle
		 */
		public Rectangle2D getBounds2D() {
			return new Float(x, y, width, height);
		}
	
		/**
		 * Set the rectangle using float coordinates
		 * 
		 * @param x the x coordinate of the top left corner
		 * @param y the y coordinate of the top left corner
		 * @param w the width
		 * @param h the height
		 */
	    public void setRect(float x, float y, float w, float h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
	    @Override
        public void setRect(Rectangle2D r) {
            this.x = (float) r.getX();
            this.y = (float) r.getY();
            this.width = (float) r.getWidth();
            this.height = (float) r.getHeight();
        }
        
	    @Override
        public void setRect(double x, double y, double w, double h) {
            this.x = (float) x;
            this.y = (float) y;
            this.width = (float) w;
            this.height = (float) h;
        }	
	}
	
	/**
	 * A Rectangle2D with double coordinates
	 */
	public static class Double extends Rectangle2D {
		/**
		 * The x coordinate of the top left corner
		 */
		public double x;
		
		/**
		 * The y coordinate of the top right corner
		 */
		public double y;
		
		/**
		 * The width of the rectangle
		 */
		public double width;
		
		/**
		 * The height of the rectangle;
		 */
		public double height;
		
		public Double(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return x;
		}
		
		@Override
		public double getWidth() {
			return width;
		}
		
		@Override
		public double getHeight() {
			return height;
		}

		@Override
		public boolean isEmpty() {
            return (width <= 0.0f) || (height <= 0.0f);
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {
			setRect(x,y,w,h);			
		}

		public Rectangle2D getBounds2D() {
			return new Double(x, y, width, height);
		}
		
		@Override
	    public void setRect(double x, double y, double w, double h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
		@Override
        public void setRect(Rectangle2D r) {
            this.x = r.getX();
            this.y = r.getY();
            this.width = r.getWidth();
            this.height = r.getHeight();
        }
	}
	
	public boolean contains(double x, double y, double w, double h) {
		if (isEmpty()) return false;
		return contains(x, y) && contains(x + w, y + h);
	}
	
	/**
	 * Set this rectangle to a rectangle defined by double coordinates
	 * 
	 * @param x the x coordinate of the top left corner
	 * @param y the y coordinate of the top right corner
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 */
    public abstract void setRect(double x, double y, double w, double h);
	
    /**
     * Set this Rectangle2D to be the same as a given Rectangle2D
     * 
     * @param r the Rectangle2D
     */
    public void setRect(Rectangle2D r) {
        setRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
	
	@Override
    public void setFrame(double x, double y, double w, double h) {
        setRect(x, y, w, h);
    }
	
	/**
	 * Test if this Rectangle2D contains a rectangle defined by double coordinates
	 */
    public boolean contains(double x, double y) {
        double x0 = getX();
        double y0 = getY();
        return (x >= x0 &&
                y >= y0 &&
                x < x0 + getWidth() &&
                y < y0 + getHeight());
    }
    
    /**
     * Test if this Rectangle2D intersects a rectangle deined by double coordinates
     */
    public boolean intersects(double x, double y, double w, double h) {
        if (isEmpty() || w <= 0 || h <= 0) return false;
        double x0 = getX();
        double y0 = getY();
        return (x + w > x0 &&
                y + h > y0 &&
                x < x0 + getWidth() &&
                y < y0 + getHeight());
    }    
}
