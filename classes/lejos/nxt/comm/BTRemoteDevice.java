package lejos.nxt.comm;

/**
 * Represents a remote Bluetooth device.
 * 
 * @author Lawrie Griffiths
 *
 */
public class BTRemoteDevice {

	private byte[] addr = new byte[7];
	private char[] friendlyNameCAr = new char[16];
	private int friendlyNameLen = 0;
	private byte[] deviceClass = new byte[4];
	private static final char[] cs = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	public BTRemoteDevice(char[] friendlyNameCharArray, int len, byte[] deviceAddr, byte [] devclass) {
		setFriendlyName(friendlyNameCharArray, len);
		setDeviceAddr(deviceAddr);
		setDeviceClass(devclass);
	}

	public void setDeviceAddr(byte[] deviceAddr) {
		for(int i=0;i<7;i++) addr[i] = deviceAddr[i];		
	}
	
	public byte[] getDeviceAddr() {
		return addr;
	}
	
	public void setFriendlyName(char[] friendlyNameCharArray, int len) {
		for(int i=0; i<len; i++) this.friendlyNameCAr[i] = friendlyNameCharArray[i];
		this.friendlyNameLen = len;

	}
	
	public String getFriendlyName() {
		return new String(this.friendlyNameCAr, 0 ,this.friendlyNameLen);
	}
	
	/*
	 * Get the FriendlyName of the BTRemoteDevice as Char-Array 
	 * @params: 
	 */
	public int getFriendlyName(char[] friendlyNameCharArray) {
		for(int i=0; i<friendlyNameLen; i++) friendlyNameCharArray[i] = this.friendlyNameCAr[i];
		return friendlyNameLen;
	}
	
	public void setDeviceClass(byte[] devclass) {
		for(int i=0;i<4;i++) deviceClass[i] = devclass[i];
	}
	
	public String getAddressString() {
		char[] caddr = new char[12];
		
		int ci = 0;
		int nr = 0;
		int addri = 0;
		
		for(int i=0; i<6; i++) {
			addri = (int)addr[i];
			nr = (addri>=0) ? addri : (256 + addri);	
			caddr[ci++] = cs[nr / 16];
			caddr[ci++] = cs[nr % 16];
		}
		return new String(caddr, 0, 12);
	}
	
	public byte[] getDeviceClass() {
		return deviceClass;
	}
}