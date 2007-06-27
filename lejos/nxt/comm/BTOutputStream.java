package lejos.nxt.comm;

import java.io.*;

public class BTOutputStream extends OutputStream {
	byte[] bb = new byte[1];
	
	public void write(int b) {
		bb[0] = (byte) b;
		Bluetooth.btSend(bb, 1);
	}
}
