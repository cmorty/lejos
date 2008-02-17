package lejos.nxt.comm;

import java.io.OutputStream;

public class USBDebugOutputStream extends OutputStream{
	public static final int MAX_LENGTH = 63;
	private StringBuffer buf = new StringBuffer(MAX_LENGTH);
		
	public void write(int c) {
		buf.append((char) c);
		if (buf.length() == MAX_LENGTH || c == '\n') {
			Debug.out(buf.toString());
			buf.delete(0,buf.length());
		}	
	}
}
