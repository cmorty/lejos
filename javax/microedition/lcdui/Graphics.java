package java.awt;

import lejos.nxt.LCD;

/**
 * Preliminary Graphics class for LCD Screen
 * @author Brian Bagnall
 *
 */
public class Graphics {
	private static final byte HEIGHT = 64; // Pixels
	private static final byte WIDTH = 100; // Pixels
	private int [] buff;
	private int rgbColor = BLACK;
	
	public static final int BLACK = 1;
	public static final int WHITE = 0;

	public Graphics() {
		 buff = new int[HEIGHT*WIDTH/32];
	}
	
	// TESTING PURPOSES ONLY
	public static void main(String [] options) throws Exception {
		Graphics g = new Graphics();
		for(int i=0;i<HEIGHT;i=i+2)
			g.drawLine(0,HEIGHT - 1 - i,i,0);
		g.refresh();
		Thread.sleep(5000);
		g.clear();
		for(int i=0;i<HEIGHT;i=i+6)
			g.drawRect(0, 0, i, i);
		g.refresh();
		Thread.sleep(5000);
		g.clear();
		for(int i=0;i<8;i++)
			g.drawOval(WIDTH/2, HEIGHT/2, 7*i, 8*(7-i));
		g.refresh();
	}

	/**
	* Using rgbColor as argument even though global, because when this
	* setPixel() method is used later it will need color argument
	*/
	public void setPixel(int rgbColor, int x, int y) {
		if(x<0||x>=WIDTH||y<0||y>=HEIGHT) return; // Test-Modify for speed
		int xChar = x / 4;
		int yChar = y / 8;
		int index = yChar * 25 + xChar;
		int specificBit = (y % 8) + ((x % 4) * 8);
		buff[index] = buff[index] | (rgbColor << specificBit);
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

		setPixel(rgbColor,x0, y0);
		if (dx > dy) {
			int fraction = dy - (dx >> 1);  // same as 2*dy - dx
			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx; // same as fraction -= 2*dx
				}
				x0 += stepx;
				fraction += dy; // same as fraction -= 2*dy
				setPixel(rgbColor,x0, y0);
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
				setPixel(rgbColor,x0, y0);
			}
		}
	}

	public void drawArc(int x, int yME, int width, int height, int startAngle, int arcAngle) {
		// Modified McIlroy's ellipse algorithm
		int endAngle = startAngle + arcAngle;
		if(endAngle<0) endAngle = endAngle + 360;
		if(endAngle>360) endAngle = endAngle - 360;
		if(arcAngle < 0) { // Switches start and end
			int temp = startAngle;
			startAngle = endAngle;
			endAngle = temp;
		}
		
		int xc = x + (width/2); // X-center
		int yc = yME + (height/2); // Y-center
		int a = width/2;
		int b = height/2;
	
		/* e(x,y) = b^2*x^2 + a^2*y^2 - a^2*b^2 */
		int xxx = 0, y = b;
		int a2 = a*a, b2 = b*b;
		int crit1 = -(a2/4 + a%2 + b2);
		int crit2 = -(b2/4 + b%2 + a2);
		int crit3 = -(b2/4 + b%2);
		int t = -a2*y; /* e(xxx+1/2,y-1/2) - (a^2+b^2)/4 */
		int dxt = 2*b2*xxx, dyt = -2*a2*y;
		int d2xt = 2*b2, d2yt = 2*a2;
		int count = 0; // DELETE ME!!
		while (y>=0 && xxx<=a) {
			float angle = (float)Math.toDegrees(Math.atan2(y, xxx));
	
			if(startAngle < endAngle) {
				if(360 - angle >= startAngle && 360 - angle <= endAngle)
					setPixel(BLACK, xc+xxx, yc+y); // Quadrant 4: lower-right
				if (xxx!=0 || y!=0)
					if(180 - angle >= startAngle && 180 - angle <= endAngle)
						setPixel(BLACK, xc-xxx, yc-y); // Quadrant 2: Upper-left
				if (xxx!=0 && y!=0) {
					if(angle >= startAngle && angle <= endAngle)
						setPixel(BLACK, xc+xxx, yc-y); // Quadrant 1: Upper-right
					if(180 + angle >= startAngle && 180 + angle <= endAngle)
						setPixel(BLACK, xc-xxx, yc+y); // Quadrant 3: Lower-left
				}
			} else {
				if(360 - angle >= startAngle)
					setPixel(BLACK, xc+xxx, yc+y); // Quadrant 4: lower-right
				if (xxx!=0 || y!=0)
					if(180 - angle <= endAngle) 
						setPixel(BLACK, xc-xxx, yc-y); // Quadrant 2: Upper-left
				if (xxx!=0 && y!=0) {
					if(angle <= endAngle)
						setPixel(BLACK, xc+xxx, yc-y); // Quadrant 1: Upper-right
					if(180 + angle >= startAngle)
						setPixel(BLACK, xc-xxx, yc+y); // Quadrant 3: Lower-left
				}
			}

			if (t + b2*xxx <= crit1 ||   /* e(xxx+1,y-1/2) <= 0 */
			    t + a2*y <= crit3)      /* e(xxx+1/2,y) <= 0 */
				{xxx++; dxt += d2xt; t += dxt;} // incx()
			 else if (t - a2*y > crit2) /* e(xxx+1/2,y-1) > 0 */
				{y--; dyt += d2yt; t += dyt;} // incy()
			else {
				{xxx++; dxt += d2xt; t += dxt;} // incx()
				{y--; dyt += d2yt; t += dyt;} // incy()
			}
		}
	}
	
	public void drawOval(int x, int y, int width, int height) {
		
		int r1 = width/2;
		int r2 = height/2;
		
		int rr1 =  r1 * r1;
		int r21 = rr1 + rr1;
		int r41 = r21 + r21;
		int rr2 = r2 * r2;
		int r22 = rr2 + rr2;
		int r42 = r22 + r22;
		int k = r2 * r21;
		int p = r41 + r42;
		int cc = r22 + rr1 - k;
		k = k + k; // Addition faster than mult.
		int rf2 = -r41;
		r22 = r22 + p;
		int xx = 0;
		int yy = r2;
		do {
			ePlot(x, y, xx, yy);
		     if(cc >= 0) {
		    	yy = yy - 1;
		        k = k - r41;
		        cc = cc - k;
		     }
		     cc = cc + rf2 + r22;
		     rf2 = rf2 + r42;
		     xx = xx + 1;
		} while(rf2 <= k);
		
		r22 = r22 - p;
		k = r1 * r22;
		cc = r21 + rr2 - k;
		k = k + k; // Addition faster than mult.
		int rf1 = -r42;
		r21 = r21 + p;
		xx = r1;
		yy = 0;
		do {
			ePlot(x, y, xx, yy);
		    if(cc >= 0) {
		    	xx = xx - 1;
		        k = k - r42;
		        cc = cc - k;
		    }
		    cc = cc + rf1 + r21;
		    rf1 = rf1 + r41;
		    yy = yy + 1;
		} while(rf1 <= k);
	}

	private void ePlot(int x, int y, int a, int b) {
		setPixel(rgbColor,x + a, y + b);
		setPixel(rgbColor,x + a, y - b);
		setPixel(rgbColor,x - a, y + b);
		setPixel(rgbColor,x - a, y - b);
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

	// Temp for testing purposes until Canvas made.
	public void refresh() {
		LCD.setDisplay(buff);
		LCD.refresh(); // Unsure if needed
	}
	
	// Temp method for testing. Clears out graphics buffer
	// and refreshes screen.
	public void clear() {
		for(int i=0;i<buff.length;i++)
			buff[i] = 0;
		refresh();
	}
}