package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class StringItem extends Item {
	private String text;
	
	public StringItem(String label, String text) {
		this.label = label;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void paint(Graphics g, int x, int y, int w, int h, boolean selected) {
		
	}
}
