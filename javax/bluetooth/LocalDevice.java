package javax.bluetooth;

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
		return null;
	}
	
	public DeviceClass getDeviceClass() {
		return null;
	}
	
	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		return true;
	}
	
	public static boolean isPowerOn() {
		return true; 
	}
	
	public int getDiscoverable() {
		return 0;
	}
	
	/**
	 * Possibly use Properties class in implementation.
	 * @param property
	 * @return
	 */
	public static String getProperty(String property) {
		return null;
	}
	
	public String getBluetoothAddress() {
		return null;
	}
	
	
}
