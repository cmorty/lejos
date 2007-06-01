package lejos.nxt.comm;

import java.io.*;
import lejos.nxt.*;

public class BTInputStream extends InputStream {
	private byte buf[] = new byte[256];
	private int bufIdx = -1, bufSize = -1;
	
	public int read() {
		if (bufIdx == bufSize) {
			do {
				bufSize = Bluetooth.readPacket(buf, 256);
			} while (bufSize == 0);
			bufIdx = 0;
		}
		
	    return buf[bufIdx++] & 0xFF;
	}
}
