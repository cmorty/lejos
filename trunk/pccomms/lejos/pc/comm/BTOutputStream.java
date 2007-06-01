package lejos.pc.comm;

import java.io.*;

public class BTOutputStream extends OutputStream {
	ByteArrayOutputStream baos;
	OutputStream os;
	
	public BTOutputStream(OutputStream os) {
		this.os = os;
		baos = new ByteArrayOutputStream();
	}
	
	public void write(int b) throws IOException {
		baos.write(b);
	}
	
	public void flush() throws IOException {
		byte[] b = baos.toByteArray();
		
		byte lsb = (byte) b.length;
		
		os.write(lsb);
		os.write(0);
		os.write(b);
		os.flush();
	}
}
