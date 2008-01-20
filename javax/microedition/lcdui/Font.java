package javax.microedition.lcdui;
import lejos.nxt.LCD;

/**
 * 
 * @author Andre Nijholt
 */
public class Font {
	public int getHeight()
	{
		return LCD.FONT_HEIGHT;
	}
	
	public int stringWidth(String str)
	{
		return str.length()*LCD.CELL_WIDTH;
	}

}
