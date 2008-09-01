package lejos.devices;

import java.util.*;

/**
 * Class designed to manage all NMEA Sentence.
 * 
 * GGA and RMC Sentence needs to validate data.
 * This class has methods to validate received data
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 *
 */
public class NMEASentence {

	static byte checksum;
	static protected String nmeaSentence = "";
	protected StringTokenizer st;

	/* GETTERS & SETTERS */
	
	/**
	 * Set a new nmea sentence into the object
	 * 
	 * @param sentence
	 */
	public void setSentence(String sentence){
		nmeaSentence = sentence;
	}

	private static byte getChecksum() {
		return checksum;
	}

	/* CHECKSUM METHODS */
	
	/**
	 * Return if your NMEA Sentence is valid or not
	 * 
	 * @return
	 */
	static public boolean isValid(){
		int end = nmeaSentence.indexOf('*');
		String checksumStr = nmeaSentence.substring(end + 1, end + 3);
		checksum = convertChecksum(checksumStr);
		return(getChecksum() == calcChecksum());
	}

	/**
	 * Return if your NMEA Sentence is valid or not
	 * 
	 * @param sentence
	 * @return
	 */
	static public boolean isValid(String sentence){
		nmeaSentence = sentence;
		int end = nmeaSentence.indexOf('*');
		String checksumStr = nmeaSentence.substring(end + 1, end + 3);
		checksum = convertChecksum(checksumStr);
		return(getChecksum() == calcChecksum());
	}
	

	/**
	 * Method designed to calculate a checksum using data
	 * 
	 * @return
	 */
	private static byte calcChecksum() {
		int start = nmeaSentence.indexOf('$');
		int end = nmeaSentence.indexOf('*');
		if(end < 0) {
			end = nmeaSentence.length();
		}
		byte checksum = (byte) nmeaSentence.charAt(start + 1);
		for (int index = start + 2; index < end; ++index) {
			checksum ^= (byte) nmeaSentence.charAt(index);
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
	 * @param DD_MM
	 * @param CoordenateType
	 * @return
	 */
	protected float degreesMinToDegrees(String DD_MM,int CoordenateType) {//throws NumberFormatException
		float decDegrees = 0;
		float degrees = 0;
		float minutes = 0;
		float seconds = 0;
		String doubleCharacterSeparator = ".";
		String DDMM;

		//1. Count characters until character '.'
		int dotPosition = DD_MM.indexOf(doubleCharacterSeparator);
		if(CoordenateType == 0){
			//Latitude Management
			DDMM = DD_MM.substring(0, dotPosition);
			if(DDMM.length() == 4){
				degrees = Float.parseFloat(DDMM.substring(0, 2));
				minutes = Float.parseFloat(DD_MM.substring(2));
			}else if(DDMM.length() == 3){
				degrees = Float.parseFloat(DDMM.substring(0, 1));
				minutes = Float.parseFloat(DD_MM.substring(1));
			}

			if((degrees >=0) && (degrees <=90)){
				//throw new NumberFormatException();
			}
		}else{
			//Latitude Management
			DDMM = DD_MM.substring(0, dotPosition);
			if(DDMM.length() == 5){
				degrees = Float.parseFloat(DDMM.substring(0, 3));
				minutes = Float.parseFloat(DD_MM.substring(3));
			}else if(DDMM.length() == 4){
				degrees = Float.parseFloat(DDMM.substring(0, 2));
				minutes = Float.parseFloat(DD_MM.substring(2));
			}else if(DDMM.length() == 3){
				degrees = Float.parseFloat(DDMM.substring(0, 1));
				minutes = Float.parseFloat(DD_MM.substring(1));
			}

			if((degrees >=0) && (degrees <=180)){
				//throw new NumberFormatException();
			}
		}
		//Idea
		//http://id.mind.net/%7Ezona/mmts/trigonometryRealms/degMinSec/degMinSec.htm
		decDegrees = (float)(degrees + (minutes * (1.0 / 60.0)) + (seconds * (1.0 / 3600.0)));
		
		return decDegrees;
	}
}
