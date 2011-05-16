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
	 * The device address.
	 */
	public String deviceAddress;
	
	/**
	 * A string used to locate the NXT. Dependent on
	 * the version of NXTComm used.
	 */
	String btResourceString;
	
	/**
	 * A pointer to the NXT. Dependent on the
	 * version of NXTComm used.
	 */
	long nxtPtr;
	
	/**
	 * The protocol used to connect to the NXT: USB or BLUETOOTH.
	 */
	public int protocol = 0;

	/**
	 * the present connection state of the NXT
	 */
	public NXTConnectionState connectionState = NXTConnectionState.UNKNOWN;
	
	public NXTInfo() {}
	
	/**
	 * Create a NXTInfo that is used to connect to 
	 * a NXT via Bluetooth using the Bluetooth address.
	 * 
	 * @param protocol the protocol to use (USB or BLUETOOTH)
	 * @param name the name of the NXT
	 * @param address the Bluetooth address with optional colons between hex pairs.
	 */	
	public NXTInfo(int protocol, String name, String address) {
		this.name = name;
		this.deviceAddress = address;
		this.protocol = protocol;
	}

	/**
	 * copy constructor
	 * @param info
	 */
	public NXTInfo(NXTInfo info) {
		super();
		this.btResourceString = info.btResourceString;
		this.connectionState = info.connectionState;
		this.deviceAddress = info.deviceAddress;
		this.name = info.name;
		this.nxtPtr = info.nxtPtr;
		this.protocol = info.protocol;
	}
	
}
