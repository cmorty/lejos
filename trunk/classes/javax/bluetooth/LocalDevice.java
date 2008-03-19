package javax.bluetooth;

import lejos.nxt.comm.Bluetooth;

/**
 * Currently unimplemented. Singleton class.
 * @author BB
 *
 */
public class LocalDevice {
	
	private static LocalDevice localDevice;
	
	/**
	 * DEVELOPER NOTES:
	 * The values in cs are redundant from RemoteDevice. 
	 * Might save some memory by recycling it.
	 */
	private static final char[] cs = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
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
		char [] nameChars = ConvertBytesToChars(Bluetooth.getFriendlyName());
		String fName = new String(nameChars);  
		return fName.substring(0, fName.indexOf(0)); // Clip off extra chars
	}
	
	private char [] ConvertBytesToChars(byte [] byteArray) {
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
		/*
		 * DEVELOPER NOTES:
		 * Most of the code in this method is redundant from 
		 * RemoteDevice.getBluetoothAddress(). Might be able to 
		 * save memory by reusing code. 
		 */
		char[] caddr = new char[12];
		
		byte [] addr = Bluetooth.getLocalAddress();
		int ci = 0;
		int nr = 0;
		int addri = 0;
		
		for(int i=0; i<6; i++) {
			addri = (int)addr[i];
			nr = (addri>=0) ? addri : (256 + addri);	
			caddr[ci++] = cs[nr / 16];
			caddr[ci++] = cs[nr % 16];
		}
		return new String(caddr);
	}
}
