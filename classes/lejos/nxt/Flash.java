package lejos.nxt;

public class Flash {

	private Flash()
	{
	}
	
	public static native void readPage(byte[] buf, int pageNum);

	public static native void writePage(byte[] buf, int pageNum);
}
