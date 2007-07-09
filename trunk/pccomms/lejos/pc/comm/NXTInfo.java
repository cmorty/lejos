package lejos.pc.comm;

public class NXTInfo {
	public String name;
	public String btDeviceAddress;
	public String btResourceString;
	public int nxtPtr;
	public int protocol = 0; // 0=URL, 1 = Bluetooth
	
	public NXTInfo() {
		
	}
	
	public NXTInfo(String name, String address) {
		this.name = name;
		this.btDeviceAddress = address;
		this.btResourceString = name + "::" + address;
		this.protocol = NXTCommFactory.BLUETOOTH;
	}
}
