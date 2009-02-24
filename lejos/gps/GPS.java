package lejos.gps;

import java.io.*;
import java.util.*;

/**
 * This class manages a data received from a GPS Device.
 * GPS Class manages the following NMEA Sentences:
 *  
 * GPRMC
 * GPGSV
 * GPGSA
 * GPGGA (superclass)
 * GPVTG (superclass)
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 *
 */
public class GPS extends BasicGPS {

	//RMC
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
	private RMCSentence rmcSentence;
	private GSVSentence gsvSentence;
	private GSASentence gsaSentence;

	//Date Object with use GGA & RMC Sentence
	private Date date;

	//Security
	private boolean internalError = false;
	
	// Use Vector to keep compatibility with J2ME
	private Vector listeners = new Vector();

	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public GPS(InputStream in) {
		super(in);
		rmcSentence = new RMCSentence();
		gsvSentence = new GSVSentence();
		ns = new NMEASatellite[4];
		gsaSentence = new GSASentence();
		SV = new int[12];
		
		date = new Date();
	}
	

	/* GETTERS & SETTERS */

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
	
	// TODO Is this really needed? In Java exceptions are used for error reporting.
	public boolean existInternalError(){
		return internalError;
	}

	/**
	 * Internal helper method to aid in the subclass architecture. Overwrites the superclass
	 * method and calls it internally.
	 * 
	 * @param token
	 * @param s
	 */
	protected void sentenceChooser(String token, String s) {
		super.sentenceChooser(token, s);
		if (token.equals(RMCSentence.HEADER)){
			parseRMC(s);
		}else if (token.equals(GSVSentence.HEADER)){
			parseGSV(s);
		}else if (token.equals(GSASentence.HEADER)){
			parseGSA(s);
		}
	}
	
	/* NMEA */

	/**
	 * This method parse a GGA Sentence. Overwirtes superclass version.
	 * 
	 * @param nmeaSentece
	 */
	protected void parseGGA(String nmeaSentence){

		super.parseGGA(nmeaSentence);
		updateTime(); // TODO: Only update when method is called?
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
		
		updateDate(); // TODO Only update when getDate() method called?

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
	protected void parseVTG(String nmeaSentence){
		super.parseVTG(nmeaSentence);
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