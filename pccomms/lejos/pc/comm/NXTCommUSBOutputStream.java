package lejos.pc.comm;

import java.io.*;

public class NXTCommUSBOutputStream extends OutputStream {
	private NXTComm nxtComm;
	
	public NXTCommUSBOutputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
	}
	
	public void write(int b) throws IOException {
		byte[] bb = new byte[1];
		bb[0] = (byte) b;
		nxtComm.write( bb);
	}
}

