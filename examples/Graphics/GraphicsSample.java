import javax.microedition.lcdui.Graphics;

public class GraphicsSample {
	
	public static void main(String [] options) throws Exception {
		Graphics g = new Graphics();
		g.drawLine(5,5,60,60);
		g.drawRect(62, 10, 25, 35);
		g.refresh();
	}
}
