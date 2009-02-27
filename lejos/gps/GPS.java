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
	 * Returns the number of satellites being tracked to
	 * determine the coordinates. This method overwrites the superclass method
	 * and returns the number from the GSV sentence.
	 * @return Number of satellites e.g. 8
	 */
	/* TODO: Uncomment if this isn't bug
	public int getSatellitesTracked(){
		return gsvSentence.getSatellitesTracked();
	}
	*/
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
		
		int timeStamp = ggaSentence.getTime();
		
		if(timeStamp >0) {
			String rt = Integer.toString(timeStamp);
			int hh = Integer.parseInt(rt.substring(0, rt.length() - 4));
			int mm = Integer.parseInt(rt.substring(rt.length() - 4, rt.length()-2));
			int ss = Integer.parseInt(rt.substring(rt.length()-2, rt.length()));
		
			date.setHours(hh);
			date.setMinutes(mm);
			date.setSeconds(ss);
		}
	}

	/**
	 * Update Date values
	 */
	private void updateDate(){
		int dateStamp = rmcSentence.getDate();
		
		if(dateStamp > 0) {
			String rd = Integer.toString(dateStamp);
			int dd = Integer.parseInt(rd.substring(0, rd.length() - 4));
			int mm = Integer.parseInt(rd.substring(rd.length() - 4, rd.length()-2));
			int yy = Integer.parseInt(rd.substring(rd.length()-2, rd.length()));
			
			date.setDay(dd);
			date.setMonth(mm);
			date.setYear(yy);
		}
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
			// TODO: Why the try-catch block?
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