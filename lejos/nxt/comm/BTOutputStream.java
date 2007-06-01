package lejos.nxt.comm;

import java.io.*;

public class BTOutputStream extends OutputStream {

	public void write(int b) {
		byte[] bb = new byte[1];
		bb[0] = (byte) b;
		Bluetooth.btSend(bb, 1);
	}
}
