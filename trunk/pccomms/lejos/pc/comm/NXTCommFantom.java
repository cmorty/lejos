package lejos.pc.comm;

public class NXTCommFantom implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native String[] jfantom_find();
	public native int jfantom_open(String nxt);
	public native void jfantom_close(int nxt);
	public native void jfantom_send_data(int nxt, byte [] message, int len, int replyLen);
	public native byte[] jfantom_read_data(int nxt, int len);
	
	public NXTInfo[] search(String name, int protocol) {
		String[] nxtNames = jfantom_find();
		NXTInfo[] nxtInfo = new NXTInfo[nxtNames.length];
		for(int i=0;i<nxtNames.length;i++) {
			nxtInfo[i] = new NXTInfo();
			nxtInfo[0].name = nxtNames[i];
			return nxtInfo;
		}
		return new NXTInfo[0];
	}

	public void open(NXTInfo nxtInfo) {
		this.nxtInfo = nxtInfo;
		nxtInfo.fantomNXT = jfantom_open(nxtInfo.name);
	}
	
	public void close() {
		jfantom_close(nxtInfo.fantomNXT);
	}
	
	public byte [] sendRequest(byte [] data, int replyLen) {
		jfantom_send_data(nxtInfo.fantomNXT, data, data.length, replyLen-1);
		return jfantom_read_data(nxtInfo.fantomNXT, replyLen);
	}
	
	static {
		System.loadLibrary("jfantom");
	}

}

