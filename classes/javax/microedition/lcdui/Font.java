package javax.microedition.lcdui;
import lejos.nxt.LCD;

/**
 * 
 * @author Andre Nijholt
 */
public class Font {
    final int width;
    final int height;
    final int glyphWidth;
    final byte [] glyphs;
    private static Font systemFont = new Font(LCD.getSystemFont(), LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.FONT_WIDTH);

    /**
     * Create a new font for the specified NXT format glyph map
     * @param glyphs the actual bytes of the glyph.
     * @param width The cell width.
     * @param height The cell height.
     * @param glyphWidth The width of the glyph bits.
     */
    Font(byte [] glyphs, int width, int height, int glyphWidth)
    {
        this.glyphs = glyphs;
        this.width = width;
        this.height = height;
        this.glyphWidth = glyphWidth;
    }

    public static Font getDefaultFont()
    {
        return systemFont;
    }


	public int getHeight()
	{
		return height;
	}
	
	public int stringWidth(String str)
	{
		return str.length()*width;
	}

}
