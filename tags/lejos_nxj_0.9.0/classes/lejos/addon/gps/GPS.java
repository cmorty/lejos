package lejos.addon.gps;

import java.io.*;
import java.util.*;

/**
 * This class manages data received from a GPS Device.
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
 * DEVELOPER NOTES: More NMEA sentence types that can be added if there is demand for them:
 * http://www.gpsinformation.org/dale/nmea.htm
 */
public class GPS extends SimpleGPS {
	
	//Classes which manages GGA, RMC, VTG, GSV, GSA Sentences
	private RMCSentence rmcSentence;
	private GSVSentence gsvSentence;
	private int gsvSentenceNumber = -1;
	private int gsvSentenceTotal = -1;
	
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
	 * Get NMEA Satellite. The satellite list is retrieved from the almanac data. Satellites are
	 * ordered by their elevation: highest elevation (index 0) -> lowest elevation.
	 * 
	 * @param index the satellite index
	 * @return the NMEASaltellite object for the selected satellite
	 */
	public Satellite getSatellite(int index){
		Satellite s = gsvSentence.getSatellite(index); 
		// Compare getPRN() with this satellite, fill in setTracked():
		// TODO: This fails because most satellites are set to 0 when this is called. Not synced yet.
		boolean tracked = false;
		int [] prns = getPRN();
		for(int i=0;i<prns.length;i++) {
			if(prns[i] == s.getPRN()) {
				tracked=true;
				break;
			}
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
	 * The satellites in view is a list of satellites the GPS could theoretically connect to (i.e. satellites that 
	 * are not over the earth's horizon). The getSatellitesInView() method will always return an equal or greater
	 * number than getSatellitesTracked().
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesInView(){
		return gsvSentence.getSatellitesInView();
	}
	
		
	/**
	 * Internal helper method to aid in the subclass architecture. Overwrites the superclass
	 * method and calls it internally.
	 * 
	 * @param header
	 * @param s
	 */
	protected void sentenceChooser(String header, String s) {
		if (header.equals(RMCSentence.HEADER)){
			rmcSentence.setSentence(s);
			notifyListeners(this.rmcSentence);
		}else if (header.equals(GSVSentence.HEADER)){
			// TODO: I wonder what happens if say 2 of 4 are received and parse is called?
			// Because 2 would be new data, 2 would be old data.
			// Can't happen because parse() only called when not null.
			// BUT what if it is in the middle of parsing and new data comes in? 
			// Solution: Sync GSVSentence.parse()? checkRefresh() is already synced though.
			
			// 0. Get StringTokenizer to read info from NMEASentence:
			StringTokenizer st = new StringTokenizer(s,",");
			st.nextToken(); // Skip header $GPGSV
			// 1.1 Find out how many sentences in sequence.
			gsvSentenceTotal = Integer.parseInt(st.nextToken());
			// 1.2 Find out which sentence this is.
			gsvSentenceNumber = Integer.parseInt(st.nextToken());
			// 2. Assign sentence to GSVSentence in order.
			gsvSentence.setSentence(s, gsvSentenceNumber, gsvSentenceTotal);
			// 3. If last sentence:
			if(gsvSentenceTotal == gsvSentenceNumber) {
				// 3a. setSentence() to last one so it is not null
				gsvSentence.setSentence(s);
				// 3b. Notify GPSListener
				notifyListeners(this.gsvSentence);
			}
		} else
			super.sentenceChooser(header, s);  // Check superclass sentences.
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