package lejos.gps;


import java.io.*;
import java.util.*;

/**
 * This class manages data received from a GPS Device.
 * SimpleGPS Class manages the following NMEA Sentences
 * which supply location, heading, and speed data:
 * 
 * GPGGA (location data)
 * GPVTG (heading and speed data)
 * 
 * @author BB
 */
public class SimpleGPS extends Thread {

	/**
	 * BUFF is the amount of bytes to read from the stream at once.
	 * It should not be longer than the number of characters in the shortest 
	 * NMEA sentence otherwise it might cause a bug.
	 */
	private final int BUFF = 20; 
	private byte [] segment = new byte[BUFF];
	private StringBuffer currentSentence = new StringBuffer();
	
	private String START_CHAR = "$";
	
	private InputStream in;
		
	public int errors = 0; // TODO: DELETE ME. Testing purposes only.
	
	//Classes which manages GGA, VTG Sentences
	protected GGASentence ggaSentence;
	protected VTGSentence vtgSentence;
	
	//Data
	private StringTokenizer tokenizer;
	
	// Security
	private boolean shutdown = false;
	
	// Listener-notifier
	static protected Vector listeners = new Vector();

	
	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public SimpleGPS(InputStream in) {
		this.in = in;
		
		ggaSentence = new GGASentence();
		vtgSentence = new VTGSentence();
		
		// Juan: Don't comment out the next line. 
		// This should be a daemon thread so VM exits when user program terminates.
		this.setDaemon(true); // Must be set before thread starts
		this.start();
	}
	
	/* GETTERS & SETTERS */

	/**
	 * Get Latitude
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return ggaSentence.getLatitude();
	}

	
	/**
	 * Get Latitude Direction
	 * 
	 * @return the latitude direction
	 */
	public char getLatitudeDirection(){
		return ggaSentence.getLatitudeDirection();
	}


	/**
	 * Get Longitude
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return ggaSentence.getLongitude();
	}

	/**
	 * Get Longitude Direction
	 * 
	 * @return the longitude direction
	 */
	public char getLongitudeDirection(){
		return ggaSentence.getLongitudeDirection();
	}

	
	/**
	 * The altitude above mean sea level
	 * 
	 * @return Meters above sea level e.g. 545.4
	 */
	public float getAltitude(){
		return ggaSentence.getAltitude();
	}

	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked(){
		return ggaSentence.getSatellitesTracked();
	}

	/**
	 * Get GPS Quality Data
	 * 
	 * @return the quality
	 */
	public int getQuality(){
		return ggaSentence.getQuality();
	}
	
	/**
	 * Get the last time stamp from the satellite for GGA sentence.
	 * 
	 * @return Time as a UTC integer. 123459 = 12:34:59 UTC
	 */
	public int getTimeStamp() { 
		return ggaSentence.getTime();
	}
	
	/**
	 * Get speed in kilometers per hour
	 * 
	 * @return the speed in kilometers per hour
	 */
	public float getSpeed() {
		return vtgSentence.getSpeed();
	}

	/**
	 * Get the course heading of the GPS unit.
	 * @return course (0.0 to 360.0)
	 */
	public float getCourse() {
		return vtgSentence.getTrueCourse();
	}
			
	/**
	 * Method used to close connection. There is no real need to call this method.
	 * Included in case programmer wants absolutely clean exit. 
	 */
	public void close() throws IOException {
		this.shutdown = true;
		in.close();
	}
	
	/**
	 * Keeps reading sentences from GPS receiver stream and extracting data.
	 * This is a daemon thread so when program ends it won't keep running.
	 */
	public void run() {
		String token;
		String s;
		
		while(!shutdown) {
			
			s = getNextString();
			// Check if sentence is valid:
			if(s.indexOf('*') < 0) { 
				continue;
			}
			if(s.indexOf('$') < 0) {
				continue;
			}

			//2008/07/28
			//Debug checksum validation
			//Class 19: java.lang.StringIndexOutOfBoundsException
			// TODO: I suspect we don't need this try-catch block anymore.
			try{
				if(NMEASentence.isValid(s)){
					tokenizer = new StringTokenizer(s);
					token = tokenizer.nextToken();
					// Choose which type of sentence to parse:
					sentenceChooser(token, s); // Method to make subclass more efficient - no redundant code.
				}
			}catch(StringIndexOutOfBoundsException e){
				System.err.println("SimpleGPS.run() error. StringIndexOutOfBounds");
			}catch(ArrayIndexOutOfBoundsException e2){
				//Jab
				//Bug detected: 06/08/2008
				System.err.println("SimpleGPS.run() error. ArrayIndexOutOfBounds");
			}
			//2008/07/18
			//Increase the list with more NMEA Sentences
		
		}
	}

	/**
	 * Internal helper method to aid in the subclass architecture. Overwritten by subclass.
	 * @param token
	 * @param s
	 */
	protected void sentenceChooser(String token, String s) {
		if (token.equals(GGASentence.HEADER)){
			this.ggaSentence.setSentence(s);
			notifyListeners(this.ggaSentence);
		}else if (token.equals(VTGSentence.HEADER)){
			this.vtgSentence.setSentence(s);
			notifyListeners(this.vtgSentence);
		}
	}
	
	static protected void notifyListeners(NMEASentence sen){
		/* TODO: Problem is ggaSentence is a reused object in this API.
		 * Should really pass a copy of the NMEASentence to notify (and the copy
		 * must have all the appropriate GGA data, not just NMEA). However, check
		 *  if there are any listeners before making unnecessary copy. */
		
		for(int i=0; i<listeners.size();i++){
			GPSListener gpsl = (GPSListener)listeners.elementAt(i);
			gpsl.sentenceReceived(sen);
		}
	}

	
	/**
	 * Pulls the next NMEA sentence as a string
	 * @return NMEA string, including $ and end checksum 
	 */
	private String getNextString() {
		boolean done = false;
		String sentence = "";
		int endIndex = 0;

		do{
			// Read in buf length of sentence
			try {
				// TODO: Does in.read() pause the thread or does this eat up unnecessary
				// CPU cycles? Maybe add a Thread.sleep here that cuts out CPU waste.
				// This in.read() method reads in BUFF length of bytes every time.
				in.read(segment);
			}catch (IOException e) {
				// TODO: How to handle error?
			}catch(Exception e){
				// TODO: ??
			}
			// Append char[] data into currentSentence
			for(int i=0;i<BUFF;i++)
				currentSentence.append((char)segment[i]);
			
			// Search for $ symbol (indicates start of new sentence)
			if(currentSentence.indexOf(START_CHAR, 1) >= 0) {
				done = true;
			}
			
			// TODO: Probably better to throw exception here if GPS disconnects
			//In case user turns off GPS Device / GPS Device has low batteries / Other disconnect scenarios
			// There is also the listener of LocationProvider.
			if(currentSentence.length() >= 500){
				errors++;
				//2008/09/06 : JAB
				//Reset
				//currentSentence = new StringBuffer();
				//segment = new byte[BUFF];
				System.err.println("Bug in SimpleGPS.getNextString() detected. > 500");
				System.err.println("Sentence: " + currentSentence.toString());
				
				//If detect a problem with InputStream
				//System detect the event and notify the problem with the
				//Enabling the flag internalError
				return null;
			}
		}while(!done);

		try{
			endIndex = currentSentence.indexOf(START_CHAR, 1);
			sentence = currentSentence.substring(0, endIndex);
			
			// Crop print current sentence
			currentSentence.delete(0, endIndex);
		}catch(Exception e){
			// TODO: Why catch a runtime exception here?
			System.err.println("Exception in SimpleGPS.getNextString() " + e.getMessage());
		}
		
		return sentence;
	}
	
	/* EVENTS*/

	/**
	 * add a listener to manage events with GPS
	 * 
	 * @param listener
	 */
	static public void addListener (GPSListener listener){
		listeners.addElement(listener); 
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	static public void removeListener (GPSListener listener)
	{
		listeners.removeElement(listener); 
	}

	
}