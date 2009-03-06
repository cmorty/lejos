package lejos.gps;

import java.util.*;

/**
 * Class designed to manage all NMEA Sentence.
 * 
 * GGA and RMC Sentence needs to validate data.
 * This class has methods to validate received data
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 */
abstract public class NMEASentence {
	
	static byte checksum;
	protected String nmeaSentence = null;
	protected StringTokenizer st;
	private long timeStamp = -1;
	
	/* GETTERS & SETTERS */
	
	/**
	 * Retrieve the header constant for this sentence.
	 * TODO: Maybe getSentenceType()?
	 * TODO: Should it return the $ too, or maybe have a list of constants?
	 */
	//abstract public String getHeader();
	
	/**
	 * Set a new nmea sentence into the object
	 * 
	 * @param sentence
	 */
	public void setSentence(String sentence){
		this.timeStamp = System.currentTimeMillis();
		nmeaSentence = sentence;
	}

	/**
	 * This method returns the system time at which the data for the NMEA Sentence 
	 * was collected. It uses System.currentTimeMillis() to create the time stamp.
	 * Note: It might seem strange not to use the satellite time to time-stamp the
	 * data, but in fact the javax.microedition.location API calls for the system time.
	 * @return system time when the data was collected
	 */
	public long getTimeStamp() {
		// TODO leJOS returns an int internally, so this is limited to about 24 days worth
		// of continuous operation. If a program runs longer than this the timestamp is invalid.
		return timeStamp;
	}
	
	/**
	 *  This method is called by all the getter methods. It checks if a new sentence has 
	 *  been received since the last call. 
	 *  It sets nmeaSentence to null to act as flag for when method called again.
	 */
	protected void checkRefresh() {
		if(nmeaSentence != null) {
			parse();
			nmeaSentence = null; // Once data is parsed, discard string (used as flag)
		}
	}
	
	/**
	 * Abstract method to parse out all relevant data from the nmeaSentence.
	 */
	abstract protected void parse();
	
	/* CHECKSUM METHODS */
	
	/**
	 * Return if your NMEA Sentence is valid or not
	 *
	 * @param sentence the NMEA sentence
	 * @return true iff the NMEA Sentence is true
	 */
	public static boolean isValid(String sentence){
		int end = sentence.indexOf('*');
		String checksumStr = sentence.substring(end + 1, end + 3);
		byte checksumByte = convertChecksum(checksumStr);
		return(checksumByte == calcChecksum(sentence));
	}
	
	/**
	 * Method designed to calculate a checksum using data
	 * 
	 * @return
	 */
	private static byte calcChecksum(String sentence) {
		int start = sentence.indexOf('$');
		int end = sentence.indexOf('*');
		if(end < 0) {
			end = sentence.length();
		}
		byte checksum = (byte) sentence.charAt(start + 1);
		for (int index = start + 2; index < end; ++index) {
			checksum ^= (byte) sentence.charAt(index);
		}
		return checksum;
	}

	/**
	 * Method used to create a checksum with String
	 * 
	 * @param checksum_string
	 * @return
	 */
	private static byte convertChecksum(String checksum_string) {
		byte checksum;
		checksum = (byte)((hexCharToByte(checksum_string.charAt(0)) & 0xF ) << 4 );
		checksum = (byte)(checksum | hexCharToByte(checksum_string.charAt(1)) & 0xF );
		return checksum;
	}
	
	/**
	 * NOTE: This functionality can be replaced by Byte.parseByte()
	 * if we ever make a Byte class.
	 * @param hex_char
	 * @return
	 */
	private static byte hexCharToByte(char hex_char) {
	  if( hex_char > 57 )
		  return((byte)(hex_char - 55));
	  else
		  return((byte)(hex_char - 48));
	}

	/* UTILITIES METHODS */

	/**
	 * Any GPS Receiver gives Lat/Lon data in the following way:
	 * 
	 * http://www.gpsinformation.org/dale/nmea.htm
	 * http://www.teletype.com/pages/support/Documentation/RMC_log_info.htm
	 * 
	 * 4807.038,N   Latitude 48 deg 07.038' N
	 * 01131.000,E  Longitude 11 deg 31.000' E
	 * 
	 * This data is necessary to convert to Decimal Degrees.
	 * 
	 * Latitude values has the range: -90 <-> 90
	 * Longitude values has the range: -180 <-> 180
	 * 
	 * @param DD_MM the day and month
	 * @param CoordinateType the coordinate type
	 * @return the decimal degrees
	 */
	protected float degreesMinToDegrees(String DD_MM) {
		float decDegrees = 0;
		float degrees = 0;
		float minutes = 0;
		float seconds = 0;
		String doubleCharacterSeparator = ".";
		String DDMM;
		//1. Count characters until character '.'
		int dotPosition = DD_MM.indexOf(doubleCharacterSeparator);
		DDMM = DD_MM.substring(0, dotPosition);
		 	
		//Longitude and Latitude Management
		degrees = Float.parseFloat(DDMM.substring(0, DDMM.length() - 2));
		minutes = Float.parseFloat(DD_MM.substring(DDMM.length() - 2));
		
		/* TODO: I don't think we need this. I removed CoordinateType:
		if(CoordinateType == 0){
			if((degrees >=0) && (degrees <=90)){
				throw new NumberFormatException();
			}
		}else{
			if((degrees >=0) && (degrees <=180)){
				throw new NumberFormatException();
			}
		}*/
		
		//Idea
		//http://id.mind.net/%7Ezona/mmts/trigonometryRealms/degMinSec/degMinSec.htm
		decDegrees = (float)(degrees + (minutes * (1.0 / 60.0)) + (seconds * (1.0 / 3600.0)));
		
		return decDegrees;
	}
}
