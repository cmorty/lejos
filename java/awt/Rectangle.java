package java.awt;

/**
 * Minimal Rectangle implementation.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Rectangle {
	public int height, width, x, y;
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
