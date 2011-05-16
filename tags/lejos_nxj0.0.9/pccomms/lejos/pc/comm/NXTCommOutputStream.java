package lejos.pc.comm;

import java.io.*;

/**
 * Implementation of OutputStream over NXTComm using Bluetooth.
 */
public class NXTCommOutputStream extends OutputStream {
	private ByteArrayOutputStream baos;
	private NXTComm nxtComm;
	
	public NXTCommOutputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
		baos = new ByteArrayOutputStream();
	}
	
	public void write(int b) throws IOException {
		baos.write(b);
	}
	
	public void flush() throws IOException {
        if (baos.size() > 0)
        {
            byte[] b = baos.toByteArray();
            nxtComm.write(b);
            baos.reset();
        }
	}
}
