package lejos.gps;

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
 * @author Juan Antonio Brenha Moral
 */
public class RMCSentence extends NMEASentence{

	//RMC Sentence
	private String nmeaHeader = "";
	private float dateTimeOfFix = 0;
	private String warning = "";
	private float latitude = 0;
	private String latitudeDirection = "";
	private float longitude = 0;
	private String longitudeDirection = "";
	private float groundSpeed;//In knots
	private String courseMadeGood;
	private float dateOfFix = 0;
	private String magneticVariation = "";
	//private String magneticVariationLetter = "";

	private float speed;//In Kilometers per hour

	//Header
	public static final String HEADER = "$GPRMC";

	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Get Latitude
	 * 
	 */
	public float getLatitudeRAW(){
		return latitude;  
	}

	/**
	 * Get Longitude
	 * 
	 * @return
	 */
	public float getLongitudeRAW(){
		return longitude;
	}

	/**
	 * Get Speed in Kilometers
	 * 
	 * @return
	 */
	public float getSpeed(){
		return speed;  
	}

	/**
	 * Get date in integer format
	 * 
	 * @return
	 */
	public int getDate(){
		return Math.round(dateOfFix);
	}

	/**
	 * Returns the azimuth of the current direction of the movement. So if the gps device is still,
	 * the value will most likely random. Returns 0 if cannot be determined yet.
	 *
	 * @return The azimuth in degrees.
	 */
	public String getAzimuth() {
		return magneticVariation;
	}

	/**
	 * Return compass value from GPS
	 * 
	 * @return
	 */
	public String getCompassDegrees(){
		return courseMadeGood;
	}
	
	/**
	 * Parase a RMC Sentence
	 * 
	 * $GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62
	 */
	public void parse (){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(nmeaSentence,",");

		try{
			nmeaHeader = (String)st.nextToken();//$GPRMC
			dateTimeOfFix = Float.parseFloat((String)st.nextToken());
			warning = (String)st.nextToken();
			latitude = Float.parseFloat((String)(st.nextToken()));
			//latitude = degreesMinToDegrees(st.nextToken(),0);
			latitudeDirection = (String)st.nextToken();
			longitude = Float.parseFloat((String)st.nextToken());
			//longitude = degreesMinToDegrees(st.nextToken(),1);
			longitudeDirection = (String)st.nextToken();
			String s = st.nextToken();
			groundSpeed = s.equals("") ? 0 : Float.parseFloat(s);
			courseMadeGood = st.nextToken();
			dateOfFix = Float.parseFloat((String)st.nextToken());
			magneticVariation = st.nextToken();//Float.parseFloat((String)st.nextToken());
			//magneticVariationLetter = (String)st.nextToken();
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

		//Speed
		if (groundSpeed > 0) {
			// km/h = knots * 1.852
			speed = (float) ((groundSpeed) * 1.852);
		}
		// A negative speed doesn't make sense.
		if (speed < 0) {
			speed = 0;
		}

	}//End Parse

}//End Class
