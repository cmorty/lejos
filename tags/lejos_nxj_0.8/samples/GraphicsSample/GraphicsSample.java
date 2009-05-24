import javax.microedition.lcdui.Graphics;
import lejos.nxt.*;

/**
 * Simple demonstration of leJOS graphics.
 * 
 * see the LCDUI sample for more sophisticated graphics.
 * 
 * @author Brian Bagnall and Lawrie Griffiths
 *
 */
public class GraphicsSample {
	
	public static void main(String [] options) throws Exception {
		Graphics g = new Graphics();
		g.drawLine(5,5,60,60);
		g.drawRect(62, 10, 25, 35);
		g.refresh();
		Button.waitForPress();
	}
}
