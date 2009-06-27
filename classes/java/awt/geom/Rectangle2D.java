package java.awt.geom;

import java.awt.Rectangle;

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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			
		}

		public boolean contains(double x, double y) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Point2D p) {
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			
		}

		public boolean contains(double x, double y) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean contains(Point2D p) {
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
	
	public boolean contains(double x, double y, double w, double h) {
		if (isEmpty()) return false;
		return contains(x, y) && contains(x + w, y + h);
	}
}
