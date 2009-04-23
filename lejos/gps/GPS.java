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
public class GPS extends SimpleGPS {
	
	//Classes which manages GGA, RMC, VTG, GSV, GSA Sentences
	private RMCSentence rmcSentence;
	private GSVSentence gsvSentence;
	private GSASentence gsaSentence;

	//Date Object with use GGA & RMC Sentence
	private Date date;
	
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
		// TODO: Would be more proper to return a new Date object instead of recycled Date.
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
	
	
	/* TODO: Might be worth overwriting the SimpleGPS method for lat, long, speed, course, 
	and maybe time because they can be gotten from two sources (RMC). Perhaps check if
	== -1, if so try getting it from another sentence. Also check time-stamp for both to 
	see which is more recent. */
	/* ANSWER: With Holux-1200, GGA gets values before RMC. Ignore RMC? */ 
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates. This method overwrites the superclass method
	 * and returns the number from the GSV sentence.
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked(){
		// TODO: This can be gotten from two sources. Use the one with greater time stamp.
		// (If getTimeStamp == -1, it will be less than the other one.)
		return gsvSentence.getSatellitesTracked();
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
	// TODO: This seems like an arbitrary method. Not very useful.
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
		if (token.equals(RMCSentence.HEADER)){
			rmcSentence.setSentence(s);
			notifyListeners(this.rmcSentence);
		}else if (token.equals(GSVSentence.HEADER)){
			gsvSentence.setSentence(s);
			notifyListeners(this.gsvSentence);
		}else if (token.equals(GSASentence.HEADER)){
			gsaSentence.setSentence(s);
			notifyListeners(this.gsaSentence);
		} else
			super.sentenceChooser(token, s);  // Check superclass sentences.
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
}