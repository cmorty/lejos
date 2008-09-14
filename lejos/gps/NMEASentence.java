package lejos.gps;

import java.util.*;

/**
 * Class designed to manage all NMEA Sentence.
 * 
 * GGA and RMC Sentence needs to validate data.
 * This class has methods to validate receivedad data
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
}
