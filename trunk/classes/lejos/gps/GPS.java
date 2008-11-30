package lejos.gps;

import java.io.*;
import java.util.*;

/**
 * This class manages a data received from a GPS Device.
 * GPS Class manages the following NMEA Sentences:
 * 
 * GPGGA
 * GPRMC
 * GPVTG
 * GPGSV
 * GPGSA
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 *
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
	
	private InputStream in;
	
	//GGA
	private int RAWtime = 0;
	private float latitude;
	private String latitudeDirection = "";
	private float longitude;
	private String longitudeDirection = "";
	private float altitude = 0;
	private int satellitesTracked = 0;
	public static final int MINIMUM_SATELLITES_TO_WORK = 4;
	public static final int MAXIMUM_SATELLITES_TO_WORK = 12;
	private float hdop = 0;
	private int quality = 0;
	
	//RMC
	private float speed = 0;
	private int RAWdate = 0;
	private float compassDegrees  = 0;
	
	//GSV
	private NMEASatellite[] ns;
	
	//GSA
	private String mode = "";
	private int modeValue = 0;
	private int[] SV;
	private float PDOP = 0;
	private float HDOP = 0;
	private float VDOP = 0;
	
	//Classes which manages GGA, RMC, VTG, GSV, GSA Sentences
	private GGASentence ggaSentence;
	private RMCSentence rmcSentence;
	private VTGSentence vtgSentence;
	private GSVSentence gsvSentence;
	private GSASentence gsaSentence;

	//Date Object with use GGA & RMC Sentence
	private Date date;

	//Security
	private boolean shutdown = false;
	private boolean updateMode = false;
	private boolean internalError = false;

	//Data
	private String sentence;
	private StringTokenizer tokenizer;
	
	// Use Vector to keep compatibility with J2ME
	private Vector listeners = new Vector();

	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public GPS(InputStream in) {
		ggaSentence = new GGASentence();
		rmcSentence = new RMCSentence();
		vtgSentence = new VTGSentence();
		gsvSentence = new GSVSentence();
		ns = new NMEASatellite[4];
		gsaSentence = new GSASentence();
		SV = new int[12];
		
		date = new Date();
		
		this.in = in;
		//this.setDaemon(true); // Must be set before thread starts
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
	public String getLatitudeDirection(){
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
	public String getLongitudeDirection(){
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

	/**
	 * Return Compass Degrees
	 * in a range: 0-359
	 * 
	 * @return the compass degrees
	 */
	public int getCompassDegrees(){
		return Math.round(compassDegrees);
	}
	
	/**
	 * Return a Date Object with data from GGA and RMC NMEA Sentence
	 * 
	 * @return the date
	 */
	public Date getDate(){
		return date;
	}

	/**
	 * 
	 * Get NMEA Satellite
	 * 
	 * @param index the satellite index
	 * @return the NMEASaltellite object for the selected satellite
	 */
	public NMEASatellite getSatellite(int index){
		return ns[index];
	}
	
	/**
	 * Get Mode1
	 * 
	 * @return mode1
	 */
	public String getMode(){
		return mode;
	}

	/**
	 * Get Mode2
	 * 
	 * @return mode2
	 */
	public int getModeValue(){
		return modeValue;
	}
	
	/**
	 * Get an Array with Satellite ID
	 * 
	 * @return array of satellite IDs
	 */
	public int[] getSV(){
		return SV;
	}
	
	/**
	 * Get PDOP
	 * 
	 * @return the PDOP
	 */
	public float getPDOP(){
		return PDOP;
	}

	/**
	 * Get HDOP
	 * 
	 * @return the HDOP
	 */
	public float getHDOP(){
		return HDOP;
	}

	/**
	 * Get VDOP
	 * 
	 * @return the VDOP
	 */
	public float getVDOP(){
		return VDOP;
	}

	/**
	 * Get true or false in relation to 2 factors:
	 * 
	 * + Number of Satellites GGA -> Number of Satellites
	 * + Quality of data GSA -> Mode: A & Value:3
	 * 
	 * @return GPS status
	 */
	public boolean getGPSStatus(){
		boolean status = false;
		if(
			(satellitesTracked >= MINIMUM_SATELLITES_TO_WORK) && 
			//(mode.equals("A")) &&
			(modeValue == 3)){
			
			status = true;
		}
		return status;
	}
	
	/**
	 * Set if GPS Object is going to update internal values or not.
	 * this method is critic to avoid to Crash VM
	 * 
	 * With this way the robot get GPS data onDemand
	 * 
	 * @param status
	 */
	public void updateValues(boolean status){
		updateMode = status;
	}

	/**
	 * Method used to finish the Thread
	 */
	public void shutDown(){
		this.shutdown = true;
	}
	
	public boolean existInternalError(){
		return internalError;
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
				try{
					if(NMEASentence.isValid(s)){
						tokenizer = new StringTokenizer(s);
						token = tokenizer.nextToken();

						if (token.equals(GGASentence.HEADER)){
							parseGGA(s);
						}else if (token.equals(RMCSentence.HEADER)){
							parseRMC(s);
						}else if (token.equals(VTGSentence.HEADER)){
							parseVTG(s);
						}else if (token.equals(GSVSentence.HEADER)){
							parseGSV(s);
						}else if (token.equals(GSASentence.HEADER)){
							parseGSA(s);
						}
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
			
		}
		
		return sentence;
	}

	/* NMEA */

	/**
	 * This method parse a GGA Sentence
	 * 
	 * @param nmeaSentece
	 */
	private void parseGGA(String nmeaSentence){

		ggaSentence.setSentence(nmeaSentence);
		ggaSentence.parse();
		
		this.RAWtime = ggaSentence.getTime();
		updateTime();

		this.latitude = ggaSentence.getLatitude();
		this.latitudeDirection = ggaSentence.getLatitudeDirection();
		this.longitude = ggaSentence.getLongitude();
		this.longitudeDirection = ggaSentence.getLongitudeDirection();
		this.satellitesTracked = ggaSentence.getSatellitesTracked();
		this.altitude = ggaSentence.getAltitude();
		this.quality = ggaSentence.getQuality();

		//Events
		fireGGASentenceReceived(ggaSentence);
	}

	/**
	 * Update Time values
	 */
	private void updateTime(){
		String rt = Integer.toString(this.RAWtime);
		int hh;
		int mm;
		int ss;

		if(rt.length()<6){
			hh = Integer.parseInt(rt.substring(0, 1));
			mm = Integer.parseInt(rt.substring(1, 3));
			ss = Integer.parseInt(rt.substring(3, 5));
		}else{
			hh = Integer.parseInt(rt.substring(0, 2));
			mm = Integer.parseInt(rt.substring(2, 4));
			ss = Integer.parseInt(rt.substring(4, 6));
		}

		//updateTimeValues(hh, mm, ss);
		date.setHours(hh);
		date.setMinutes(mm);
		date.setSeconds(ss);
	}

	/**
	 * This method parse a RMC Sentence
	 * 
	 * @param nmeaSentece
	 */
	private void parseRMC(String nmeaSentence){
		rmcSentence.setSentence(nmeaSentence);
		rmcSentence.parse();
		
		//Charles Manning notes
		//Is better use VTG instead of RMC
		//this.speed = rmcSentence.getSpeed();
		this.RAWdate = rmcSentence.getDate();
		this.compassDegrees = rmcSentence.getCompassDegrees();
		
		updateDate();

		//Events
		fireRMCSentenceReceived(rmcSentence);
	}

	/**
	 * Update Date values
	 */
	private void updateDate(){
		String rd = Integer.toString(this.RAWdate);
		int yy;
		int mm;
		int dd;

		if(rd.length()<6){
			dd = Integer.parseInt(rd.substring(0, 1));
			mm = Integer.parseInt(rd.substring(1, 3));
			yy = Integer.parseInt(rd.substring(3, 5));
		}else{
			dd = Integer.parseInt(rd.substring(0, 2));
			mm = Integer.parseInt(rd.substring(2, 4));
			yy = Integer.parseInt(rd.substring(4, 6));
		}

		//updateDateValues(dd,mm,yy);
		date.setDay(dd);
		date.setMonth(mm);
		date.setYear(yy);
	}

	/**
	 * This method parse a VTG Sentence
	 * 
	 * @param nmeaSentece
	 */
	private void parseVTG(String nmeaSentence){
		vtgSentence.setSentence(nmeaSentence);
		vtgSentence.parse();
		
		this.speed = vtgSentence.getSpeed();

		//Events
		fireVTGSentenceReceived (vtgSentence);
	}

	/**
	 * This method parse a GSV Sentence
	 * 
	 * @param nmeaSentece
	 */
	private void parseGSV(String nmeaSentence){
		gsvSentence.setSentence(nmeaSentence);
		gsvSentence.parse();

		this.ns[0] = gsvSentence.getSatellite(0);
		this.ns[1] = gsvSentence.getSatellite(1);
		this.ns[2] = gsvSentence.getSatellite(2);
		this.ns[3] = gsvSentence.getSatellite(3);

		//Events
		fireGSVSentenceReceived(gsvSentence);
	}

	/**
	 * This method parse a GSV Sentence
	 * 
	 * @param nmeaSentece
	 */
	private void parseGSA(String nmeaSentence){
		gsaSentence.setSentence(nmeaSentence);
		gsaSentence.parse();
		
		mode = gsaSentence.getMode();
		modeValue = gsaSentence.getModeValue();
		SV = gsaSentence.getSV();
		PDOP = gsaSentence.getPDOP();
		HDOP = gsaSentence.getHDOP();
		VDOP = gsaSentence.getVDOP();

		//Events
		fireGSASentenceReceived(gsaSentence);
	}

	/* EVENTS*/

	/**
	 * add a listener to manage events with GPS
	 * 
	 * @param listener
	 */
	public void addListener (GPSListener listener){
		listeners.addElement(listener); 
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	public void removeListener (GPSListener listener)
	{
		listeners.removeElement(listener); 
	}

	/**
	 * Method which is used when system parse a GGA Sentence
	 * 
	 * @param ggaSentence
	 */
	private void fireGGASentenceReceived (GGASentence ggaSentence){
		GPSListener GPSL;
		for(int i=0; i<listeners.size();i++){
			try{
				GPSL = (GPSListener)listeners.elementAt(i);
				GPSL.ggaSentenceReceived(this, ggaSentence);
			}catch(Throwable t){

			}
		}
	}

	/**
	 * Method which is used when system parse a RMC Sentence
	 * 
	 * @param rmcSentence
	 */
	private void fireRMCSentenceReceived (RMCSentence rmcSentence){
		GPSListener GPSL;
		for(int i=0; i<listeners.size();i++){
			try{
				GPSL = (GPSListener)listeners.elementAt(i);
				GPSL.rmcSentenceReceived(this, rmcSentence);
			}catch(Throwable t){

			}
		}
	}

	/**
	 * Method which is used when system parse a VTG Sentence
	 * 
	 * @param VTGSentence
	 */
	private void fireVTGSentenceReceived (VTGSentence vtgSentence){
		GPSListener GPSL;
		for(int i=0; i<listeners.size();i++){
			try{
				GPSL = (GPSListener)listeners.elementAt(i);
				GPSL.vtgSentenceReceived(this, vtgSentence);
			}catch(Throwable t){

			}
		}
	}

	/**
	 * Method which is used when system parse a GSV Sentence
	 * 
	 * @param GSVSentence
	 */
	private void fireGSVSentenceReceived (GSVSentence gsvSentence){
		GPSListener GPSL;
		for(int i=0; i<listeners.size();i++){
			try{
				GPSL = (GPSListener)listeners.elementAt(i);
				GPSL.gsvSentenceReceived(this, gsvSentence);
			}catch(Throwable t){

			}
		}
	}

	/**
	 * Method which is used when system parse a GSV Sentence
	 * 
	 * @param GSVSentence
	 */
	private void fireGSASentenceReceived (GSASentence gsaSentence){
		GPSListener GPSL;
		for(int i=0; i<listeners.size();i++){
			try{
				GPSL = (GPSListener)listeners.elementAt(i);
				GPSL.gsaSentenceReceived(this, gsaSentence);
			}catch(Throwable t){

			}
		}
	}
}