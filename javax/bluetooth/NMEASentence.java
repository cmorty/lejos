package javax.bluetooth;
import java.util.Vector;

/**
 * An NMEA sentence consists of (in order) a '$' character, some 
 * header information, many data fields seperated by ',' a '*' symbol
 * at the end, and a final hexidecimal checksum number (two chars = one byte).
 * e.g. $GPGGA,140819.000,4433.2984,N,08056.3959,W,6,00,50.0,168.7,M,-36.0,M,,0000*53
 * The first two chars (GP) are the talker id
 * The next 3 characters (GGA) are the sentence id
 * @author BB
 *
 */

// TO DO:
// 1. Handle GPGGA sentences
// 2. Handle GPGLL sentences
// 3. Handle GPGSA sentences (satellite info)
// 4. Handle GPGSV sentences (detailed sat info)
// OPTION: Instead of refreshing all values at once in refreshVals()
// could get rid of setSentence() and refresh each only when called.
public class NMEASentence {

	private String sentence;
	
	private String prefix; // GP = standard NMEA sentence, others are LC, PS, HC, PG 
	private String dataType;
	private byte checksum;
	
	private Vector fields = null;
		
	public NMEASentence(String sentence) {
		setSentence(sentence);
	}
	
	public void setSentence(String sentence) {
		this.sentence = sentence;
		refreshVals();
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public boolean isValid() {
		return(getChecksum() == calcChecksum());
	}
	
	public byte getChecksum() {
		return checksum;
	}
	
	public byte calcChecksum() {
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
	
	private void refreshVals() {
		prefix = sentence.substring(1, 3); // Skip over '$'
		dataType = sentence.substring(3, 6);
		int end = sentence.indexOf('*');
		String checksumStr = sentence.substring(end + 1, end + 3);
		checksum = convertChecksum(checksumStr);
	}
	
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