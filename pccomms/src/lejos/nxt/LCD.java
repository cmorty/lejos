package lejos.nxt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * PC emulation of the LCD class
 * 
 * @author Lawrie Griffiths
 *
 */
public class LCD extends JPanel
{
	private static final long serialVersionUID = 1L;
	public static final int LCD_WIDTH = 100;
    public static final int LCD_HEIGHT = 64;
    public static final int FONT_WIDTH = 5;
    public static final int FONT_HEIGHT = 8;
    public static final int CELL_WIDTH = FONT_WIDTH + 1;
    public static final int CELL_HEIGHT = FONT_HEIGHT;
    
    private static LCD singleton = new LCD();
    private static Color white = new Color(155, 205, 155, 255);
    private static Color black = new Color(0,0,0,255);
    // Start NXT frame
    static NXTFrame frame = NXTFrame.getSingleton();
 
    private static boolean auto = true;
    
    private BufferedImage lcd = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private Graphics2D lcdGC = lcd.createGraphics();
    private Font font = new Font("monospaced", Font.BOLD, 8);
    
    private LCD() {
    	// Do not instantiate
    }
    
    public static LCD getSingleton() {
    	return singleton;
    }
    
    public static void clearDisplay() {
    	LCD.clear();
    }
    
    public static void drawChar(char c, int x, int y) {
    	char[] chars = {c};
        singleton.lcdGC.setFont(singleton.font);
        singleton.lcdGC.setColor(white);
        singleton.lcdGC.fillRect(x*CELL_WIDTH, y*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
        singleton.lcdGC.setColor(black);
        singleton.lcdGC.drawChars(chars,0, 1, x*CELL_WIDTH, (y+1)*CELL_HEIGHT -1);
        if (auto) singleton.repaint();   	
    }
    
    public static void drawInvertedChar(char c, int x, int y) {
    	char[] chars = {c};
        singleton.lcdGC.setFont(singleton.font);
        singleton.lcdGC.setColor(black);
        singleton.lcdGC.fillRect(x*CELL_WIDTH, y*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
        singleton.lcdGC.setColor(white);
        singleton.lcdGC.drawChars(chars,0, 1, x*CELL_WIDTH, (y+1)*CELL_HEIGHT -1);
        if (auto) singleton.repaint();   	
    }
    
    public static void drawString(String str, int x, int y) {
    	//System.out.println("drawString: " + str);
        // Draw each character separately to get the 1-pixel gap on the right
        for (int i=0;i<str.length();i++) LCD.drawChar(str.charAt(i), x+i, y);  	
    }
    
    public static void drawInt(int i, int x, int y) {
    	String s = "" + i;
    	drawString(s,x,y);
    }
    
    public static void drawString(String str, int x, int y, boolean inverted) {
        // Draw each character separately to get the 1-pixel gap on the right
        for (int i=0;i<str.length();i++) {
        	if (inverted) LCD.drawInvertedChar(str.charAt(i), x+i, y);
        	else LCD.drawChar(str.charAt(i), x+i, y);
        }
    }
    
    public static void drawInt(int i, int places, int x, int y) {
    	String s = "" + i;
    	while(s.length() < places) s = " " + s;
    	drawString(s,x,y);
    }
    
    public static void refresh() {
    	singleton.repaint();
    }
    
    public static void asyncRefresh() {
    	singleton.repaint();
    }
    
    public static void asyncRefreshWait() {
    	// Does nothing
    }
    
    public static void setAutoRefresh(boolean on) {
    	auto = on;
    }
    
    public static void setPixel(int x, int y, int color) {
    	Color pixelColor = (color == 0 ? white : black);
    	singleton.lcdGC.setColor(pixelColor);	
    	singleton.lcdGC.fillRect(x, y, 1, 1);
    	if (auto) singleton.repaint();	
    }

    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        super.paint(g);
        int width = getWidth();
        int height = getHeight();
        int imgWidth = lcd.getWidth();
        int imgHeight = lcd.getHeight();
        // Draw a scaled version of the display, keep the aspect ratio and
        // centre it.
        if (width < (height*imgWidth)/imgHeight)
        {
            imgHeight = (width*imgHeight)/imgWidth;
            imgWidth = width;
        }
        else
        {
            imgWidth = (height*imgWidth)/imgHeight;
            imgHeight = height;
        }
        g2d.drawImage(lcd, (width-imgWidth)/2, (height-imgHeight)/2, imgWidth, imgHeight, null);

    }
    
    public static void clear()
    {
    	//System.out.println("clear");
        singleton.lcdGC.setColor(white);
        singleton.lcdGC.fillRect(0, 0, LCD_WIDTH, LCD_HEIGHT);
        if (auto) singleton.repaint();
    }
    
    public static void clear(int row) {
    	//System.out.println("clear");
        singleton.lcdGC.setColor(white);
        singleton.lcdGC.fillRect(row * CELL_WIDTH, 0, LCD_WIDTH, CELL_HEIGHT);
        if (auto) singleton.repaint();   	
    }
    
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
    public static void bitBlt(byte[] src, int sw, int sh, int sx, int sy, int dx, int dy, int w, int h, int rop) {
    	singleton.update(src); // Assumes copy of whole screen. TODO: full bitBlt
    	if (auto) singleton.repaint();
	}
    
    public void update(byte [] buffer)
    {
        int offset = 0;
        int row = 0;
        lcdGC.setColor(white);
        lcdGC.fillRect(0, 0, LCD_WIDTH, LCD_HEIGHT);
        lcdGC.setColor(new Color(0, 0, 0, 255));
        for(row = 0; row < 64; row += 8)
            for(int x = 0; x < LCD_WIDTH; x++)
            {
                byte vals = buffer[offset++];
                for(int y = 0; y < 8; y++)
                {
                    if ((vals & 1) != 0)
                        lcdGC.fillRect(x, y+row, 1, 1);
                    vals >>= 1;
                }
            }
    }
}

