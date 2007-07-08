package javax.microedition.lcdui;

import lejos.nxt.LCD;

/**
 * Preliminary Graphics class for LCD Screen
 * @author Brian Bagnall
 *
 */
public class Graphics {
	/** drawArc and fillArc accuracy parameter */
	private static final int ARC_ACC = 5;

	private static final byte HEIGHT = 64; // Pixels
	private static final byte WIDTH = 100; // Pixels

	/* Public color definitions */
	public static final int BLACK = 1;
	public static final int WHITE = 0;

	/* Public line stroke definitions */
	public static final int SOLID 	= 0;
	public static final int DOTTED 	= 2;

	private int [] buff;
	private int rgbColor = BLACK;
	private int strokeStyle = SOLID;

	public Graphics() {
		 buff = new int[HEIGHT*WIDTH/32];
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
		drawLine(x0, y0, x1, y1, strokeStyle);
	}
	
	private void drawLine(int x0, int y0, int x1, int y1, int style) {
		// Uses Bresenham's line algorithm
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;
		boolean skip = false;

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
				if ((style == SOLID) || !skip)
					setPixel(rgbColor, x0, y0);
				skip = !skip;
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
				if ((style == SOLID) || !skip)
					setPixel(rgbColor, x0, y0);
				skip = !skip;
			}
		}
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		drawArc(x, y, width, height, startAngle, arcAngle, strokeStyle, false);
	}
	
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		// drawArc is for now only SOLID 
		drawArc(x, y, width, height, startAngle, arcAngle, SOLID, true);
	}

	private void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, 
			int style, boolean fill) {
		// Scale up width and height to create more accurate ellipse form
		int xscale = (width < height) ? ARC_ACC : ((ARC_ACC * width + (width >> 1)) / 

height);
		int yscale = (width < height) ? ((ARC_ACC * height + (height >> 1)) / width) : 

ARC_ACC;
		
		// Calculate x, y center and radius from upper left corner
		int x0 = x + (width >> 1);
		int y0 = y + (height >> 1);
		int radius = (width < height) ? (width >> 1) : (height >> 1);
		
		// Check and set start and end angle
		int endAngle = startAngle + arcAngle;
		while (endAngle < 0) endAngle = endAngle + 360;
		while (endAngle > 360) endAngle = endAngle - 360;
		if(arcAngle < 0) { // Switches start and end
			int temp = startAngle;
			startAngle = endAngle;
			endAngle = (temp > 0) ? temp : 360;
		}
		
		// Initialize scaled up Bresenham's circle algorithm
		int f = (1 - ARC_ACC * radius);
		int ddF_x = 0;
		int ddF_y = -2 * ARC_ACC * radius;
		int xc = 0;
		int yc = ARC_ACC * radius;
		int dotskip = 0;
		while (xc < yc) {
			if (f >= 0) { 
				yc--;
				ddF_y += 2;
				f += ddF_y;
			}
		    
			xc++;
		    ddF_x += 2;
		    f += ddF_x + 1;
		    
		    // Skip points for dotted version
		    dotskip = (dotskip + 1) % (2 * ARC_ACC);
		    if ((style == DOTTED) && !fill && (dotskip < ((2 * ARC_ACC) - 1))) continue;

		    // Scale down again
		    int xxp = (xc * xscale + (xscale >> 1)) / (ARC_ACC * ARC_ACC);
		    int xyp = (xc * yscale + (yscale >> 1)) / (ARC_ACC * ARC_ACC);
		    int yyp = (yc * yscale + (yscale >> 1)) / (ARC_ACC * ARC_ACC);
		    int yxp = (yc * xscale + (xscale >> 1)) / (ARC_ACC * ARC_ACC);
		    
		    // Calculate angle for partly circles / ellipses
		    // NOTE: Below, (float) should not be needed. Not sure why Math.round() only accepts float.
		    int tp = (int) Math.round((float)Math.toDegrees(Math.atan2(yc, xc)));
		    if (fill) {
		    	/* TODO: Optimize more by drawing horizontal lines */
		    	if (((90 - tp) >= startAngle) && ((90 - tp) <= endAngle))
		    		drawLine(x0, y0, x0 + yxp, y0 - xyp, style); // 0   - 45 degrees
		    	if ((tp >= startAngle) && (tp <= endAngle))
		    		drawLine(x0, y0, x0 + xxp, y0 - yyp, style); // 45  - 90 degrees
		    	if (((180 - tp) >= startAngle) && ((180 - tp) <= endAngle))
		    		drawLine(x0, y0, x0 - xxp, y0 - yyp, style); // 90  - 135 degrees
		    	if (((180 - (90 - tp)) >= startAngle) && ((180 - (90 - tp)) <= endAngle))
		    		drawLine(x0, y0, x0 - yxp, y0 - xyp, style); // 135 - 180 degrees
		    	if (((270 - tp) >= startAngle) && ((270 - tp) <= endAngle))
		    		drawLine(x0, y0, x0 - yxp, y0 + xyp, style); // 180 - 225 degrees
		    	if (((270 - (90 - tp)) >= startAngle) && ((270 - (90 - tp)) <= endAngle))
		    		drawLine(x0, y0, x0 - xxp, y0 + yyp, style); // 225 - 270 degrees
		    	if (((360 - tp) >= startAngle) && ((360 - tp) <= endAngle))
		    		drawLine(x0, y0, x0 + xxp, y0 + yyp, style); // 270 - 315 degrees
		    	if (((360 - (90 - tp)) >= startAngle) && ((360 - (90 - tp)) <= endAngle))
		    		drawLine(x0, y0, x0 + yxp, y0 + xyp, style); // 315 - 360 degrees
		    } else {
		    	if (((90 - tp) >= startAngle) && ((90 - tp) <= endAngle))
		    		setPixel(rgbColor, x0 + yxp, y0 - xyp); // 0   - 45 degrees
		    	if ((tp >= startAngle) && (tp <= endAngle))
		    		setPixel(rgbColor, x0 + xxp, y0 - yyp); // 45  - 90 degrees
		    	if (((180 - tp) >= startAngle) && ((180 - tp) <= endAngle))
		    		setPixel(rgbColor, x0 - xxp, y0 - yyp); // 90  - 135 degrees
		    	if (((180 - (90 - tp)) >= startAngle) && ((180 - (90 - tp)) <= endAngle))
		    		setPixel(rgbColor, x0 - yxp, y0 - xyp); // 135 - 180 degrees
		    	if (((270 - tp) >= startAngle) && ((270 - tp) <= endAngle))
		    		setPixel(rgbColor, x0 - yxp, y0 + xyp); // 180 - 225 degrees
		    	if (((270 - (90 - tp)) >= startAngle) && ((270 - (90 - tp)) <= endAngle))
		    		setPixel(rgbColor, x0 - xxp, y0 + yyp); // 225 - 270 degrees
		    	if (((360 - tp) >= startAngle) && ((360 - tp) <= endAngle))
		    		setPixel(rgbColor, x0 + xxp, y0 + yyp); // 270 - 315 degrees
		    	if (((360 - (90 - tp)) >= startAngle) && ((360 - (90 - tp)) <= endAngle))
		    		setPixel(rgbColor, x0 + yxp, y0 + xyp); // 315 - 360 degrees
		    }
		}
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) 

{

		int xc = x + (width/2);
		int yc = y + (height/2);
		int a = arcWidth/2;
		int b = arcHeight/2;

		int translateX = (width/2) - (arcWidth/2);
		int translateY = (height/2) - (arcHeight/2);

		// Draw 4 sides:
		int xDiff = arcWidth/2;
		int yDiff = arcHeight/2;
		drawLine(x, y+yDiff, x, height-yDiff);
		drawLine(width, y+yDiff, width, height-yDiff);
		drawLine(x+xDiff, y, width-xDiff, y);
		drawLine(x+xDiff, height, width-xDiff, height);


		/* e(x,y) = b^2*x^2 + a^2*y^2 - a^2*b^2 */
		int xxx = 0, yyy = b;
		int a2 = a*a, b2 = b*b;
		int crit1 = -(a2/4 + a%2 + b2);
		int crit2 = -(b2/4 + b%2 + a2);
		int crit3 = -(b2/4 + b%2);
		int t = -a2*yyy; /* e(xxx+1/2,y-1/2) - (a^2+b^2)/4 */
		int dxt = 2*b2*xxx, dyt = -2*a2*yyy;
		int d2xt = 2*b2, d2yt = 2*a2;

		while (yyy>=0 && xxx<=a) {
			setPixel(BLACK, xc+xxx + translateX, yc+yyy + translateY); // Q4
			if (xxx!=0 || yyy!=0)
				setPixel(BLACK, xc-xxx - translateX, yc-yyy - translateY); // Q2
			if (xxx!=0 && yyy!=0) {
				setPixel(BLACK, xc+xxx + translateX, yc-yyy - translateY); // Q1
				setPixel(BLACK, xc-xxx - translateX, yc+yyy + translateY); // Q3
			}
			if (t + b2*xxx <= crit1 ||   /* e(xxx+1,y-1/2) <= 0 */
			    t + a2*yyy <= crit3)      /* e(xxx+1/2,y) <= 0 */
				{xxx++; dxt += d2xt; t += dxt;} // incx()
			 else if (t - a2*yyy > crit2) /* e(xxx+1/2,y-1) > 0 */
				{yyy--; dyt += d2yt; t += dyt;}
			else {
				{xxx++; dxt += d2xt; t += dxt;} // incx()
				{yyy--; dyt += d2yt; t += dyt;}
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
	 
	public void fillRect(int x, int y, int width, int height) {
		if ((width < 0) || (height < 0))
			return;

		for(int i=y;i<y + height;i++) 
			drawLine(x, i, x + width, i);
			//for(int j=x; j<x+width;j++) // Barely faster than using lines.
				//setPixel(rgbColor, j, i);
 	}

	
	public void drawString(String str, int x, int y) {
		LCD.drawString(str, x, y);
	}

	public int getStrokeStyle() {
		return strokeStyle;
	}

	public void setStrokeStyle(int style) {
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException();
		}
		strokeStyle = style;
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
