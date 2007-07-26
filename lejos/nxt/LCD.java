package lejos.nxt;

/**
 * 
 * @author Andre Nijholt
 */
public class LCD {
	public static final int SCREEN_SCALE 	= 4;
	public static final int SCREEN_WIDTH 	= 100;
	public static final int SCREEN_HEIGHT 	= 64;
	
	public static final int DISPLAY_WIDTH 	= 100;
	public static final int DISPLAY_DEPTH 	= 8;	// 8 * 8 bits = 64 pixels
	
	public static final int NOOF_CHARS 		= 128;
	public static final int FONT_WIDTH 		= 5;
	public static final int CELL_WIDTH 		= FONT_WIDTH + 1;
	
	public static final int DISPLAY_CHAR_WIDTH = DISPLAY_WIDTH / CELL_WIDTH;
	public static final int DISPLAY_CHAR_DEPTH = DISPLAY_DEPTH;

	private static final byte font[][] = {
		/* 0x00 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x01 */ {0x3E, 0x55, 0x61, 0x55, 0x3E},
		/* 0x02 */ {0x3E, 0x6B, 0x5F, 0x6B, 0x3E},
		/* 0x03 */ {0x0C, 0x1E, 0x3C, 0x1E, 0x0C},
		/* 0x04 */ {0x08, 0x1C, 0x3E, 0x1C, 0x08},
		/* 0x05 */ {0x18, 0x5E, 0x7E, 0x5E, 0x18},
		/* 0x06 */ {0x18, 0x5C, 0x7E, 0x5C, 0x18},
		/* 0x07 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x08 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x09 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0A */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0B */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0C */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0D */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0E */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x0F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x10 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x11 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x12 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x13 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x14 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x15 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x16 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x17 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x18 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x19 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1A */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1B */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1C */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1D */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1E */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x1F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
		/* 0x20 */ {0x00, 0x00, 0x00, 0x00, 0x00},
		/* 0x21 */ {0x00, 0x00, 0x5F, 0x00, 0x00},
		/* 0x22 */ {0x00, 0x07, 0x00, 0x07, 0x00},
		/* 0x23 */ {0x14, 0x3E, 0x14, 0x3E, 0x14},
		/* 0x24 */ {0x04, 0x2A, 0x7F, 0x2A, 0x10},
		/* 0x25 */ {0x26, 0x16, 0x08, 0x34, 0x32},
		/* 0x26 */ {0x36, 0x49, 0x59, 0x26, 0x50},
		/* 0x27 */ {0x00, 0x00, 0x07, 0x00, 0x00},
		/* 0x28 */ {0x00, 0x1C, 0x22, 0x41, 0x00},
		/* 0x29 */ {0x00, 0x41, 0x22, 0x1C, 0x00},
		/* 0x2A */ {0x2A, 0x1C, 0x7F, 0x1C, 0x2A},
		/* 0x2B */ {0x08, 0x08, 0x3E, 0x08, 0x08},
		/* 0x2C */ {0x00, 0x50, 0x30, 0x00, 0x00},
		/* 0x2D */ {0x08, 0x08, 0x08, 0x08, 0x08},
		/* 0x2E */ {0x00, 0x60, 0x60, 0x00, 0x00},
		/* 0x2F */ {0x20, 0x10, 0x08, 0x04, 0x02},
		/* 0x30 */ {0x3E, 0x51, 0x49, 0x45, 0x3E},
		/* 0x31 */ {0x00, 0x42, 0x7F, 0x40, 0x00},
		/* 0x32 */ {0x42, 0x61, 0x51, 0x49, 0x46},
		/* 0x33 */ {0x21, 0x41, 0x45, 0x4B, 0x31},
		/* 0x34 */ {0x18, 0x14, 0x12, 0x7F, 0x10},
		/* 0x35 */ {0x27, 0x45, 0x45, 0x45, 0x39},
		/* 0x36 */ {0x3C, 0x4A, 0x49, 0x49, 0x30},
		/* 0x37 */ {0x01, 0x01, 0x79, 0x05, 0x03},
		/* 0x38 */ {0x36, 0x49, 0x49, 0x49, 0x36},
		/* 0x39 */ {0x06, 0x49, 0x49, 0x29, 0x1E},
		/* 0x3A */ {0x00, 0x36, 0x36, 0x00, 0x00},
		/* 0x3B */ {0x00, 0x56, 0x36, 0x00, 0x00},
		/* 0x3C */ {0x08, 0x14, 0x22, 0x41, 0x00},
		/* 0x3D */ {0x14, 0x14, 0x14, 0x14, 0x14},
		/* 0x3E */ {0x41, 0x22, 0x14, 0x08, 0x00},
		/* 0x3F */ {0x02, 0x01, 0x59, 0x05, 0x02},
		/* 0x40 */ {0x1C, 0x2A, 0x36, 0x3E, 0x0C},
		/* 0x41 */ {0x7E, 0x09, 0x09, 0x09, 0x7E},
		/* 0x42 */ {0x7F, 0x49, 0x49, 0x49, 0x3E},
		/* 0x43 */ {0x3E, 0x41, 0x41, 0x41, 0x22},
		/* 0x44 */ {0x7F, 0x41, 0x41, 0x22, 0x1C},
		/* 0x45 */ {0x7F, 0x49, 0x49, 0x49, 0x41},
		/* 0x46 */ {0x7F, 0x09, 0x09, 0x09, 0x01},
		/* 0x47 */ {0x3E, 0x41, 0x41, 0x49, 0x3A},
		/* 0x48 */ {0x7F, 0x08, 0x08, 0x08, 0x7F},
		/* 0x49 */ {0x00, 0x41, 0x7F, 0x41, 0x00},
		/* 0x4A */ {0x20, 0x40, 0x41, 0x3F, 0x01},
		/* 0x4B */ {0x7F, 0x08, 0x14, 0x22, 0x41},
		/* 0x4C */ {0x7F, 0x40, 0x40, 0x40, 0x40},
		/* 0x4D */ {0x7F, 0x02, 0x04, 0x02, 0x7F},
		/* 0x4E */ {0x7F, 0x04, 0x08, 0x10, 0x7F},
		/* 0x4F */ {0x3E, 0x41, 0x41, 0x41, 0x3E},
		/* 0x50 */ {0x7F, 0x09, 0x09, 0x09, 0x06},
		/* 0x51 */ {0x3E, 0x41, 0x51, 0x21, 0x5E},
		/* 0x52 */ {0x7F, 0x09, 0x19, 0x29, 0x46},
		/* 0x53 */ {0x26, 0x49, 0x49, 0x49, 0x32},
		/* 0x54 */ {0x01, 0x01, 0x7F, 0x01, 0x01},
		/* 0x55 */ {0x3F, 0x40, 0x40, 0x40, 0x3F},
		/* 0x56 */ {0x1F, 0x20, 0x40, 0x20, 0x1F},
		/* 0x57 */ {0x7F, 0x20, 0x18, 0x20, 0x7F},
		/* 0x58 */ {0x63, 0x14, 0x08, 0x14, 0x63},
		/* 0x59 */ {0x03, 0x04, 0x78, 0x04, 0x03},
		/* 0x5A */ {0x61, 0x51, 0x49, 0x45, 0x43},
		/* 0x5B */ {0x00, 0x7F, 0x41, 0x41, 0x00},
		/* 0x5C */ {0x02, 0x04, 0x08, 0x10, 0x20},
		/* 0x5D */ {0x00, 0x41, 0x41, 0x7F, 0x00},
		/* 0x5E */ {0x04, 0x02, 0x01, 0x02, 0x04},
		/* 0x5F */ {0x40, 0x40, 0x40, 0x40, 0x40},
		/* 0x60 */ {0x00, 0x00, 0x07, 0x00, 0x00},
		/* 0x61 */ {0x20, 0x54, 0x54, 0x54, 0x78},
		/* 0x62 */ {0x7f, 0x48, 0x44, 0x44, 0x38},
		/* 0x63 */ {0x30, 0x48, 0x48, 0x48, 0x20},
		/* 0x64 */ {0x38, 0x44, 0x44, 0x48, 0x7f},
		/* 0x65 */ {0x38, 0x54, 0x54, 0x54, 0x18},
		/* 0x66 */ {0x08, 0x7e, 0x09, 0x09, 0x02},
		/* 0x67 */ {0x0c, 0x52, 0x52, 0x52, 0x3e},
		/* 0x68 */ {0x7f, 0x08, 0x04, 0x04, 0x78},
		/* 0x69 */ {0x00, 0x44, 0x7d, 0x40, 0x00},
		/* 0x6A */ {0x20, 0x40, 0x40, 0x3d, 0x00},
		/* 0x6B */ {0x7f, 0x10, 0x28, 0x44, 0x00},
		/* 0x6C */ {0x00, 0x41, 0x7f, 0x40, 0x00},
		/* 0x6D */ {0x7c, 0x04, 0x18, 0x04, 0x78},
		/* 0x6E */ {0x7c, 0x08, 0x04, 0x04, 0x78},
		/* 0x6F */ {0x38, 0x44, 0x44, 0x44, 0x38},
		/* 0x70 */ {(byte) 0xfc, 0x14, 0x14, 0x14, 0x08},
		/* 0x71 */ {0x08, 0x14, 0x14, 0x18, 0x7c},
		/* 0x72 */ {0x7c, 0x08, 0x04, 0x04, 0x08},
		/* 0x73 */ {0x48, 0x54, 0x54, 0x54, 0x20},
		/* 0x74 */ {0x04, 0x3f, 0x44, 0x40, 0x20},
		/* 0x75 */ {0x3c, 0x40, 0x40, 0x20, 0x7c},
		/* 0x76 */ {0x1c, 0x20, 0x40, 0x20, 0x1c},
		/* 0x77 */ {0x3c, 0x40, 0x38, 0x40, 0x3c},
		/* 0x78 */ {0x44, 0x28, 0x10, 0x28, 0x44},
		/* 0x79 */ {0x0c, 0x50, 0x50, 0x50, 0x3c},
		/* 0x7A */ {0x44, 0x64, 0x54, 0x4c, 0x44},
		/* 0x7B */ {0x00, 0x08, 0x36, 0x41, 0x00},
		/* 0x7C */ {0x00, 0x00, 0x7F, 0x00, 0x00},
		/* 0x7D */ {0x00, 0x41, 0x36, 0x08, 0x00},
		/* 0x7E */ {0x00, 0x07, 0x00, 0x07, 0x00},
		/* 0x7F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
	};

	private static int [] nativeBuff = new int[200];
	public static LCD lcd;

	public LCD() {
		// Store static reference for calling from Graphics
		lcd = this;
	}

	public static void setPixel(int rgbColor, int x, int y) {
		if (x < 0 || x >= SCREEN_WIDTH || y < 0 || y >= SCREEN_HEIGHT) return; // Test-Modify for speed
		int xChar = x / 4;
		int yChar = y / 8;
		int index = yChar * 25 + xChar;
		int specificBit = (y % 8) + ((x % 4) * 8);
		nativeBuff[index] = nativeBuff[index] | (rgbColor << specificBit);
	}

	public static void drawString(String str, int x, int y) {
		drawString(str, x, y);
	}
	
	public static void drawString(String str, int x, int y, boolean invert) {
		char [] strData = str.toCharArray();
		for (int i = 0; (i < strData.length) && (x < DISPLAY_CHAR_WIDTH) 
				&& (y < DISPLAY_CHAR_DEPTH); i++) {
			if (strData[i] == '\n') {
				// Continue on next line
				y++;
				continue;
			}

			drawChar(strData[i], (x + i) * CELL_WIDTH, y, invert);
		}
	}
	  	  	  
	public static void drawChar(char c, int x, int y, boolean invert) {
		for (int i = 0; i <= FONT_WIDTH; i++) {
			int xChar = (x + i) / 4;
			int index = y * 25 + xChar;
			
			if (i < FONT_WIDTH) {
				nativeBuff[index] = nativeBuff[index] | ((invert ? (font[c][i] ^0xFF) : font[c][i]) << (((x + i) % 4) * 8));
			} else if (invert) {
				nativeBuff[index] = nativeBuff[index] | (0xFF << (((x + i) % 4) * 8));
			}
		}
	}

	public static void clearDisplay() {
		for (int i = 0; i < nativeBuff.length; i++) {
			nativeBuff[i] = 0;
		}
		clear();
	}
	
	public static void setDisplay() {
		setDisplay(nativeBuff);
	}

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
	   * Display an int on the LCD at specified x,y co-ordinate.
	   */
	  public static native void drawInt(int i, int x, int y);

	  /**
	   * Display an in on the LCD at x,y with leading spaces to occupy at least the number
	   * of characters specified by the places parameter.
	   */
	  public static native void drawInt(int i, int places, int x, int y);

	
	
	/* Draw stuff only on PC
	public static void refresh() {
		lcd.repaint();
	}
	
    public synchronized void paint(java.awt.Graphics g) {
        int w = getSize().width;
        int h = getSize().height;

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, w, h); 

        // Draw NXJ screen buffer contents
        g2.setColor(Color.BLACK);
        for (int xp = 0; xp < DISPLAY_WIDTH; xp++) {
        	for (int yp = 0; yp < DISPLAY_DEPTH; yp++) {
        		for (int pixel = 0; pixel < 8; pixel++) {
        			if ((displayBuffer[yp][xp] & (0x80 >> pixel)) != 0) {        				
        				g2.fillRect(SCREEN_SCALE * xp, SCREEN_SCALE * (yp * 8 + 7 - pixel), 
        						SCREEN_SCALE, SCREEN_SCALE);
        			}
        		}
        	}
        }
    } */

}
