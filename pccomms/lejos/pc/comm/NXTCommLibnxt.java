package lejos.pc.comm;

import java.io.*;

public class NXTCommLibnxt implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native int jlibnxt_find();
	public native void jlibnxt_open(int nxt);
	public native void jlibnxt_close(int nxt);
	public native void jlibnxt_send_data(int nxt, byte [] message);
	public native byte[] jlibnxt_read_data(int nxt, int len);
	
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

	public void open(NXTInfo nxtInfo) {
		this.nxtInfo = nxtInfo;
		jlibnxt_open(nxtInfo.nxtPtr);
	}
	
	public void close() {
		if (nxtInfo != null && nxtInfo.nxtPtr != 0) jlibnxt_close(nxtInfo.nxtPtr);
	}
	
	public byte[] sendRequest(byte [] data, int replyLen) {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
        if (replyLen == 0) return new byte [0];
		return jlibnxt_read_data(nxtInfo.nxtPtr, replyLen);
	}
	
	public OutputStream getOutputStream() {
		return new LibnxtOutputStream(this, nxtInfo.nxtPtr);		
	}
	
	public InputStream getInputStream() {
		return new LibnxtInputStream(this, nxtInfo.nxtPtr);		
	}
	
	static {
		System.loadLibrary("jlibnxt");
	}

}
