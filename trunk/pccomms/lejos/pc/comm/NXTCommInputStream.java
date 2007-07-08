package lejos.pc.comm;

import java.io.*;

public class NXTCommInputStream extends InputStream {
	private NXTComm nxtComm;

	public NXTCommInputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
	}
	
	public int read() throws IOException {
        byte[] buf = nxtComm.read();		
	    return buf[0] & 0xFF;
	}
}
