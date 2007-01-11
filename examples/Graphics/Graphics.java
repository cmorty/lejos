

import lejos.nxt.LCD;

public class Graphics {
	private static final byte HEIGHT = 64; // Pixels
	private static final byte WIDTH = 100; // Pixels
	private int [] buff;

	public Graphics() {
		 buff = new int[HEIGHT*WIDTH/32];
	}
	
	// TESTING PURPOSES ONLY
	public static void main(String [] options) throws Exception {
		Graphics g = new Graphics();
		g.drawLine(5,5,60,60);
		g.drawRect(62, 10, 25, 35);
		g.refresh();
	}

	public void setPixel(int x, int y) {
		int xChar = x / 4;
		int yChar = y / 8;
		int index = yChar * 25 + xChar;
		int specificBit = (y % 8) + ((x % 4) * 8);
		buff[index] = buff[index] | (1 << specificBit);
	}

	public void drawLine(int x0, int y0, int x1, int y1) {
		// Uses Bresenham's line algorithm
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;

		if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
		if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
		dy <<= 1; // dy is now 2*dy
		dx <<= 1; // dx is now 2*dx

		setPixel(x0, y0);
		if (dx > dy) {
			int fraction = dy - (dx >> 1);  // same as 2*dy - dx
			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx; // same as fraction -= 2*dx
				}
				x0 += stepx;
				fraction += dy; // same as fraction -= 2*dy
				setPixel(x0, y0);
			}
	        	} else {
				int fraction = dx - (dy >> 1);
				while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;
				}
				y0 += stepy;
				fraction += dx;
				setPixel(x0, y0);
			}
		}
	}

	public void drawRect(int x, int y, int width, int height) {
		if ((width < 0) || (height < 0))
			return;

		if (height == 0 || width == 0) {
	    		drawLine(x, y, x + width, y + height);
		} else {
	    	drawLine(x, y, x + width - 1, y);
			drawLine(x + width, y, x + width, y + height - 1);
			drawLine(x + width, y + height, x + 1, y + height);
			drawLine(x, y + height, x, y + 1);
		}
    }
	
	public void drawString(String str, int x, int y) {
		LCD.drawString(str, x, y);
	}

	// Temp for testing purposes until canvas made.
	public void refresh() {
		LCD.setDisplay(buff);
		LCD.refresh(); // Unsure if needed
	}
}
