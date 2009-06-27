package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Minimal implementation of Line2D.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Line2D implements Shape {
	public static class Float extends Line2D {
		public float x1, y1, x2, y2;
		
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
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Point2D p) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}

		public Rectangle getBounds() {
			// TODO Auto-generated method stub
			return null;
		}

		public Rectangle2D getBounds2D() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean intersects(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean intersects(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public static class Double extends Line2D {
		public double x1, y1, x2, y2;
		
		public Double() {};
		
		public Double(double x1, double y1, double x2, double y2) {
			setLine(x1, y1, x2, y2);
		}
		
		public void setLine(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public boolean contains(double x, double y) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Point2D p) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}

		public Rectangle getBounds() {
			// TODO Auto-generated method stub
			return null;
		}

		public Rectangle2D getBounds2D() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean intersects(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean intersects(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
