package java.awt;

import java.awt.geom.*;

/**
 * Minimal Point implementation.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Point extends Point2D {
	public int x,y;
	
	public Point(int x, int y) {
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
