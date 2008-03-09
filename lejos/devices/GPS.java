package lejos.devices;

import java.io.*;
import lejos.nxt.comm.Debug;

/**
 * Class to pull data from a GPS receiver
 * @author BB
 *
 */

/* DEVELOPER NOTES:
 * In general this could be improved by parsing out the appropriate 
 * fields only at the time they are called, rather than parsing them
 * all at once as can be seen in the run() method. 
 * This would save some CPU cycles since it would work to extract
 * data that the user wants, rather than parsing data that ends 
 * up going unused.
 */
public class GPS extends Thread {

	/**
	 * BUFF is the amount of bytes to read from the stream at once.
	 * It should not be longer than the shortest NMEA sentence otherwise
	 * it might cause a bug.
	 */
	private final int BUFF = 20; 
	private byte [] segment = new byte[BUFF];
	private StringBuffer currentSentence = new StringBuffer();
	
	private String START_CHAR = "$";
	private String GGA_STR = "GGA";
	
	private InputStream in;
	
	private String BLANK = ""; // DELETE ME
	
	private int time = 0;
	private float latitude = 0;
	private float longitude = 0;
	private float altitude = 0;
	private byte satellites_tracked = 0;
	
	/**
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public GPS(InputStream in) {
		this.in = in;
		this.setDaemon(true); // Must be set before thread starts
		this.start();
	}
	
	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	/**
	 * The altitude above mean sea level
	 * @return Meters above sea level e.g. 545.4     
	 */
	public float getAltitude() {
		return altitude;
	}
	
	/**
	 * Returns the last time stamp retrieved from a satellite
	 * 
	 * @return The time as a UTC integer. 123519 = 12:35:19 UTC
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * @return Number of satellites e.g. 8
	 */
	public byte getSatellitesTracked() {
		return satellites_tracked;
	}
	
	/**
	 * Placeholder idea: returns heading (from North) based on
	 * previous lat/long reading.
	 * @return
	 */
	public int getHeading() {
		return -1;
	}
	
	/**
	 * Placeholder idea: returns speed based on previous lat/long reading.
	 * @return
	 */
	public int getSpeed() {
		return -1;
	}
	
	public void addGPSListener() {
		/* Placeholder method */
	}
	
	/**
	 * Placeholder Idea: Set a latitude/longitude as origin, then
	 * it will return x, y coordinates (in CM or Inches).
	 * By default, uses first reading as origin.
	 * Need to make methods for getX() and getY(), setUnits()
	 * @param longitude
	 * @param latitude
	 */
	public void setOriginPoint(String longitude, String latitude) {
		
	}
	
	/**
	 * Keeps reading sentences from GPS receiver stream and extracting data.
	 * This is a daemon thread so when program ends it won't keep running.
	 */
	public void run() {
		/* Code holder for parsing values */
		
		while(true) {
			String s = getNextString();
			
			// Check if sentence is valid:
			if(s.indexOf('*') < 0) { 
				Debug.out("Error no * caught!\n");
				Debug.out("String: " + s + "\n");
				continue;
			}
			if(s.indexOf('$') < 0) {
				Debug.out("Error no $ caught!\n");
				Debug.out("String: " + s + "\n");
				continue;
			}
			
			// Make NMEASentence
			NMEASentence sen = new NMEASentence(s);
			Debug.out("String: " + s + "\n");
			
			// Check if valid (discard if it is invalid)
			if(sen.isValid()) {
				
				// Check if contains lat/long data
				if(sen.getDataType().equals(GGA_STR)) {
					
					// Update all global vars
					//time = Integer.parseInt((String)sen.getDataFields().elementAt(0)); // Convert string to int
					
					latitude = Float.parseFloat((String)sen.getDataFields().elementAt(1));
					longitude = Float.parseFloat((String)sen.getDataFields().elementAt(3));
					altitude = Float.parseFloat((String)sen.getDataFields().elementAt(7));
					
					//satellites_tracked  = (byte)Integer.parseInt((String)sen.getDataFields().elementAt(9));
				}
			// Notify appropriate listeners if data changed
			}
		}
	}
	
	/**
	 * Pulls the next NMEA sentence as a string
	 * @return NMEA string, including $ and end checksum 
	 */
	private String getNextString() {
		boolean done = false;
		do {
			// Read in buf length of sentence
			try {
				in.read(segment);
			} catch (IOException e) {
				// How to handle error?
			}
			// Append char[] data into currentSentence
			for(int i=0;i<BUFF;i++)
				currentSentence.append((char)segment[i]);
			
			// Search for $ symbol (indicates start of new sentence)
			if(currentSentence.indexOf(START_CHAR, 1) >= 0) {
				done = true;
			}
			
		} while(!done);
		
		int endIndex = currentSentence.indexOf(START_CHAR, 1);
		String sentence = currentSentence.substring(0, endIndex);
		
		// Crop out current sentence
		currentSentence.delete(0, endIndex);
		
		return sentence;
	}
}