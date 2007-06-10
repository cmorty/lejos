package lejos.pc.comm;

import java.io.*;

public class LibnxtInputStream extends InputStream {
	private int nxt;
	private NXTCommLibnxt libnxt;

	public LibnxtInputStream(NXTCommLibnxt libnxt,int nxt) {
		this.nxt = nxt;
		this.libnxt = libnxt;
	}
	
	public int read() {
        byte[] buf = libnxt.jlibnxt_read_data(nxt, 1);		
	    return buf[0] & 0xFF;
	}
}

