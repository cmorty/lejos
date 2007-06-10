package lejos.pc.comm;

import java.io.*;

public class LibnxtOutputStream extends OutputStream {
	private int nxt;
	private NXTCommLibnxt libnxt;
	
	public LibnxtOutputStream(NXTCommLibnxt libnxt,int nxt) {
		this.nxt = nxt;
		this.libnxt = libnxt;
	}
	
	public void write(int b) {
		byte[] bb = new byte[1];
		bb[0] = (byte) b;
		libnxt.jlibnxt_send_data(nxt, bb);
	}
}
