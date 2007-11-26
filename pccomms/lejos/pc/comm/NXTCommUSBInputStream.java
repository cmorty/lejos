package lejos.pc.comm;

import java.io.*;

public class NXTCommUSBInputStream extends InputStream {
	private NXTComm nxtComm;

	public NXTCommUSBInputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
	}
	
	public int read() throws IOException {
        byte[] buf = nxtComm.read();
	    return buf[0] & 0xFF; // USB uses 1-byte packets
	}
}
