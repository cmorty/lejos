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
	
	//GSA
	private int[] SV; // TODO: The hell is this?
	
	//Classes which manages GGA, RMC, VTG, GSV, GSA Sentences
	private RMCSentence rmcSentence;
	private GSVSentence gsvSentence;
	private GSASentence gsaSentence;

	//Date Object with use GGA & RMC Sentence
	private Date date;
	
	// Use Vector to keep compatibility with J2ME
	private Vector listeners = new Vector();

	public static final int MINIMUM_SATELLITES_TO_WORK = 4;
	public static final int MAXIMUM_SATELLITES_TO_WORK = 12;
	
	/**
	 * The constructor. It needs an InputStream
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public GPS(InputStream in) {
		super(in);
		rmcSentence = new RMCSentence();
		gsvSentence = new GSVSentence();
		gsaSentence = new GSASentence();
		SV = new int[12];
		
		date = new Date();
	}
	

	/* GETTERS & SETTERS */

	/**
	 * Return Compass Degrees
	 * in a range: 0.0-359.9
	 * 
	 * @return the compass degrees
	 */
	public float getCompassDegrees(){
		return rmcSentence.getCompassDegrees();	
	}
	
	/**
	 * Return a Date Object with data from GGA and RMC NMEA Sentence
	 * 
	 * @return the date
	 */
	public Date getDate(){
		updateDate();
		updateTime();
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
		return gsvSentence.getSatellite(index);
	}
	
	/**
	 * Get Mode1
	 * 
	 * @return mode1
	 */
	public String getMode(){
		return gsaSentence.getMode();
	}

	/**
	 * Get Mode2
	 * 
	 * @return mode2
	 */
	public int getModeValue(){
		return gsaSentence.getModeValue();
	}
	
	/**
	 * Get an Array with Satellite ID
	 * 
	 * @return array of satellite IDs
	 */
	public int[] getSV(){
		return gsaSentence.getSV();
	}
	
	/**
	 * Get PDOP
	 * 
	 * @return the PDOP
	 */
	public float getPDOP(){
		return gsaSentence.getPDOP();
	}

	/**
	 * Get HDOP
	 * 
	 * @return the HDOP
	 */
	public float getHDOP(){
		return gsaSentence.getHDOP();
	}

	/**
	 * Get VDOP
	 * 
	 * @return the VDOP
	 */
	public float getVDOP(){
		return gsaSentence.getVDOP();
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
			(ggaSentence.getSatellitesTracked() >= MINIMUM_SATELLITES_TO_WORK) && 
			//(mode.equals("A")) &&
			(getModeValue() == 3)){
			
			status = true;
		}
		return status;
	}
	
	
	/**
	 * Internal helper method to aid in the subclass architecture. Overwrites the superclass
	 * method and calls it internally.
	 * 
	 * @param token
	 * @param s
	 */
	protected void sentenceChooser(String token, String s) {
		//super.sentenceChooser(token, s); // Fires listener here
		if (token.equals(GGASentence.HEADER)){
			ggaSentence.setSentence(s);
			fireGGASentenceReceived(ggaSentence);
		}else if (token.equals(VTGSentence.HEADER)){
			vtgSentence.setSentence(s);
			fireVTGSentenceReceived(vtgSentence);
		}else if (token.equals(RMCSentence.HEADER)){
			rmcSentence.setSentence(s);
			fireRMCSentenceReceived(rmcSentence);
		}else if (token.equals(GSVSentence.HEADER)){
			gsvSentence.setSentence(s);
			fireGSVSentenceReceived(gsvSentence);
		}else if (token.equals(GSASentence.HEADER)){
			gsaSentence.setSentence(s);
			fireGSASentenceReceived(gsaSentence);
		}
	}
	
	/* NMEA */

	/**
	 * Update Time values
	 */
	private void updateTime(){
		String rt = Integer.toString(ggaSentence.getTime());
		int hh;
		int mm;
		int ss;

		// TODO: More redundant coding here. This can be minimized in half
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
	 * Update Date values
	 */
	private void updateDate(){
		String rd = Integer.toString(rmcSentence.getDate());
		int yy;
		int mm;
		int dd;

		// TODO: More bloated code. Easily reduced in half
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