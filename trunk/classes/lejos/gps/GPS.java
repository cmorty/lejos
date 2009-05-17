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
/*
 * DEVELOPER NOTES: More NMEA sentence types that can be added:
 * http://www.gpsinformation.org/dale/nmea.htm
 */
public class GPS extends SimpleGPS {
	
	//Classes which manages GGA, RMC, VTG, GSV, GSA Sentences
	private RMCSentence rmcSentence;
	private GSVSentence gsvSentence;
	private GSASentence gsaSentence;

	//Date Object with use GGA & RMC Sentence
	private Date date;
	
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
	public Satellite getSatellite(int index){
		Satellite s = gsvSentence.getSatellite(index); 
		// Compare getPRN() with this satellite, fill in setTracked():
		boolean tracked = false;
		int [] prns = getPRN();
		for(int i=0;i<prns.length;i++) {
			if(prns[i] == s.getPRN()) tracked=true;
			break;
		}
		s.setTracked(tracked);
		return s;
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
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked(){
		return ggaSentence.getSatellitesTracked();
	}
	
	/**
	 * The satellites in view is a list of satellites the GPS could theoretically connect to. These satellites
	 * are retrieved from the almanac data. The getSatellitesInView() method will always return an equal or greater
	 * number than getSatellitesTracked().
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesInView(){
		return gsvSentence.getSatellitesInView();
	}
	
	/**
	 * Selection type of 2D or 3D fix 
	 * <li> 'M' = manual
	 * <li> 'A' = automatic 
	 * @return selection type - either 'A' or 'M'
	 */
	public String getSelectionType(){
		return gsaSentence.getMode();
	}

	/**
	 *  3D fix - values include:
	 *  <li>1 = no fix
	 *  <li>2 = 2D fix
	 *  <li>3 = 3D fix
	 * 
	 * @return fix type (1 to 3)
	 */
	public int getFixType(){
		return gsaSentence.getModeValue();
	}
	
	/**
	 * Get an Array of Pseudo-Random Noise codes (PRN). You can look up a list of GPS satellites by 
	 * this number at: http://en.wikipedia.org/wiki/List_of_GPS_satellite_launches
	 * Note: This number might be similar or identical to SVN. 
	 * 
	 * @return array of PRNs
	 */
	public int[] getPRN(){
		return gsaSentence.getPRN();
	}
	
	/**
	 * Get the 3D Position Dilution of Precision (PDOP). When visible GPS satellites are close
	 * together in the sky, the geometry is said to be weak and the DOP value is high; when far
	 * apart, the geometry is strong and the DOP value is low. Thus a low DOP value represents
	 * a better GPS positional accuracy due to the wider angular separation between the 
	 * satellites used to calculate a GPS unit's position. Other factors that can increase 
	 * the effective DOP are obstructions such as nearby mountains or buildings.
	 * 
	 * @return The PDOP (PDOP * 6 meters = the error to expect in meters) -1 means PDOP is unavailable from the GPS.
	 */
	public float getPDOP(){
		return gsaSentence.getPDOP();
	}

	/**
	 * Get the Horizontal Dilution of Precision (HDOP). When visible GPS satellites are close
	 * together in the sky, the geometry is said to be weak and the DOP value is high; when far
	 * apart, the geometry is strong and the DOP value is low. Thus a low DOP value represents
	 * a better GPS positional accuracy due to the wider angular separation between the 
	 * satellites used to calculate a GPS unit's position. Other factors that can increase 
	 * the effective DOP are obstructions such as nearby mountains or buildings.
	 * 
	 * @return the HDOP (HDOP * 6 meters = the error to expect in meters) -1 means HDOP is unavailable from the GPS.
	 */
	public float getHDOP(){
		return gsaSentence.getHDOP();
	}

	/**
	 * Get the Vertical Dilution of Precision (VDOP). When visible GPS satellites are close
	 * together in the sky, the geometry is said to be weak and the DOP value is high; when far
	 * apart, the geometry is strong and the DOP value is low. Thus a low DOP value represents
	 * a better GPS positional accuracy due to the wider angular separation between the 
	 * satellites used to calculate a GPS unit's position. Other factors that can increase 
	 * the effective DOP are obstructions such as nearby mountains or buildings.
	 * 
	 * @return the VDOP (VDOP * 6 meters = the error to expect in meters) -1 means VDOP is unavailable from the GPS.
	 */
	public float getVDOP(){
		return gsaSentence.getVDOP();
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