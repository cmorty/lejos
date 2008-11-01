package lejos.gps;

import java.util.*;

/**
 * This class has been designed to manage a GGA Sentence
 * 
 * GGA - essential fix data which provide 3D location and accuracy data.
 * 
 * $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
 * 
 * Where:
 *      GGA          Global Positioning System Fix Data
 *      123519       Fix taken at 12:35:19 UTC
 *      4807.038,N   Latitude 48 deg 07.038' N
 *      01131.000,E  Longitude 11 deg 31.000' E
 *      1            Fix quality: 0 = invalid
 *                                1 = GPS fix (SPS)
 *                                2 = DGPS fix
 *                                3 = PPS fix
 * 			       4 = Real Time Kinematic
 * 			       5 = Float RTK
 *                                6 = estimated (dead reckoning) (2.3 feature)
 * 			       7 = Manual input mode
 * 			       8 = Simulation mode
 *      08           Number of satellites being tracked
 *      0.9          Horizontal dilution of position
 *      545.4,M      Altitude, Meters, above mean sea level
 *      46.9,M       Height of geoid (mean sea level) above WGS84
 *                       ellipsoid
 *      (empty field) time in seconds since last DGPS update
 *      (empty field) DGPS station ID number
 *      *47          the checksum data, always begins with *
 * 
 * @author Juan Antonio Brenha Moral
 */
public class GGASentence extends NMEASentence{
	
	//GGA
	private String nmeaHeader = "";
	private float dateTimeOfFix = 0;
	private float latitude = 0;
	private String latitudeDirection = "";
	private float longitude = 0;
	private String longitudeDirection = "";
	private float quality;
	private float satellitesTracked = 0;
	private float hdop = 0;
	private float altitude = 0;
	private String altitudeUnits;
	private float geoidalSeparation;
	private String geoidalSeparationUnit;

	//Header
	public static final String HEADER = "$GPGGA";

	/*
	 * GETTERS & SETTERS
	 */

	/**
	 * Get Latitude
	 * 
	 */
	public float getLatitude() {
		return latitude;
	}
	
	/**
	 * Get Latitude Direction
	 * 
	 * @return the latitude direction
	 */
	public String getLatitudeDirection(){
		return latitudeDirection;
	}
	
	/**
	 * Get Longitude
	 * 
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * Get Longitude Direction
	 * @return the longitude direction
	 */
	public String getLongitudeDirection(){
		return longitudeDirection;
	}
	
	/**
	 * Get Altitude
	 * 
	 * @return the altitude
	 */
	public float getAltitude(){
		return altitude;
	}

	/**
	 * Returns the last time stamp retrieved from a satellite
	 * 
	 * @return The time as a UTC integer. 123519 = 12:35:19 UTC
	 */
	public int getTime(){
		return Math.round(dateTimeOfFix);
	}
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked() {
		return Math.round(satellitesTracked);
	}

	/**
	 * Get GPS Quality Data
	 * 
	 * @return the quality
	 */
	public int getQuality(){
		return Math.round(quality);
	}

	
	/**
	 * Method used to parse a GGA Sentence
	 */
	public void parse(){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(nmeaSentence,",");
		String q = "";
		String h = "";
		
		try{
			nmeaHeader = st.nextToken();//Global Positioning System Fix Data
			dateTimeOfFix = Float.parseFloat((String)st.nextToken());//UTC Time
			latitude = degreesMinToDegrees(st.nextToken(),0);
			latitudeDirection = st.nextToken();//N
			longitude = degreesMinToDegrees(st.nextToken(),1);
			longitudeDirection = st.nextToken();//E
			q = st.nextToken();
			if(q.length() == 0){
				quality = 0;
			}else{
				quality = Float.parseFloat(q);//Fix quality
			}
			//quality = Float.parseFloat(st.nextToken());//Fix quality
			satellitesTracked = Float.parseFloat((String)st.nextToken());//Number of satellites being tracked

			h = st.nextToken();
			if(h.length() == 0){
				hdop = 0;
			}else{
				hdop = Float.parseFloat(h);//Horizontal dilution of position
			}
			//hdop = Float.parseFloat(st.nextToken());//Horizontal dilution of position
			altitude = Float.parseFloat((String)st.nextToken());

			//Improve quality data
			if (longitudeDirection.equals("E") == false) {
				longitude = -longitude;
			}
			if (latitudeDirection.equals("N") == false) {
				latitude = -latitude;
			}

		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

	}//End parse
	
}//End class
