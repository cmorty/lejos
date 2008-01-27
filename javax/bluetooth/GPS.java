package javax.bluetooth;

import java.io.*;

import lejos.nxt.LCD; // !! DELETE

/**
 * Class to pull data from a GPS receiver
 * @author BB
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
	/**
	 * 
	 * @param in An input stream from the GPS receiver
	 */
	public GPS(InputStream in) {
		this.in = in;
		this.setDaemon(true); // Must be set before thread starts
		this.start();
	}
	
	public String getLatitude() {
		return "lat";
	}
	
	public String getLongitude() {
		return "long";
	}
	
	public String getAltitude() {
		return "alt";
	}
	
	public int getTime() {
		return 0;
	}
	
	/**
	 * Placeholder idea: returns heading (from North) based on
	 * previous lat/long reading.
	 * @return
	 */
	public int getHeading() {
		return -1;
	}
	
	/**
	 * Placeholder idea: returns speed based on previous lat/long reading.
	 * @return
	 */
	public int getSpeed() {
		return -1;
	}
	
	public void addGPSListener() {
		/* Placeholder method */
	}
	
	/**
	 * Placeholder Idea: Set a latitude/longitude as origin, then
	 * it will return x, y coordinates (in CM or Inches).
	 * By default, uses first reading as origin.
	 * Need to make methods for getX() and getY(), setUnits()
	 * @param longitude
	 * @param latitude
	 */
	public void setOriginPoint(String longitude, String latitude) {
		
	}
	
	// Debugging variables (DELETE WHEN DONE)
	int sentenceCount = 0;
	
	/**
	 * Keeps reading sentences from GPS receiver stream and extracting data.
	 * This is a daemon thread so when program ends it won't keep running.
	 */
	public void run() {
		/* Code holder for parsing values */
		while(true) {
			String s = getNextString();

			// Make NMEASentence
			NMEASentence sen = new NMEASentence(s);
			
			LCD.clear();
			LCD.drawInt(sentenceCount, 0, 0); // DELETE
			LCD.drawString(s, 0, 1);
			LCD.drawString(sen.getPrefix(), 0, 2);
			LCD.drawString(sen.getDataType(), 0, 3);
			LCD.drawInt(sen.getChecksum(), 0, 4);
			LCD.drawInt(sen.calcChecksum(), 0, 5);
			
			LCD.refresh();
			
			
			// Check if valid (discard if it is invalid)
			// Check if contains lat/long data
			// If so, pull that data and update all global vars
			// Notify appropriate listeners if data changed
		}
	}
	
	/**
	 * Pulls the next NMEA sentence as a string
	 * @return NMEA string, including $ and end checksum 
	 */
	private String getNextString() {
		boolean done = false;
		do {
			// Read in buf length of sentence
			try {
				in.read(segment);
			} catch (IOException e) {
				// How to handle error?
			}
			// Append char[] data into currentSentence
			for(int i=0;i<BUFF;i++)
				currentSentence.append((char)segment[i]);
			
			// Search for $ symbol (indicates start of new sentence)
			if(currentSentence.indexOf(START_CHAR, 1) >= 0) {
				done = true;
				sentenceCount++; // DELETE
			}
			
		} while(!done);
		
		int endIndex = currentSentence.indexOf(START_CHAR, 1);
		String sentence = currentSentence.substring(0, endIndex);
		
		// Crop out current sentence
		currentSentence.delete(0, endIndex);
		
		return sentence;
	}
	
	/**
	 * Accepts a $GPGGA sentence and rips out lat, long, alt, time
	 * and updates the global variables for each, then notifies all
	 * listeners.
	 *
	 */
	private void ripGPGGA_Sentence(StringBuffer sentence) {
		
	}
	
	/* GPS SAMPLE OUTPUT
	$GPGSA,A,2,,,,,,,,,,,,,50.0,50.0,50.0*06
	$GPRMC,140817.000,V,4433.2983,N,08056.3970,W,1.42,77.42,020707,,,E*51
	$GPGGA,140818.000,4433.2984,N,08056.3964,W,6,00,50.0,168.7,M,-36.0,M,,0000*5C
	$GPGSA,A,2,,,,,,,,,,,,,50.0,50.0,50.0*06
	$GPRMC,140818.000,V,4433.2984,N,08056.3964,W,1.42,77.42,020707,,,E*5C
	$GPGGA,140819.000,4433.2984,N,08056.3959,W,6,00,50.0,168.7,M,-36.0,M,,0000*53
	$GPGSA,A,2,,,,,,,,,,,,,50.0,50.0,50.0*06
	$GPGSV,3,1,10,18,71,320,30,21,62,184,32,09,46,130,,22,36,290,20*79
	$GPGSV,3,2,10,24,29,154,,26,28,050,22,29,18,052,,03,13,300,*78
	$GPGSV,3,3,10,14,12,233,,19,07,327,*75
	$GPRMC,140819.000,V,4433.2984,N,08056.3959,W,1.42,77.42,020707,,,E*53
	$GPGGA,140820.000,4433.2985,N,08056.3954,W,6,00,50.0,168.7,M,-36.0,M,,0000*55
	$GPGSA,A,2,,,,,,,,,,,,,50.0,50.0,50.0*06
	$GPRMC,140820.000,V,4433.2985,N,08056.3954,W,1.42,77.42,020707,,,E*55
	$GPGGA,140821.000,4433.2985,N,08056.3948,W,6,00,50.0,168.7,M,-36.0,M,,0000*59
	$GPGSA,A,2,,,,,,,,,,,,,50.0,50.0,50.0*06
	*/
	
}