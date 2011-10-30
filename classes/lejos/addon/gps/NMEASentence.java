package lejos.addon.gps;

import java.util.StringTokenizer;

/**
 * Class designed to manage all NMEA Sentence.
 * 
 * GGA and RMC Sentence needs to validate data.
 * This class has methods to validate receivedad data
 * 
 * @author Juan Antonio Brenha Moral
 * @author BB
 *
 */
abstract public class NMEASentence {

	static protected String nmeaSentence = "";
	protected StringTokenizer st;
	
	public static final int LATITUDE = 0;
	public static final int LONGITUDE = 1;

	/**
	 *  Retrieve the header constant for this sentence.
	 *  @return The NMEA header string ($GPGGA, $GPVTG, etc...)   
	 */
	// TODO: Maybe getSentenceType()?
	// TODO: Should it return the $ too, or maybe have a list of constants?
	abstract public String getHeader();
	
	/**
	 * Abstract method to parse out all relevant data from the nmeaSentence.
	 */
	abstract protected void parse(String sentence);
	
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
		if(CoordenateType == this.LATITUDE){
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
			//Longitude Management
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
	
	//Idea
	//http://rosettacode.org/wiki/Determine_if_a_string_is_numeric#Java
	public final boolean isNumeric(final String s) {
		if (s == null || s.isEmpty()) return false;
		for (int x = 0; x < s.length(); x++) {
		    final char c = s.charAt(x);
		    if (x == 0 && (c == '-')) continue;  // negative
		    if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
		    if (c == '.') continue; // float or double values
		    return false; // invalid
		}
		return true; // valid
	}

}
