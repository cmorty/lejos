package lejos.addon.gps;


import java.util.*;

/**
 * RMC is a Class designed to manage RMC Sentences from a NMEA GPS Receiver
 * 
 * RMC - NMEA has its own version of essential gps pvt (position, velocity, time) data. It is called RMC, The Recommended Minimum, which will look similar to:
 * 
 * $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
 * 
 * Where:
 *      RMC          Recommended Minimum sentence C
 *      123519       Fix taken at 12:35:19 UTC
 *      A            Status A=active or V=Void.
 *      4807.038,N   Latitude 48 deg 07.038' N
 *      01131.000,E  Longitude 11 deg 31.000' E
 *      022.4        Speed over the ground in knots
 *      084.4        Track angle in degrees True
 *      230394       Date - 23rd of March 1994
 *      003.1,W      Magnetic Variation
 *      *6A          The checksum data, always begins with *
 * 
 * @author Juan Antonio Brenha Moral (major recoding by BB)
 */
public class RMCSentence extends NMEASentence{

	//RMC Sentence
	// TODO: Convert all/most of these floats to int
	private int dateTimeOfFix = -1;
	private String warning = "";
	private double latitude = 0;
	private String latitudeDirection = ""; // TODO Make char
	private double longitude = 0;
	private String longitudeDirection = ""; // TODO Make char
	private float groundSpeed;//In knots
	private String courseMadeGood = null;
	private int dateOfFix = -1;
	private String magneticVariation = null;
	//private String magneticVariationLetter = "";

	private float speed;//In Kilometers per hour

	//Header
	public static final String HEADER = "$GPRMC";

	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Returns the NMEA header for this sentence.
	 */
	public String getHeader() {
		return HEADER;
	}
		
	/**
	 * Get Latitude
	 * 
	 */
	// TODO: Why is this specified as RAW?
	public double getLatitudeRAW(){
		checkRefresh();
		return latitude;  
	}

	/**
	 * Get Longitude
	 * 
	 * @return the raw longitude
	 */
	// TODO: Why is this specified as RAW?
	public double getLongitudeRAW(){
		checkRefresh();
		return longitude;
	}

	/**
	 * Get Speed in Kilometers
	 * 
	 * @return the speed in kilometers per ???
	 */
	public float getSpeed(){
		checkRefresh();
		return speed;
	}

	/**
	 * Get date in integer format
	 * 
	 * @return the date in integer format
	 */
	public int getDate(){
		checkRefresh();
		return dateOfFix;
	}

	/**
	 * Return compass value from GPS
	 * 
	 * @return the compass value in degrees. -1 means it hasn't been obtained yet.
	 */
	public float getCompassDegrees(){
		checkRefresh();
		float compassDegrees = -1;
		if(courseMadeGood != null){
			compassDegrees = Float.parseFloat(courseMadeGood);
		}
		return compassDegrees;
	}
	
	public String getMagneticVariation() {
		// TODO: Parse data. Should return float, -ve for West, +ve for East. See parse()
		return magneticVariation;
	}
	
	/**
	 * Parse RMC Sentence
	 * 
	 * $GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62
	 */
	protected void parse (String sentence){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(sentence,",");

		try{
			st.nextToken(); // skip header $GPRMC
			// TODO: Maybe leave this as a float for greater accuracy?
			dateTimeOfFix = (int)Float.parseFloat(st.nextToken());
			warning = st.nextToken();
			//latitude = Float.parseFloat(st.nextToken());
			latitude = degreesMinToDegrees(st.nextToken());
			latitudeDirection = st.nextToken();
			//longitude = Float.parseFloat(st.nextToken());
			longitude = degreesMinToDegrees(st.nextToken());
			longitudeDirection = st.nextToken();
			String s = st.nextToken();
			groundSpeed = s.equals("") ? 0 : Float.parseFloat(s);
			courseMadeGood = st.nextToken();
			dateOfFix = Integer.parseInt(st.nextToken());
			magneticVariation = st.nextToken();//Float.parseFloat((String)st.nextToken());
			//magneticVariationLetter = (String)st.nextToken();
		}catch(NoSuchElementException e){
			System.err.println("Threw a NoSuch exception");
		}catch(NumberFormatException e){
			System.err.println("Threw a NumFormat exception");
		}
		
		//Improve quality data
		if (!longitudeDirection.equals("E")) {
			longitude = -longitude;
		}
		if (!latitudeDirection.equals("N")) {
			latitude = -latitude;
		}

		//Speed
		if (groundSpeed > 0) {
			// km/h = knots * 1.852
			speed = (float) ((groundSpeed) * 1.852);
		}
		// A negative speed doesn't make sense.
		// TODO: This seems iffy. Why set it arbitrarily to zero?
		if (speed < 0) {
			speed = 0;
		}

	}//End Parse

}//End Class
