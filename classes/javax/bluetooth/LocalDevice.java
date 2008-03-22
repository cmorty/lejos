package javax.bluetooth;

import lejos.nxt.comm.Bluetooth;

/**
 * Singleton class representing a local NXT Bluetooth device.
 * Most methods are standard, except you can also set the friendly
 * name with this class. 
 * @author BB
 *
 */
public class LocalDevice {
	
	private static LocalDevice localDevice;
		
	private LocalDevice() {}

	/*
	 * DEVELOPER NOTES: Technically the constructor doesn't throw
	 * a BluetoothStateException like the Bluetooth API demands.
	 * Get rid of exception?
	 */
	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		if(localDevice == null)
			localDevice = new LocalDevice();
		return localDevice;
	}
	
	public String getFriendlyName() {
		char [] nameChars = convertBytesToChars(Bluetooth.getFriendlyName());
		String fName = new String(nameChars);  
		return fName.substring(0, fName.indexOf(0)); // Clip off extra chars
	}
	
	/**
	 * Changes the friendly name of the NXT brick.
	 * NOTE: This method is not part of the standard JSR 82 API
	 * because not all Bluetooth devices can change their friendly name.
	 * @return true = success, false = failed
	 */
	public boolean setFriendlyName(String name) {
		byte[] nameBytes = convertCharsToBytes(name.toCharArray(), 16);
		return Bluetooth.setFriendlyName(nameBytes);
	}
	
	/*
	 * !! DEV NOTES: If we get javax.bluetooth and lejos.nxt.comm classes
	 * working with the same data types more consistently can probably
	 * get rid of some of these convert helper methods.
	 * e.g. Bluetooth.setFriendlyName accepts char[] and casts when
	 * it sends the data.
	 */
	protected byte[] convertCharsToBytes(char [] charArray, int length) {
		byte [] byteArray = new byte[length];
		for(byte i=0;i<charArray.length;i++)
			byteArray[i] = (byte)charArray[i];
		return byteArray;
	}
	
	/*
	 * !! DEV NOTES: If we get javax.bluetooth and lejos.nxt.comm classes
	 * working with the same data types more consistently can probably
	 * get rid of some of these convert helper methods.
	 * e.g. Bluetooth.getFriendlyName returns char[] or String instead.
	 */
	protected char [] convertBytesToChars(byte [] byteArray) {
		char [] charArray = new char[byteArray.length];
		for(byte i=0;i<charArray.length;i++)
			charArray[i] = (char)byteArray[i];
		return charArray;
	}
	
	/**
	 * The Lego Bluecore code can't retrieve this from the chip.
	 * Always returns hardcoded 0x3e0100 DeviceClass
	 * Untested if this is correct.
	 * @return
	 */
	public DeviceClass getDeviceClass() {
		return new DeviceClass(0x3e0100);
	}

	/**
	 * Normally the mode values are found in javax.bluetooth.DiscoveryAgent.
	 * We don't have this yet in NXJ so use 0 for invisible, any other
	 * value for visible.
	 * @param mode 0 = invisible, all other = visible.
	 * @return true if successful, false if unsuccessful
	 * @throws BluetoothStateException
	 */
	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		/*
		 * DEVELOPER NOTES: javax.bluetooth.DiscoveryAgent.NOT_DISCOVERABLE = 0x00
		 */
		
		// 0x00 = invisible, 0x01=visible 
		int ret = Bluetooth.setVisibility((byte)(mode == 0 ? 0 : 1));
		return (ret >=0);
	}
	
	/**
	 * Power state of the Bluecore 4 chip in the NXT brick. 
	 * @return
	 */
	public static boolean isPowerOn() {
		return Bluetooth.getPower(); 
	}
	
	/**
	 * Indicates whether the NXT brick is visible to other devices 
	 * @return 0 = not discoverable, all other = discoverable
	 */
	public int getDiscoverable() {
		return Bluetooth.getVisibility();
	}
	
	/**
	 * UNIMPLEMENTED! Returns null always.
	 * Returns various properties about the bluetooth implementation
	 * such as version, whether master/slave switch allowed, etc..
	 * Possibly use Properties class in implementation.
	 * @param property
	 * @return
	 */
	public static String getProperty(String property) {
		return null;
	}
	/**
	 * Returns the local Bluetooth address of NXT brick.
	 * @return
	 */
	public String getBluetoothAddress() {
		byte [] addr = Bluetooth.getLocalAddress();
		// !! DEV NOTES: What if it doesn't return proper data?
		return Bluetooth.addressToString(addr);
	}
}
