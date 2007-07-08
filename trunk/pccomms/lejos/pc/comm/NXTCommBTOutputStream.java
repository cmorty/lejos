package lejos.pc.comm;

import java.io.*;

public class NXTCommBTOutputStream extends OutputStream {
	ByteArrayOutputStream baos;
	NXTComm nxtComm;
	
	public NXTCommBTOutputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
		baos = new ByteArrayOutputStream();
	}
	
	public void write(int b) throws IOException {
		baos.write(b);
	}
	
	public void flush() throws IOException {
		byte[] b = baos.toByteArray();
		byte [] data = new byte[b.length+2];
		data[0] = (byte) b.length;
		data[1] = 0;
		for(int i=0;i<b.length;i++) data[i+2] = b[i];
		
		baos.reset();
		
		nxtComm.write(data);
	}
}
