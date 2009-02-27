package lejos.gps;

import java.io.*;
import java.util.*;

import lejos.nxt.Sound; // TODO Eliminate

/**
 * This class manages data received from a GPS Device.
 * BasicGPS Class manages the following NMEA Sentences
 * which supply location, heading, and speed data:
 * 
 * GPGGA (location data)
 * GPVTG (heading and speed data)
 * 
 * @author BB
 */
public class BasicGPS extends Thread {

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
	
	//Security TODO: This "bug fix" needs to go.
	private boolean shutdown = false;
	private boolean updateMode = true; // Start reading GPS sentences as soon as instantiated.
	private boolean internalError = false;
	
	//Data
	private StringTokenizer tokenizer;
	
	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public BasicGPS(InputStream in) {
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
	public float getLatitude() {
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
	public float getLongitude() {
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
	 * Set if GPS Object is going to update internal values or not.
	 * this method is critic to avoid to Crash VM
	 * 
	 * With this way the robot get GPS data onDemand
	 * 
	 * @param status
	 */
	// TODO This method needs to go.
	public void updateValues(boolean status){
		updateMode = status;
	}

	// TODO Is this really needed? In Java exceptions are used for error reporting.
	public boolean existInternalError(){
		return internalError;
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
			
			//2008/08/02
			//If update mode is True, internal values can be updated
			//It is a way to save CPU
			if(updateMode){
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
					System.err.println("BasicGPS.run() error. StringIndexOutOfBounds");
					Sound.beep();
				}catch(ArrayIndexOutOfBoundsException e2){
					//Jab
					//Bug detected: 06/08/2008
					System.err.println("BasicGPS.run() error. ArrayIndexOutOfBounds");
					Sound.buzz();
				}
				//2008/07/18
				//Increase the list with more NMEA Sentences
			}
		}
	}

	/**
	 * Internal helper method to aid in the subclass architecture. Overwritten by subclass.
	 * @param token
	 * @param s
	 */
	protected void sentenceChooser(String token, String s) {
		if (token.equals(GGASentence.HEADER)){
			ggaSentence.setSentence(s);
		}else if (token.equals(VTGSentence.HEADER)){
			vtgSentence.setSentence(s);
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
			
			//I have found the bug
			//In case of turn off GPS Device / GPS Device with low batteries / Other scenarios
			// TODO: What about if they turn off GPS? Does this address that scenario? - BB
			if(currentSentence.length() >= 500){
				errors++;
				//2008/09/06 : JAB
				//Reset
				//currentSentence = new StringBuffer();
				//segment = new byte[BUFF];
				System.err.println("Bug in BasicGPS.getNextString() detected. > 500");
				System.err.println("Sentence: " + currentSentence.toString());
				lejos.nxt.Sound.beepSequenceUp();
				
				//If detect a problem with InputStream
				//System detect the event and notify the problem with the
				//Enabling the flag internalError
				internalError = true;
				updateMode = false;
				return "";
			}
		}while(!done);

		try{
			endIndex = currentSentence.indexOf(START_CHAR, 1);
			sentence = currentSentence.substring(0, endIndex);
			
			// Crop print current sentence
			currentSentence.delete(0, endIndex);
		}catch(Exception e){
			// TODO: Why catch a runtime exception here?
			System.err.println("Exception in BasicGPS.getNextString() " + e.getMessage());
		}
		
		return sentence;
	}
}