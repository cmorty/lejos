package java.awt.geom;

/**
 * Minimal Point2D implementation
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class Point2D {
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
	}
	
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
	}
	
	public abstract double getX();
	
	public abstract double getY();
	
}
