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

    public static Image createImage(int width, int height)
    {
        byte[] imageData = new byte[width*(height+7)/8];
        return new Image(width, height, imageData);
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

    public Graphics getGraphics()
    {
        return new Graphics(data, width, height);
    }
}
