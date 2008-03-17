package javax.bluetooth;

import lejos.nxt.comm.Bluetooth;

/**
 * Currently unimplemented. Singleton class.
 * @author BB
 *
 */
public class LocalDevice {
	
	private static LocalDevice localDevice;
	
	private LocalDevice() {
		
	}
	
	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		if(localDevice == null)
			localDevice = new LocalDevice();
		return localDevice;
	}
	
	public String getFriendlyName() {
		char [] nameChars = ConvertBytesToChars(Bluetooth.getFriendlyName());
		return new String(nameChars);
	}
	
	private char [] ConvertBytesToChars(byte [] byteArray) {
		char [] charArray = new char[byteArray.length];
		for(byte i=0;i<charArray.length;i++)
			charArray[i] = (char)byteArray[i];
		return charArray;
	}
	
	public DeviceClass getDeviceClass() {
		return null;
	}
	/**
	 * 
	 * @param mode Unsure if this is same as javax.bluetooth.DiscoveryAgent.LIAC
	 * @return true if successful, false if unsuccessful
	 * @throws BluetoothStateException
	 */
	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		/*
		 * DEVELOPER NOTES: I'm not sure if the mode values are
		 * the same for Bluetooth.setVisibility(mode) and javax.bluetooth.DiscoveryAgent.LIAC
		 */
		int ret = Bluetooth.setVisibility((byte)mode);
		return (ret >=0);
	}
	
	public static boolean isPowerOn() {
		return Bluetooth.getPower(); 
	}
	
	public int getDiscoverable() {
		return Bluetooth.getVisibility();
	}
	
	/**
	 * UNIMPLEMENTED!
	 * Possibly use Properties class in implementation.
	 * @param property
	 * @return
	 */
	public static String getProperty(String property) {
		return null;
	}
	/**
	 * !! NOT PROPERLY IMPLEMENTED: Convert from hex values?
	 * @return
	 */
	public String getBluetoothAddress() {
		char [] nameChars = ConvertBytesToChars(Bluetooth.getLocalAddress());
		return new String(nameChars);
	}
}
