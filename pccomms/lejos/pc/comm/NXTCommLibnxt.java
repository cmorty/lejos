package lejos.pc.comm;

import java.io.*;

/**
 * Implementation of NXTComm over USB using libnxt.
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommLibnxt implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native long jlibnxt_find();
	public native int jlibnxt_open(long nxt);
	public native void jlibnxt_close(long nxt);
	public native void jlibnxt_send_data(long nxt, byte [] message) throws IOException;
	public native byte[] jlibnxt_read_data(long nxt, int len) throws IOException;
	
	public NXTInfo[] search(String name, int protocol) {
		if ((protocol | NXTCommFactory.USB) == 0) {
			return new NXTInfo[0];
		}
		long nxt = jlibnxt_find();
		if (nxt != 0) {
			NXTInfo[] nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo();
			nxtInfo[0].protocol = NXTCommFactory.USB;
			nxtInfo[0].name = "Unknown";
			nxtInfo[0].nxtPtr = nxt;
			return nxtInfo;
		}
		return new NXTInfo[0];
	}

	public boolean open(NXTInfo nxtInfo) {
		this.nxtInfo = nxtInfo;
		int open = jlibnxt_open(nxtInfo.nxtPtr);
		return (open == 0);
	}
	
	public void close() throws IOException {
		if (nxtInfo != null && nxtInfo.nxtPtr != 0) 
        {
            jlibnxt_close(nxtInfo.nxtPtr);
            nxtInfo.nxtPtr = 0;
        }
	}
	
	public byte[] sendRequest(byte [] data, int replyLen) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
        if (replyLen == 0) return new byte [0];
		return jlibnxt_read_data(nxtInfo.nxtPtr, replyLen);
	}
	
	public byte [] read() throws IOException
	{
		byte [] ret = jlibnxt_read_data(nxtInfo.nxtPtr, 64);
        if (ret != null && ret.length == 0) return null;
        return ret;
	}
	
	public int available() throws IOException {
		return 0;
	}
	
	public void write(byte [] data) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}
	
	static {
		System.loadLibrary("jlibnxt");
	}

}
