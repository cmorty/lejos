package java.awt.geom;

public abstract class Rectangle2D extends RectangularShape {
	public static class Float extends Rectangle2D {
		public float x, y, width, height;
		
		public Float(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public double getX() {
			return (double) x;
		}

		public double getY() {
			return (double) x;
		}
		
		public double getWidth() {
			return (double) width;
		}
		
		public double getHeight() {
			return (double) height;
		}

		@Override
		public boolean isEmpty() {
            return (width <= 0.0f) || (height <= 0.0f);
		}

		public Rectangle2D getBounds2D() {
			return new Float(x, y, width, height);
		}
	
	    public void setRect(float x, float y, float w, float h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
        public void setRect(Rectangle2D r) {
            this.x = (float) r.getX();
            this.y = (float) r.getY();
            this.width = (float) r.getWidth();
            this.height = (float) r.getHeight();
        }
        
        public void setRect(double x, double y, double w, double h) {
            this.x = (float) x;
            this.y = (float) y;
            this.width = (float) w;
            this.height = (float) h;
        }	
	}
	
	public static class Double extends Rectangle2D {
		public double x, y, width, height;
		
		public Double(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public double getX() {
			return x;
		}

		public double getY() {
			return x;
		}
		
		public double getWidth() {
			return width;
		}
		
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
			
	    public void setRect(double x, double y, double w, double h) {
	        this.x = x;
	        this.y = y;
	        this.width = w;
	        this.height = h;
	    }
	    
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
	
    public abstract void setRect(double x, double y, double w, double h);
	
    public void setRect(Rectangle2D r) {
        setRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
	
	@Override
    public void setFrame(double x, double y, double w, double h) {
        setRect(x, y, w, h);
    }
	
    public boolean contains(double x, double y) {
        double x0 = getX();
        double y0 = getY();
        return (x >= x0 &&
                y >= y0 &&
                x < x0 + getWidth() &&
                y < y0 + getHeight());
    }
    
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
