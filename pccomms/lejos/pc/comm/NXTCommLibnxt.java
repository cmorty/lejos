package lejos.pc.comm;

import java.io.*;

public class NXTCommLibnxt implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native int jlibnxt_find();
	public native int jlibnxt_open(int nxt);
	public native void jlibnxt_close(int nxt);
	public native void jlibnxt_send_data(int nxt, byte [] message) throws IOException;
	public native byte[] jlibnxt_read_data(int nxt, int len) throws IOException;
	
	public NXTInfo[] search(String name, int protocol) {
		if ((protocol | NXTCommand.USB) == 0) {
			return new NXTInfo[0];
		}
		int nxt = jlibnxt_find();
		if (nxt != 0) {
			NXTInfo[] nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo();
			nxtInfo[0].protocol = NXTCommand.USB;
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
		if (nxtInfo != null && nxtInfo.nxtPtr != 0) jlibnxt_close(nxtInfo.nxtPtr);
	}
	
	public byte[] sendRequest(byte [] data, int replyLen) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
        if (replyLen == 0) return new byte [0];
		return jlibnxt_read_data(nxtInfo.nxtPtr, replyLen);
	}
	
	public byte [] read() throws IOException
	{
		return jlibnxt_read_data(nxtInfo.nxtPtr, 1);
	}
	
	public void write(byte [] data) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommUSBOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}
	
	static {
		System.loadLibrary("jlibnxt");
	}

}
