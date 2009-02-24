package lejos.gps;

import java.io.*;
import java.util.*;

/**
 * This class manages data received from a GPS Device.
 * BasicGPS Class manages the following NMEA Sentences:
 * 
 * GPGGA
 * GPVTG
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
	
	//GGA
	protected int RAWtime = 0;
	private float latitude;
	private char latitudeDirection;
	private float longitude;
	private char longitudeDirection;
	private float altitude = 0;
	protected int satellitesTracked = 0;
	public static final int MINIMUM_SATELLITES_TO_WORK = 4;
	public static final int MAXIMUM_SATELLITES_TO_WORK = 12;
	//private float hdop = 0; // TODO Seems unused.
	private int quality = 0;
	
	// VTG
	private float speed = 0;
	private float heading = 0;
	
	//Classes which manages GGA, VTG Sentences
	protected GGASentence ggaSentence;
	protected VTGSentence vtgSentence;
	
	//Security
	private boolean shutdown = false;
	private boolean updateMode = false;
	
	//Data
	private String sentence;
	private StringTokenizer tokenizer;
	
	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public BasicGPS(InputStream in) {
		ggaSentence = new GGASentence();
		vtgSentence = new VTGSentence();
		
		this.in = in;
		// Juan: Don't comment out the next line! This should be a daemon thread so VM exits when user program terminates.
		this.setDaemon(true); // Must be set before thread starts
		this.start();
	}
	

	/* GETTERS & SETTERS */

	/**
	 * Get NMEA Sentence
	 * 
	 * @return the NMEA Sentence
	 */
	public String getSentence(){
		return sentence;
	}

	/**
	 * Get Latitude
	 * 
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	
	/**
	 * Get Latitude Direction
	 * 
	 * @return the latitude direction
	 */
	public char getLatitudeDirection(){
		return latitudeDirection;
	}


	/**
	 * Get Longitude
	 * 
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * Get Longitude Direction
	 * 
	 * @return the longitude direction
	 */
	public char getLongitudeDirection(){
		return longitudeDirection;
	}

	
	/**
	 * The altitude above mean sea level
	 * 
	 * @return Meters above sea level e.g. 545.4
	 */
	public float getAltitude(){
		return altitude;
	}

	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked(){
		return satellitesTracked;
	}

	/**
	 * Get GPS Quality Data
	 * 
	 * @return the quality
	 */
	public int getQuality(){
		return quality;
	}
	
	/**
	 * Get speed in kilometers per hour
	 * 
	 * @return the speed in kilometers per hour
	 */
	public float getSpeed() {
		return speed;
	}

	public float getCourse() {
		return heading;
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

	/**
	 * Method used to finish the Thread
	 */
	// TODO Not sure why we care about shutting down this thread. It is a daemon.
	public void shutDown(){
		this.shutdown = true;
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
				System.err.println("String: " + s);

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
				try{
					if(NMEASentence.isValid(s)){
						tokenizer = new StringTokenizer(s);
						token = tokenizer.nextToken();
						// Choose which type of sentence to parse
						sentenceChooser(token, s); // Method to make subclass more efficient - no redundant code.
					}
				}catch(StringIndexOutOfBoundsException e){
					//Sound.beep();
				}catch(ArrayIndexOutOfBoundsException e2){
					//Jab
					//Bug detected: 06/08/2008
					//Sound.buzz();
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
			parseGGA(s);
		}else if (token.equals(VTGSentence.HEADER)){
			parseVTG(s);
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
				// How to handle error?
			}catch(Exception e){
				// ??
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
			if(currentSentence.length() >= 500){
				//2008/09/06 : JAB
				//Reset
				//currentSentence = new StringBuffer();
				//segment = new byte[BUFF];

				//If detect a problem with InputStream
				//System detect the event and notify the problem with the
				//Enabling the flag internalError
				// TODO: Note I deleted internalError from this. Still in subclass though. Might
				// mess up Juan's bug fix but I think this will all go away.
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
			
		}
		
		return sentence;
	}

	/* NMEA */

	/**
	 * This method parse a GGA Sentence
	 * 
	 * @param nmeaSentece
	 */
	protected void parseGGA(String nmeaSentence){

		ggaSentence.setSentence(nmeaSentence);
		ggaSentence.parse();
		
		this.RAWtime = ggaSentence.getTime();
		
		this.latitude = ggaSentence.getLatitude();
		this.latitudeDirection = ggaSentence.getLatitudeDirection();
		this.longitude = ggaSentence.getLongitude();
		this.longitudeDirection = ggaSentence.getLongitudeDirection();
		this.satellitesTracked = ggaSentence.getSatellitesTracked();
		this.altitude = ggaSentence.getAltitude();
		this.quality = ggaSentence.getQuality();
	}
		
	/**
	 * This method parse a VTG Sentence
	 * 
	 * @param nmeaSentece
	 */
	protected void parseVTG(String nmeaSentence){
		vtgSentence.setSentence(nmeaSentence);
		vtgSentence.parse();
		
		this.speed = vtgSentence.getSpeed();
		this.heading = vtgSentence.getTrueCourse();
		// On my Holux-1200 the VTG sentence leaves this blank:
		//this.compassDegrees = vtgSentence.getMagneticCourse();
	}
}