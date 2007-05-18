package lejos.pc.comm;

public class NXTCommFantom implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native int jfantom_find();
	public native int jfantom_open(int nxt);
	public native void jfantom_close(int nxt);
	public native void jfantom_send_data(int nxt, byte [] message, int len, int replyLen);
	public native byte[] jfantom_read_data(int nxt, int len);
	
	public NXTInfo[] search(String name, int protocol) {
		if ((protocol | NXTCommand.USB) == 0) {
			return new NXTInfo[0];
		}
		int nxt = jfantom_find();
		if (nxt != 0) {
			NXTInfo[] nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo();
			nxtInfo[0].protocol = NXTCommand.USB;
			nxtInfo[0].name = "Unknown";
			nxtInfo[0].fantomIterator = nxt;
			return nxtInfo;
		}
		return new NXTInfo[0];
	}

	public void open(NXTInfo nxtInfo) {
		this.nxtInfo = nxtInfo;
		nxtInfo.fantomNXT = jfantom_open(nxtInfo.fantomIterator);
	}
	
	public void close() {
		jfantom_close(nxtInfo.fantomNXT);
	}
	
	public byte [] sendRequest(byte [] data, int replyLen) {
		System.out.println("Sending data");
		jfantom_send_data(nxtInfo.fantomNXT, data, data.length, replyLen);
		return jfantom_read_data(nxtInfo.fantomNXT, replyLen);
	}
	
	static {
		System.loadLibrary("jfantom");
	}

}

