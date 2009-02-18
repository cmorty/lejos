
package lejos.nxt;

/**
 * Text and graphics output to the LCD display.
 * 
 * @author Andre Nijholt and BB bitBlt and other mods Andy Shaw
 */
public class LCD {
	public static final int SCREEN_WIDTH 	= 100;
	public static final int SCREEN_HEIGHT 	= 64;
	
	public static final int DISPLAY_WIDTH 	= 100;
	public static final int DISPLAY_DEPTH 	= 8;	// 8 * 8 bits = 64 pixels
	
	public static final int NOOF_CHARS 		= 128;
	public static final int FONT_WIDTH 		= 5;
	public static final int FONT_HEIGHT		= 8;
	public static final int CELL_WIDTH 		= FONT_WIDTH + 1;
	public static final int CELL_HEIGHT		= FONT_HEIGHT;
	
	public static final int DISPLAY_CHAR_WIDTH = DISPLAY_WIDTH / CELL_WIDTH;
	public static final int DISPLAY_CHAR_DEPTH = DISPLAY_DEPTH;

    private static byte[] font = getSystemFont();
	private static byte [] displayBuf = getDisplay();
	
	/**
	 * Common raster operations for use with bitBlt
	 */
	public static final int ROP_CLEAR		=	0x00000000;
	public static final int ROP_AND			=	0xff000000;
	public static final int ROP_ANDREVERSE	=	0xff00ff00;
	public static final int ROP_COPY		=	0x0000ff00;
	public static final int ROP_ANDINVERTED	=	0xffff0000;
	public static final int ROP_NOOP		=	0x00ff0000;
	public static final int ROP_XOR			=	0x00ffff00;
	public static final int ROP_OR			=	0xffffff00;
	public static final int ROP_NOR			=	0xffffffff;
	public static final int ROP_EQUIV		=	0x00ffffff;
	public static final int ROP_INVERT		=	0x00ff00ff;
	public static final int ROP_ORREVERSE	=	0xffff00ff;
	public static final int ROP_COPYINVERTED=	0x0000ffff;
	public static final int ROP_ORINVERTED	=	0xff00ffff;
	public static final int ROP_NAND		=	0xff0000ff;
	public static final int ROP_SET			=	0x000000ff;

	/**
	 * Standard two input BitBlt function with the LCD display as the
	 * destination. Supports standard raster ops and
	 * overlapping images. Images are held in native leJOS/Lego format.
	 * @param src byte array containing the source image
	 * @param sw Width of the source image
	 * @param sh Height of the source image
	 * @param sx X position to start the copy from
	 * @param sy Y Position to start the copy from
	 * @param dx X destination
	 * @param dy Y destination
	 * @param w width of the area to copy
	 * @param h height of the area to copy
	 * @param rop raster operation.
	 */
	public static void bitBlt(byte [] src, int sw, int sh, int sx, int sy, int dx, int dy, int w, int h, int rop)
	{
		bitBlt(src, sw, sh, sx, sy, displayBuf, SCREEN_WIDTH, SCREEN_HEIGHT, dx, dy, w, h, rop);	
	}
	/**
	 * Special case bitBlt with no input image. Can be used to clear areas
	 * draw rectangles etc.
	 * @param dx X destination
	 * @param dy Y destination
	 * @param w width of the area to copy
	 * @param h height of the area to copy
	 * @param rop raster operation.
	 */	
	public static void bitBlt(int dx, int dy, int w, int h, int rop)
	{
		bitBlt(displayBuf, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, displayBuf, SCREEN_WIDTH, SCREEN_HEIGHT, dx, dy, w, h, rop);	
	}	
	/**
	 * Method to set a pixel to screen.
	 * @param rgbColor the pixel color (0 = white, 1 = black)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public static void setPixel(int rgbColor, int x, int y) {
		if (x < 0 || x >= SCREEN_WIDTH || y < 0 || y >= SCREEN_HEIGHT) return; // Test-Modify for speed
		int bit = (y & 0x7);
		int index = (y/8)*DISPLAY_WIDTH + x;
		displayBuf[index] = (byte)((displayBuf[index] & ~(1 << bit)) | (rgbColor << bit));
	}
	
	/**
	 * Method to get a pixel from the screen.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the pixel color (0 = white, 1 = black)
	 */
	public static int getPixel(int x, int y) {
		if (x < 0 || x >= SCREEN_WIDTH || y < 0 || y >= SCREEN_HEIGHT) return 0; 
		int bit = (y & 0x7);
		int index = (y/8)*DISPLAY_WIDTH + x;
		return ((displayBuf[index] >> bit) & 1);
	}
	
	/**
	 * Output a string to the display. Allow any location. Allow the string to
	 * be inverted.
	 * @param str String to display	
	 * @param x X location (pixels)
	 * @param y Y location (pixels)
	 * @param invert set to true to displayed the string inverted
	 */
	public static void drawString(String str, int x, int y, boolean invert) {
		char [] strData = str.toCharArray();
		if (invert)
		{
			for (int i = 0; (i < strData.length); i++) {
				drawChar(strData[i], x + i*CELL_WIDTH, y, true);
			}
		}
		else
		{
			for (int i = 0; (i < strData.length); i++) {
				drawChar(strData[i], x + i*CELL_WIDTH, y, ROP_COPY);
			}
		}		
		
	}
	/**
	 * Draw a single char to an arbitary location on the screen.
	 * @param c Character to display
	 * @param x X location (pixels)
	 * @param y Y location (pixels)
	 * @param invert Set to true to invert the display
	 */
	public static void drawChar(char c, int x, int y, boolean invert) {
		bitBlt(font, FONT_WIDTH*128, FONT_HEIGHT, FONT_WIDTH*c, 0, x, y, FONT_WIDTH, FONT_HEIGHT, (invert ? ROP_COPYINVERTED : ROP_COPY));
		if (invert) bitBlt(x+FONT_WIDTH, y, 1, FONT_HEIGHT, ROP_SET);
	}
	
	/**
	 * Output a string to the display. Allow any location. Allow use of raster
	 * operations.
	 * @param str String to display	
	 * @param x X location (pixels)
	 * @param y Y location (pixels)
	 * @param rop Raster operation
	 */
	public static void drawString(String str, int x, int y, int rop) {
		char [] strData = str.toCharArray();
		for (int i = 0; (i < strData.length); i++) {
			drawChar(strData[i], x + i*CELL_WIDTH, y, rop);
		}
	}

	/**
	 * Draw a single char to an arbitary location on the screen.
	 * @param c Character to display
	 * @param x X location (pixels)
	 * @param y Y location (pixels)
	 * @param rop Raster operation for how to combine with existing content
	 */
	public static void drawChar(char c, int x, int y, int rop) {
		bitBlt(font, FONT_WIDTH*128, FONT_HEIGHT, FONT_WIDTH*c, 0, x, y, FONT_WIDTH, FONT_HEIGHT, rop);
	}
	
	/**
	 * Draw pixels a byte at a time. Should probably no longer be used.
	 * @param b
	 * @param x
	 * @param y
	 * @param invert
	 */
	public static void drawPixels(byte b, int x, int y, boolean invert) {
		int index = (y/8)*DISPLAY_WIDTH + x;
		displayBuf[index] |= ((invert ? (b ^ 0xFF) : b));
	}

	public static void clearDisplay() {
		clear();
	}
	
	public static void setDisplay() {
	}

	/**
	 * Display a string on the LCD at specified x,y co-ordinate.
	 */
	public static native void drawString(String str, int x, int y);

	/**
	 * Display an int on the LCD at specified x,y co-ordinate.
	 */
	public static native void drawInt(int i, int x, int y);

	/**
	 * Display an in on the LCD at x,y with leading spaces to occupy at least the number
	 * of characters specified by the places parameter.
	 */
	public static native void drawInt(int i, int places, int x, int y);

	/**
	 * Update the display.
	 */
	public static native void refresh();
	  
	/**
	 * Clear the display.
	 */
	public static native void clear();
	  
	/**
	 * Write graphics from a Java buffer to the display.
	 */
	public static native void setDisplay(int[] buff);
	
	/**
	 * Provide access to the LCD display frame buffer. Allows both the firmware
	 * and Java to make changes.
	 * @return byte array that is the frame buffer.
	 */
	public static native byte [] getDisplay();
    
	/**
	 * Provide access to the LCD system font. Allows both the firmware
	 * and Java to share the same font bitmaps.
	 * @return byte array that is the frame buffer.
	 */
	public static native byte [] getSystemFont();
	
	/**
	 * Turn on/off the automatic refresh of the LCD display. At system startup
	 * auto refresh is on.
	 * @param mode 1 to enable 0 to disable
	 */
	public static native void setAutoRefresh(int mode);
	
	/**
	 * Standard two input BitBlt function. Supports standard raster ops and
	 * overlapping images. Images are held in native leJOS/Lego format.
	 * @param src byte array containing the source image
	 * @param sw Width of the source image
	 * @param sh Height of the source image
	 * @param sx X position to start the copy from
	 * @param sy Y Position to start the copy from
	 * @param dst byte array containing the destination image
	 * @param dw Width of the destination image
	 * @param dh Height of the destination image
	 * @param dx X destination
	 * @param dy Y destination
	 * @param w width of the area to copy
	 * @param h height of the area to copy
	 * @param rop raster operation.
	 */
	public native static void bitBlt(byte [] src, int sw, int sh, int sx, int sy, byte dst[], int dw, int dh, int dx, int dy, int w, int h, int rop);
	
	/**
	 * Scrolls the screen up one text line
	 *
	 */
	public static void scroll() {
		LCD.bitBlt(displayBuf, SCREEN_WIDTH, SCREEN_HEIGHT, 0, CELL_HEIGHT,
				   0, 0, SCREEN_WIDTH, SCREEN_HEIGHT - CELL_HEIGHT, ROP_COPY);
		LCD.bitBlt(0, SCREEN_HEIGHT - CELL_HEIGHT,
				   SCREEN_WIDTH, CELL_HEIGHT, ROP_CLEAR);
	}
}
