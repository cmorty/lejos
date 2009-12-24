
package lejos.nxt;
import lejos.util.Delay;

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

    public static final int DEFAULT_REFRESH_PERIOD = 250;

    private static byte[] font = getSystemFont();
	private static byte [] displayBuf = getDisplay();
	private static boolean autoRefresh = true;
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
	private static void bitBlt(byte [] src, int sw, int sh, int sx, int sy, int dx, int dy, int w, int h, int rop)
	{
		bitBlt(src, sw, sh, sx, sy, displayBuf, SCREEN_WIDTH, SCREEN_HEIGHT, dx, dy, w, h, rop);	
	}

    /**
	 * Draw a single char on the LCD at specified x,y co-ordinate.
	 * @param c Character to display
	 * @param x X location
	 * @param y Y location
	 */
	public static void drawChar(char c, int x, int y) {
		bitBlt(font, FONT_WIDTH*128, FONT_HEIGHT, FONT_WIDTH*c, 0, x*CELL_WIDTH, y*CELL_HEIGHT, FONT_WIDTH, FONT_HEIGHT, ROP_COPY);
	}
	

	public static void clearDisplay() {
		clear();
	}
	
	public static void setDisplay() {
	}

	/**
	 * Display a string on the LCD at specified x,y co-ordinate.
     *
     * @param str The string to be displayed
     * @param x The x character co-ordinate to display at.
     * @param y The y character co-ordinate to display at.
     */
	public static native void drawString(String str, int x, int y);

	/**
	 * Display an int on the LCD at specified x,y co-ordinate.
     *
     * @param i The value to display.
     * @param x The x character co-ordinate to display at.
     * @param y The y character co-ordinate to display at.
     */
	public static native void drawInt(int i, int x, int y);

	/**
	 * Display an in on the LCD at x,y with leading spaces to occupy at least the number
	 * of characters specified by the places parameter.
     *
     * @param i The value to display
     * @param places number of places to use to display the value
     * @param x The x character co-ordinate to display at.
     * @param y The y character co-ordinate to display at.
     */
	public static native void drawInt(int i, int places, int x, int y);

	/**
	 * Start the process of updating the display. This will always return
     * immediately after starting the refresh process.
	 */
	public static native void asyncRefresh();

    /**
     * Obtain the system time when the current display refresh operation will
     * be complete. Not that this may be in the past.
     * @return the system time in ms when the refresh will be complete.
     */
    public static native int getRefreshCompleteTime();

    /**
     * Wait for the current refresh cycle to complete.
     */
    public static void asyncRefreshWait()
    {
        int waitTime = getRefreshCompleteTime() - (int)System.currentTimeMillis();
        if (waitTime > 0) Delay.msDelay(waitTime);
    }

    /**
     * Refresh the display. If auto refresh is off, this method will wait until
     * the display refresh has completed. If auto refresh is on it will return
     * immediately.
     */
    public static void refresh()
    {
        asyncRefresh();
        if (!autoRefresh) asyncRefreshWait();
    }


	/**
	 * Clear the display.
	 */
	public static native void clear();
	  
	/**
	 * Write graphics from a Java buffer to the display.
     *
     * @param buff The contents to write to the display.
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
     * Set the period used to perform automatic refreshing of the display.
     * A period of 0 disables the refresh.
     * @param period time in ms
     * @return the previous refresh period.
     */
    public static native int setAutoRefreshPeriod(int period);

	/**
	 * Turn on/off the automatic refresh of the LCD display. At system startup
	 * auto refresh is on.
	 * @param on true to enable, false to disable
	 */
	public static void setAutoRefresh(boolean on)
    {
        setAutoRefreshPeriod((on ? DEFAULT_REFRESH_PERIOD : 0));
        autoRefresh = on;
    }
	
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
		LCD.bitBlt(null, 0, 0, 0, 0, 0, SCREEN_HEIGHT - CELL_HEIGHT,
				   SCREEN_WIDTH, CELL_HEIGHT, ROP_CLEAR);
	}

    /**
     * Set the LCD contrast.
     * @param contrast 0 blank 0x60 full on
     */
    public native static void setContrast(int contrast);
}
