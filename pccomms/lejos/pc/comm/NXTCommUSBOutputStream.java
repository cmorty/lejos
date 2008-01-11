package lejos.pc.comm;

import java.io.*;

/**
 * Implementation of OutputStream over NXTComm using USB.
 */
public class NXTCommUSBOutputStream extends OutputStream {
	private NXTComm nxtComm;
	
	public NXTCommUSBOutputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
	}
	
	public void write(int b) throws IOException {
		byte[] bb = new byte[1]; // USB uses 1-byte packets
		bb[0] = (byte) b;
		nxtComm.write( bb);
	}
}

