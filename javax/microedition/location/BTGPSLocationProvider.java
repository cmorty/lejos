package javax.microedition.location;

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;


import lejos.gps.*;

/**
 * This class is not visible to users and should not be instantiated directly. Instead it
 * is retrieved from the factory method LocationProvider.getInstance().
 * @author BB
 *
 */
class BTGPSLocationProvider extends LocationProvider implements DiscoveryListener, GPSListener {

	SimpleGPS gps = null;
	DiscoveryAgent da;
	RemoteDevice btDevice = null;
	
	// LocationListener variables:
	private Thread listyThread = null;
	private boolean listenerRunning = true;
	private GPSListener gpsl = null; // JSR-179 only allows one LocationListener at a time.
	
	/**
	 * doneInq is used to ensure the code doesn't try to connect to the GPS device
	 * before the Bluecore chip is done the inquiry. If you try to connect before the inquiry is
	 * done it will cause a malfunction. This is due to our Bluecove code in leJOS, which requires
	 * the programmer to be very careful. 
	 */
	boolean doneInq = false;
	
	// I think this indicates the BT device is a GPS unit:
	private static final int GPS_MAJOR = 0x1F00;
	
	protected BTGPSLocationProvider() {
		
		// TODO: Move this to searchConnect method?
		// TODO: The problem here is that it searches every time. Slow. Need to try Properties?
		// TODO: BIG ONE: Should only connect to GPS that isPaired() (from menu). Will
		// allow some degree of control over which GPS is connects to in classroom.
		
		try {
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			da.startInquiry(DiscoveryAgent.GIAC, this);
		} catch (BluetoothStateException e) {
			System.err.println("BT State Exception! " + e.getMessage());
		}
		
		while(!doneInq) {Thread.yield();}
		
		// TODO NEEDED? REDUCE? NEED TO CLOSE SOMETHING?
		//try {Thread.sleep(200);} catch (Exception e) {} 
		
		// TODO: What is the procedure if it fails to connect? Return? Throw BT exception?
		if(btDevice == null) System.err.println("Nothing found. It should exit here.");
		
		String address = btDevice.getBluetoothAddress();
		String btaddy = "btspp://" + address;
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			// TODO: What is procedure if it fails to connect?
			if(scn == null)	System.err.println("BTGPSLocationProvider.scn is null!");
			
			StreamConnection c = scn.acceptAndOpen();
			InputStream in = c.openInputStream();
			if(in != null) {
				gps = new SimpleGPS(in);
				// c.close(); // TODO: Clean up when done. HOW TO HANDLE IN LOCATION?
			}
		} catch(IOException e) {
			System.err.println("IOException in BTLocationProvider");	
		}
		
		// Add itself to SimpleGPS as listener
		SimpleGPS.addListener(this);
	}
	
	public Location getLocation(int timeout) throws LocationException,
			InterruptedException {
		/* TODO The timeout might play to the fact that it is still acquiring satellites?
		 * I was wondering about that before. Maybe it makes sense to have timeout in SimpleGPS?
		 * TODO: Solution! Keep asking for altitude until is positive? (longitude can be negative)
		 * Or perhaps just until speed positive? (set those after)
		 * TODO: -1 in timeout is supposed to represent the default timeout (GPSListener?)
		 * TODO: I don't know if this is supposed to wait for the GPS to provide a new
		 * coordinate data or if it is okay to pass the latest cached GPS coordinates.
		 * Is the purpose of the timeout that it gets a new updated location that
		 * is not the previously returned or cached one? 
		*/
		
		if(timeout == 0)
			throw new IllegalArgumentException("timeout cannot equal 0");
		
		// Timeout results in LocationException:
		long startTime = System.currentTimeMillis();
		
		while(gps.getLatitude() == 0 & gps.getLongitude() == 0) {
			if(timeout != -1 & System.currentTimeMillis() - startTime > (timeout * 1000))
				throw new LocationException("GPS timed out");
			Thread.sleep(100); /* NOTE: This might very occasionally cause an error because
			* Thread.yield() seems to cause sentence parsing to start too soon. */ 
		}
		
		QualifiedCoordinates qc = new QualifiedCoordinates(gps.getLatitude(), gps.getLongitude(), gps.getAltitude());
		Location loc = new Location(qc, gps.getSpeed(), gps.getCourse(), gps.getTimeStamp(),
				0,null); // TODO: Implement location method and extraInfo (0 and null for now)
		
		return loc;
	}
	

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	/**
	 * TODO: Copy the docs from regular API source code (formatting too)
	 */
	public void setLocationListener(LocationListener listener, int interval,
			int timeout, int maxAge) {
		
		// * Stop all previous listener threads *
		listenerRunning = false;
		if(listyThread != null) {
			System.err.println("About to end old thread. Alive? " + listyThread.isAlive());
			while(listyThread.isAlive()) {Thread.yield();} // End old thread
			System.err.println("Ended. Alive? " + listyThread.isAlive());
			listyThread = null; // Discard the listener thread instance 
		}
		
		// * Remove any listeners from GPSListener *
		if (listener == null) {
			// TODO: Remove current listener from SimpleGPS
			SimpleGPS.removeListener(gpsl);
			gpsl = null;
			return; // No listener provided, so return now so it dosn't make a new one
		}
		
		// * Inner classes need final variables *
		final int to = timeout;
		final LocationListener l = listener;
		final LocationProvider lp = this;
		final int delay = interval * 1000; // Oddly interval is in seconds, and not float
		
		// Make new thread here and start it if interval > 0, else if -1 
		// then use the GPSListener interface.
		if (interval > 0) { // Notify according to interval by user
			listyThread = new Thread() {
				public void run() {
					while(listenerRunning) {
						try {
							l.locationUpdated(lp, lp.getLocation(to));
							Thread.sleep(delay);
						} catch (LocationException e) {
							// TODO Auto-generated catch block
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			};
			listyThread.setDaemon(true); // so JVM exits if thread is still running
			listenerRunning = true;
			listyThread.start();
		} else if(interval < 0) { // If interval is -1, use default update interval
			// In our case, update as soon as new coordinates are available from GPS (via GPSListener) 
			// TODO: Alternate method: Use GPSListener for ProximityListener and this.
			gpsl = new GPSListener() {
				public void sentenceReceived(NMEASentence sen) {
					// Check if GGASentence. Means that new location info is ready
					if(sen.getHeader().equals(GGASentence.HEADER)) {
						try {
							l.locationUpdated(lp, lp.getLocation(to));
						} catch (LocationException e) {
							// TODO Auto-generated catch block
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			};
			SimpleGPS.addListener(gpsl);
		}
		
		// TODO: Need to implement LocationListener.providerStateChanged()  
	}
	
	/**
	 * This method is from GPSListener, used to notify the ProximityListener. 
	 */
	public void sentenceReceived(NMEASentence sen) {
		// Check if this sentence has location info
		if(sen.getHeader().equals(GGASentence.HEADER)) {
			Coordinates cur = null;
			Location loc = null;
			try {
				
				loc = this.getLocation(-1);
				cur = loc.getQualifiedCoordinates();
			} catch(InterruptedException e) {
				// TODO: This method should bail properly if it fails.
				System.err.println("Fail 1");
			} catch (LocationException e) {
				// TODO: This method should bail properly if it fails.
				System.err.println("Fail 2");
			}
			for(int i=0; i<listeners.size();i++){
				Object [] array = (Object [])listeners.elementAt(i);
				ProximityListener pl = (ProximityListener)array[0];
				Coordinates to = (Coordinates)array[1];
				Float rad = (Float)array[2];
				// Now check radius against coordinate and notify listener.
				if(cur.distance(to) <= rad.floatValue()) {
					// Remove this ProximityListener because it should be notified only once.
					// I prefer to do this BEFORE notifying the pl because the user might try
					// to re-add the pl in proximityEvent().
					LocationProvider.removeProximityListener(pl);
					pl.proximityEvent(to, loc);
				}
			}
			
			// TODO: Handle LocationListeners here instead of inner?
		}
	}
	
	/* DiscoveryListener methods: */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// TODO: It should not output this to err. Delete when done troubleshooting:
		System.err.println(btDevice.getFriendlyName(false) + " discovered.");
		/*
		System.err.println("Major = " + cod.getMajorDeviceClass());
		System.err.println("Minor = " + cod.getMinorDeviceClass());
		System.err.println("Service = " + cod.getServiceClasses());
		System.err.println("GPS_MAJOR = " + GPS_MAJOR);
		System.err.println("Authenticated? " + btDevice.isAuthenticated());
		*/
		
		if((cod.getMajorDeviceClass() & GPS_MAJOR) == GPS_MAJOR) {
			if(btDevice.isAuthenticated()) { // Check if paired.
				this.btDevice = btDevice;
				da.cancelInquiry(this);
			}
		}	
	}

	public void inquiryCompleted(int discType) {
		doneInq = true;
	}
}
