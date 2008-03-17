package javax.bluetooth;

/**
 * The DeviceClass class represents the class of device (CoD) record as defined by the Bluetooth specification. This record is defined in the Bluetooth Assigned Numbers document and contains information on the type of the device and the type of services available on the device.
 * The Bluetooth Assigned Numbers document ( http://www.bluetooth.org/assigned-numbers/baseband.htm) defines the service class, major device class, and minor device class. The table below provides some examples of possible return values and their meaning:
 * Method	Return Value	Class of Device
 * getServiceClasses() 	0x22000 	Networking and Limited Discoverable Major Service Classes
 * getServiceClasses() 	0x100000 	Object Transfer Major Service Class
 * getMajorDeviceClass() 	0x00 	Miscellaneous Major Device Class
 * getMajorDeviceClass() 	0x200 	Phone Major Device Class
 * getMinorDeviceClass() 	0x0C	With a Computer Major Device Class, Laptop Minor Device Class
 * getMinorDeviceClass() 	0x04	With a Phone Major Device Class, Cellular Minor Device Class
 * @author BB
 *
 */
public class DeviceClass {

	private static final int SERVICE_MASK = 0xffe000;
	private static final int MAJOR_MASK = 0x001f00;
	private static final int MINOR_MASK = 0x0000fc;
	
	int major;
	int minor;
	int service;
		
	public DeviceClass(int record) {
		major = record & MAJOR_MASK; 
		minor = record & MINOR_MASK;
		service = record & SERVICE_MASK;
	}
	
	/**
	 * Retrieves the major service classes. A device may have multiple major service classes. When this occurs, the major service classes are bitwise OR'ed together.
	 * @return the major service classes
	 */
	public int getServiceClasses() {
		return service;
	}

	/**
	 * Retrieves the major device class. A device may have only a single major device class.
	 * @return the major device class
	 */
	public int getMajorDeviceClass() {
		return major;
	}
	
	public int getMinorDeviceClass() {
		return minor;
	}
}
