package lejos.nxt.comm;

import java.io.*;

public class BTOutputStream extends OutputStream {
	byte[] bb = new byte[3];
	
	public void write(int b) {
		bb[0] = 1;
		bb[1] = 0;
		bb[2] = (byte) b;
		Bluetooth.btSend(bb,3);
	}
}
