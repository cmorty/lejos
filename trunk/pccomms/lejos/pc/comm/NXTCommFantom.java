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
			String nxtName = nxtNames[i];
			nxtInfo[i].btResourceString = nxtName;
			nxtInfo[i].name = "Unknown";
			nxtInfo[i].protocol = NXTCommand.USB;
			nxtInfo[i].btDeviceAddress = "";
			if (nxtName != null) {
			    if (nxtName.length() >= 3 && nxtName.substring(0,3).equals("BTH"))
			    	nxtInfo[i].protocol = NXTCommand.BLUETOOTH;	
			    int startName = nxtName.indexOf("::");
			    if (startName >= 0) startName +=2;
			    int endName = -1;
			    if (startName != -1) endName = nxtName.indexOf("::", startName);
			    if (startName >= 0 && endName >= 0) {
			    	nxtInfo[i].name = nxtName.substring(startName, endName);
		            nxtInfo[i].btDeviceAddress = nxtName.substring(endName+2);
			    }
			}
		}
		return nxtInfo;
	}

	public void open(NXTInfo nxtInfo) {
		this.nxtInfo = nxtInfo;
		nxtInfo.nxtPtr = jfantom_open(nxtInfo.btResourceString);
	}
	
	public void close() {
		jfantom_close(nxtInfo.nxtPtr);
	}
	
	public byte [] sendRequest(byte [] data, int replyLen) {
		jfantom_send_data(nxtInfo.nxtPtr, data, data.length, replyLen-1);
		return jfantom_read_data(nxtInfo.nxtPtr, replyLen);
	}
	
	static {
		System.loadLibrary("jfantom");
	}

}

