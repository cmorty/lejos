package lejos.pc.comm;

/**
 * Structure containing information about a specific NXT
 * and the potential connections to it. 
 * 
 * Returned from the NXTComm search method.
 *
 */
public class NXTInfo {
	/**
	 * Friendly name of the NXT.
	 */
	public String name;
	
	/**
	 * The Bluetooth address.
	 */
	public String btDeviceAddress;
	
	/**
	 * A string used to locate the NXT. Dependent on
	 * the version of NXTComm used.
	 */
	public String btResourceString;
	
	/**
	 * A pointer to the NXT. Dependent on the
	 * version of NXTComm used.
	 */
	public long nxtPtr;
	
	/**
	 * The protocol used to conect to the NXT: USB or BLUETOOTH.
	 */
	public int protocol = 0;

	public NXTInfo() {}
	

	/**
	 * Create a NXTInfo that is used to connect to 
	 * a NXT via Bluetooth using the Bluetooth address.
	 * 
	 * @param name the name of the NXT
	 * @param address the Bluetooth address with optional colons between hex pairs.
	 */
	public NXTInfo(String name, String address) {
		this.name = name;
		this.btDeviceAddress = address;
		this.protocol = NXTCommFactory.BLUETOOTH;
	}
}
