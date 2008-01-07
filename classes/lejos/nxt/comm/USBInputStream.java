package lejos.nxt.comm;

import java.io.*;

/**
 * Implements an InputStream over USB.
 * 
 * @author Lawrie Griffiths
 *
 */
public class USBInputStream extends InputStream {
	private byte buf[] = new byte[64];
	private int bufIdx = -1, bufSize = -1;
	
	public int read() {
		if (bufIdx == bufSize) {
			do {
				bufSize = USB.usbRead(buf, 64);
			} while (bufSize == 0);
			bufIdx = 0;
		}
		
	    return buf[bufIdx++] & 0xFF;
	}
}

