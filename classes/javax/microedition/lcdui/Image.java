package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class Image {
	private int width;
	private int height;
	private byte[] data;
	
	public Image(int width, int height, byte[] data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public byte[] getData() {
		return data;
	}
}
