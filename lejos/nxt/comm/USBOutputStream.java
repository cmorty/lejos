package lejos.nxt.comm;

import java.io.*;

/**
 * Implements an OutputStream over USB.
 * 
 * @author Lawrie Griffiths
 *
 */
public class USBOutputStream extends OutputStream {

	public void write(int b) {
		byte[] bb = new byte[1];
		bb[0] = (byte) b;
		USB.usbWrite(bb, 1);
	}
}

