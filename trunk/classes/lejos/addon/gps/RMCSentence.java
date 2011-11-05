package lejos.addon.gps;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

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
 * 
 */
public class RMCSentence extends NMEASentence{

	//RMC Sentence
	private String nmeaHeader = "";
	private int dateTimeOfFix = 0;
	private final int DATETIMELENGTH = 6;
	private String status = "";
	private final String ACTIVE = "A";
	private final String VOID = "V";
	private float latitude = 0;
	private String latitudeDirection = "";
	private float longitude = 0;
	private String longitudeDirection = "";
	private final float KNOT = 1.852f;
	private float groundSpeed;//In knots
	private int compassDegrees;
	private int dateOfFix = 0;
	private float magneticVariation = 0f;
	private String magneticVariationLetter = "";

	private float speed;//In Kilometers per hour

	//Header
	public static final String HEADER = "$GPRMC";

	//NMEA parts
	private String part1,part2,part3,part4,part5,part6,part7,part8,part9,part10,part11,part12 = "";

	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Returns the NMEA header for this sentence.
	 */
	@Override
	public String getHeader() {
		return HEADER;
	}
	
	public String getStatus(){
		return status;
	}
	
	/**
	 * Get Latitude
	 * 
	 */
	public float getLatitude(){
		return latitude;  
	}

	/**
	 * Get Longitude
	 * 
	 * @return
	 */
	public float getLongitude(){
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
	public int getTime(){
		return dateTimeOfFix;
	}
	
	public int getDate(){
		return dateOfFix;
	}

	/**
	 * Return compass value from GPS
	 * 
	 * @return
	 */
	public int getCompassDegrees(){
		return compassDegrees;
	}
	
	/**
	 * Parase a RMC Sentence
	 * 
	 * $GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62
	 */
	public void parse (String sentence){
		
		st = new StringTokenizer(sentence,",");
	
		try{
			
			//Extracting data from a GGA Sentence
			
			part1 = st.nextToken();//NMEA header
			part2 = st.nextToken();//Fix taken at 12:35:19 UTC
			part3 = st.nextToken();//Status A=active or V=Void.
			part4 = st.nextToken();//Latitude 48 deg 07.038' N
			part5 = st.nextToken();//Latitude Direction
			part6 = st.nextToken();//Longitude 11 deg 31.000' E
			part7 = st.nextToken();//Longitude Direction
			part8 = st.nextToken();//Speed over the ground in knots
			part9 = st.nextToken();//Track angle in degrees True
			part10 = st.nextToken();//Date - 23rd of March 1994
			part11 = st.nextToken();//Magnetic Variation
			part12 = st.nextToken();//Magnetic Variation Letter
			
			st = null;
			
			//Processing RMC data
			
			nmeaHeader = part1;//$GPRMC
		
			if(part2.length() == 0){
				dateTimeOfFix = 0;
			}else{
				dateTimeOfFix = Math.round(Float.parseFloat(part2));
			}
			
			if(part3.equals(ACTIVE)){
				status = ACTIVE;
			}else{
				status = VOID;
			}
			
			if(isNumeric(part4)){
				latitude = degreesMinToDegrees(part4,NMEASentence.LATITUDE);
			}else{
				latitude = 0f;
			}
			
			latitudeDirection = part5;
			
			if(isNumeric(part6)){
				longitude = degreesMinToDegrees(part6,NMEASentence.LONGITUDE);
			}else{
				longitude = 0f;
			}

			longitudeDirection = part7;
			
			if (longitudeDirection.equals("E") == false) {
				longitude = -longitude;
			}
			if (latitudeDirection.equals("N") == false) {
				latitude = -latitude;
			}
			
			if(part8.length() == 0){
				groundSpeed = 0f;
				speed = 0f;
			}else{
				groundSpeed = Float.parseFloat(part8);
				
				//Speed
				if (groundSpeed > 0) {
					// km/h = knots * 1.852
					speed = groundSpeed * KNOT;
				}
				// A negative speed doesn't make sense.
				if (speed < 0) {
					speed = 0f;
				}
			}
			
			if(part9.length() == 0){
				compassDegrees = 0;
			}else{
				compassDegrees = Math.round(Float.parseFloat(part9));
			}
			
			if(part10.length() == 0){
				dateOfFix = 0;
			}else{
				dateOfFix = Math.round(Float.parseFloat(part10));
			}

			if(part11.length() == 0){
				magneticVariation = 0;
			}else{
				magneticVariation = Math.round(Float.parseFloat(part11));
			}
			
			if(part12.length() == 0){
				magneticVariationLetter = "";
			}else{
				magneticVariationLetter = part12;
			}

		}catch(NoSuchElementException e){
			//System.err.println("RMCSentence: NoSuchElementException");
		}catch(NumberFormatException e){
			//System.err.println("RMCSentence: NumberFormatException");
		}catch(Exception e){
			//System.err.println("RMCSentence: Exception");
		}

	}//End Parse

}//End Class
