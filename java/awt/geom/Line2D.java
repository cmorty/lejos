package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * An abstract class representing a line in two dimensional space
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Line2D implements Shape {
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Float extends Line2D {
		public float x1;
		public float y1;
		public float x2;
		public float y2;
		
		public Float() {};
		
		public Float(float x1, float y1, float x2, float y2) {
			setLine(x1, y1, x2, y2);
		}
		
		public void setLine(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

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

		public Rectangle getBounds() {
			return getBounds2D().getBounds();
		}

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

		public boolean intersects(double x, double y, double w, double h) {
			return intersects(new Rectangle2D.Double(x, y, w, h));
		}

		public boolean intersects(Rectangle2D r) {
	        //TODO
			return false;
		}
		
        public double getX1() {
            return (double) x1;
        }

        public double getY1() {
            return (double) y1;
        }

        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

        public double getX2() {
            return (double) x2;
        }

        public double getY2() {
            return (double) y2;
        }

        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }
	}
	
	/**
	 * A line in 2D space using float coordinates
	 */
	public static class Double extends Line2D {
		public double x1;
		public double y1;
		public double x2;
		public double y2;
		
		public Double() {};
		
		public Double(double x1, double y1, double x2, double y2) {
			setLine(x1, y1, x2, y2);
		}
		
	    public double getX1() {
	    	return x1;
	    }

        public double getY1() {
            return y1;
        }

        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

		
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

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

		public Rectangle getBounds() {
			return getBounds2D().getBounds();
		}

		public boolean intersects(double x, double y, double w, double h) {
			return intersects(new Rectangle2D.Double(x, y, w, h));
		}

		public boolean intersects(Rectangle2D r) {
	        //TODO
			return false;
		}

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

    public abstract double getX1();

    public abstract double getY1();

    public abstract Point2D getP1();

    public abstract double getX2();

    public abstract double getY2();

    public abstract Point2D getP2();

    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        //TODO
    	return false;
    }
}
