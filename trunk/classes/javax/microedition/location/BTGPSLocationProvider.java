package javax.microedition.location;

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import lejos.gps.GPS;

/**
 * This class is not visible to users and should not be instantiated directly. Instead you should
 * retrieve it from the factory method LocationProvider.getInstance().
 * @author BB
 *
 */
class BTGPSLocationProvider extends LocationProvider implements DiscoveryListener {

	GPS gps = null;
	DiscoveryAgent da;
	RemoteDevice btDevice = null;
	/**
	 * doneInq is used to ensure the code doesn't try to connect to the GPS device
	 * before the Bluecore chip is done the inquiry. If you try to connect before the inquiry is
	 * done it will cause a malfunction. This is due to our Bluecove code in leJOS, which requires
	 * the programmer to be very careful. 
	 */
	boolean doneInq = false;
		
	private static final int GPS_MAJOR = 0x1F00;
	private static final int LOCATOR_SERVICE = 0x1F0000;
	
	protected BTGPSLocationProvider() {
		System.err.println("We're in the constructor");
		
		try {
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			System.err.println("Made DiscoveryAgent");
			
			da.startInquiry(DiscoveryAgent.GIAC, this);
			System.err.println("Started inquiry");
			
		} catch (BluetoothStateException e) {
			System.err.println("BT State Exception!");
		}
		
		while(!doneInq) {Thread.yield();}
		
		try {Thread.sleep(1000);} catch (Exception e) {} // TODO NEEDED? REDUCE? NEED TO CLOSE SOMETHING?
		
		System.err.println("Search complete!");
		if(btDevice == null) System.err.println("Nothing found. It should exit here.");
		String address = btDevice.getBluetoothAddress();
		System.err.println("Got address " + address);
		String btaddy = "btspp://" + address;
		System.err.println(btaddy);
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			if(scn == null)
				System.err.println("OOPS It is null!");
			
			System.err.println("About to open StreamConnection");
			StreamConnection c = scn.acceptAndOpen();
			System.err.println("About to open stream");
			InputStream in = c.openInputStream();
			if(in != null) {
				System.err.println("Success");
				gps = new GPS(in);
				gps.updateValues(true); // TODO: REMOVE IF THIS BUG IS ADDRESSED ELSEWHERE
				System.err.println("Got GPS");
				System.err.println("Mode: " + gps.getMode());
				
				// c.close(); // Clean up when done. HOW TO HANDLE IN LOCATION?
			}
		} catch(IOException e) {
			System.err.println("Oopsies! IOException in BTLocationProvider");	
		}
		
	}
	
	public Location getLocation(int timeout) throws LocationException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void setLocationListener(LocationListener listener, int interval,
			int timeout, int maxAge) {
		// TODO Auto-generated method stub
	}

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		System.err.println(btDevice.getFriendlyName(false) + " discovered.");
		System.err.println("Major = " + cod.getMajorDeviceClass());
		System.err.println("Minor = " + cod.getMinorDeviceClass());
		System.err.println("Service = " + cod.getServiceClasses());
		System.err.println("GPS_MAJOR = " + GPS_MAJOR);
		// TODO: It should use bitwise to determine major?
		if(cod.getMajorDeviceClass() == GPS_MAJOR) {
			this.btDevice = btDevice;
			System.err.println("About to cancel inquiry()");
			da.cancelInquiry(this);
			System.err.println("Canceled it within method.");
		}	
	}

	public void inquiryCompleted(int discType) {
		System.err.println("Inquiry completed!");
		doneInq = true;
	}
}
