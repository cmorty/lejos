package javax.microedition.location;

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import lejos.gps.BasicGPS;

/**
 * This class is not visible to users and should not be instantiated directly. Instead it
 * is retrieved from the factory method LocationProvider.getInstance().
 * @author BB
 *
 */
class BTGPSLocationProvider extends LocationProvider implements DiscoveryListener {

	BasicGPS gps = null;
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
		// TODO: The problem here is that it searches every time. Slow. Need to try Properties?
		try {
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			da.startInquiry(DiscoveryAgent.GIAC, this);
		} catch (BluetoothStateException e) {
			System.err.println("BT State Exception! " + e.getMessage());
		}
		
		while(!doneInq) {Thread.yield();}
		
		// TODO NEEDED? REDUCE? NEED TO CLOSE SOMETHING?
		try {Thread.sleep(1000);} catch (Exception e) {} 
		
		// TODO: WHat is the procedure if it fails to connect?
		if(btDevice == null) System.err.println("Nothing found. It should exit here.");
		
		String address = btDevice.getBluetoothAddress();
		String btaddy = "btspp://" + address;
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			// TODO: What is procedure if it fails to connect?
			if(scn == null)	System.err.println("BTGPSLOcationProvider.scn is null!");
			
			StreamConnection c = scn.acceptAndOpen();
			InputStream in = c.openInputStream();
			if(in != null) {
				gps = new BasicGPS(in);
				// c.close(); // TODO: Clean up when done. HOW TO HANDLE IN LOCATION?
			}
		} catch(IOException e) {
			System.err.println("IOException in BTLocationProvider");	
		}		
	}
	
	public Location getLocation(int timeout) throws LocationException,
			InterruptedException {
		// TODO The timeout might play to the fact that it is still acquiring satellites!
		// I was wondering about that before. Maybe it makes sense to have timeout in BasicGPS?
		// TODO: Solution! Keep asking for altitude until is positive? (longitude can be negative)
		// Or perhaps just until speed positive? (set those after)
		
		//TODO: Is the purpose of the timeout that it gets a new updated location that
		// is not the previously returned or cached one?
		
		QualifiedCoordinates qc = new QualifiedCoordinates(gps.getLatitude(), gps.getLongitude(), gps.getAltitude());
		Location loc = new Location(qc, gps.getSpeed(), gps.getCourse(), gps.getTimeStamp(),
				0,null); // TODO: Location method and extraInfo
		
		return loc;
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
		/*
		System.err.println(btDevice.getFriendlyName(false) + " discovered.");
		System.err.println("Major = " + cod.getMajorDeviceClass());
		System.err.println("Minor = " + cod.getMinorDeviceClass());
		System.err.println("Service = " + cod.getServiceClasses());
		System.err.println("GPS_MAJOR = " + GPS_MAJOR);
		*/
		// TODO: It should use bitwise to determine major
		if(cod.getMajorDeviceClass() == GPS_MAJOR) {
			this.btDevice = btDevice;
			da.cancelInquiry(this);
		}	
	}

	public void inquiryCompleted(int discType) {
		doneInq = true;
	}
}
