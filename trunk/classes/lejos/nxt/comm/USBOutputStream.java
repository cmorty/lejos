package lejos.nxt.comm;

import java.io.*;

public class USBOutputStream extends OutputStream {

	public void write(int b) {
		byte[] bb = new byte[1];
		bb[0] = (byte) b;
		USB.usbWrite(bb, 1);
	}
}
