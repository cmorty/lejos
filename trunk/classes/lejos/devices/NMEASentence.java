package lejos.devices;

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
// 1. GGA sentences
// 2. GLL sentences? (none by Holux)
// 3. GSA sentences (satellite info)
// 4. GSV sentences (detailed sat info)
// 5. VTG (velocity)
// 6. RMC (time and date, etc..)
// OPTION: Instead of refreshing all values at once in refreshVals()
// could get rid of setSentence() and refresh each only when called.
public class NMEASentence {

	private String sentence;
	
	private String prefix; // GP = standard NMEA sentence, others are LC, PS, HC, PG 
	private String dataType;
	private byte checksum;
	
	private Vector fields = null;
	
	private String COMMA = ",";
	private String BLANK = "";
	
	public NMEASentence(String sentence) {
		setSentence(sentence);
	}
	
	public void setSentence(String sentence) {
		this.sentence = sentence;
		refreshVals();
	}
	
	/**
	 * Returns the prefix of the NMEA sentence. e.g. "GP" 
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Returns the data type of the NMEA sentence. e.g. "GGA" 
	 * @return
	 */
	public String getDataType() {
		return dataType;
	}
	
	public Vector getDataFields() {
		return fields;
	}
	
	/**
	 * Compares the checksum values to see if this is a corrupt sentence
	 * @return
	 */
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
	
	/**
	 * This method now checks for two things (thanks Janusz):
	 * 1. Check if final * is present.
	 * 2. Check if sentence is at least 6 chars long (req header info)
	 * Note: Currently returns if error.
	 * Unsure if returning without values will cause problems elsewhere.
	 */
	private void refreshVals() {
		
		// ** POTENTIAL BUGFIX 1:
		//if(sentence.length() < 6) return; // See if has minimum length
		prefix = sentence.substring(1, 3); // Skip over '$'
		dataType = sentence.substring(3, 6);
		int end = sentence.indexOf('*');
		// ** POTENTIAL BUGFIX 2:
		//if(end < 0) return; // -1 indicates no * in string
 
		String checksumStr = sentence.substring(end + 1, end + 3);
		
		fields = extractDataFields();
		
		checksum = convertChecksum(checksumStr);
	}
	
	private Vector extractDataFields() {
		Vector df = new Vector(15);
		int end = sentence.indexOf('*');
		// Find index of first ','
		int firstIndex = sentence.indexOf(',');
		int nextIndex = 0;
		
		do {
			// Find index of next starting past first index','
			nextIndex = sentence.indexOf(',', firstIndex+1);
			// If nextIndex = -1 then use end (*) as nextIndex
			if(nextIndex == -1) nextIndex = end;
			// Substring the string in between them
			String dataField = null;
			if(sentence.substring(firstIndex+1) == COMMA)
				dataField = BLANK;
			else
				dataField = sentence.substring(firstIndex+1, nextIndex);
			// Add to fields vector
			df.addElement(dataField);
			
			firstIndex = nextIndex;
		} while(nextIndex != end);
		
		return df;
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