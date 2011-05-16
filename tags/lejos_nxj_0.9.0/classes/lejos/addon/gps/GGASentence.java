package lejos.addon.gps;


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
 * @author BB
 */
public class GGASentence extends NMEASentence{
	
	//GGA
	// TODO: Convert all/most of Juan's floats to int.
	// TODO: Initialize values with -1 or perhaps NaN
	private float dateTimeOfFix = -1;
	private double latitude = 0;
	private char latitudeDirection; // TODO: Delete me? Maybe used briefly in calc.
	private double longitude = 0;
	private char longitudeDirection; // ToDO: Delete me?
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
	 * Returns the NMEA header for this sentence.
	 */
	@Override
	public String getHeader() {
		return HEADER;
	}
	
	/**
	 * Get Latitude
	 * 
	 */
	public double getLatitude() {
		checkRefresh();
		return latitude;
	}
	
	/**
	 * Get Latitude Direction
	 * 
	 * @return the latitude direction
	 */
	// TODO: I think this is a useless method because we just use + or - in coordinates.
	public char getLatitudeDirection(){
		checkRefresh();
		return latitudeDirection;
	}
	
	/**
	 * Get Longitude
	 * 
	 */
	public double getLongitude() {
		checkRefresh();
		return longitude;
	}

	/**
	 * Get Longitude Direction
	 * @return the longitude direction
	 */
	// TODO: I think this is a useless method because we just use + or - in coordinates.
	public char getLongitudeDirection(){
		checkRefresh();
		return longitudeDirection;
	}
	
	/**
	 * Get Altitude
	 * 
	 * @return the altitude
	 */
	public float getAltitude(){
		checkRefresh();
		return altitude;
	}

	/**
	 * Returns the last time stamp retrieved from a satellite
	 * 
	 * @return The time as a UTC integer. 123519 = 12:35:19 UTC
	 */
	public int getTime(){
		checkRefresh();
		// TODO: Why is he using Math.round?
		return Math.round(dateTimeOfFix);
	}
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked() {
		checkRefresh();
		// TODO: Why is he using Math.round?
		return Math.round(satellitesTracked);
	}

	/**
	 * Get GPS Quality Data
	 * 
	 * @return the fix quality
	 */
	public int getFixQuality(){
		checkRefresh();
		// TODO: Why is he using Math.round?
		return Math.round(quality);
	}
	
	/**
	 * Method used to parse a GGA Sentence
	 */
	@Override
	protected void parse(String sentence){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(sentence,",");
		// TODO: What's with all these Strings defined as ""?
		String q = "";
		String h = "";
		
		// TODO: Should this really be in a try-catch block? Didn't do anything until I added System.err output.
		try{
			st.nextToken(); // skip $GPGGA header
			// TODO: I attempted Integer.parseInt and it threw a NumberFormatException because
			// UTC looks like 175958.000. Is it wrong to discard decimals? Always seem to be 000.
			dateTimeOfFix = Float.parseFloat(st.nextToken());//UTC Time
			latitude = degreesMinToDegrees(st.nextToken());
			latitudeDirection = st.nextToken().charAt(0);//N
			longitude = degreesMinToDegrees(st.nextToken());
			longitudeDirection = st.nextToken().charAt(0);//E
			q = st.nextToken();
			if(q.length() == 0){
				quality = 0;
			}else{
				quality = Float.parseFloat(q);//Fix quality
			}
			//quality = Float.parseFloat(st.nextToken());//Fix quality
			satellitesTracked = Float.parseFloat(st.nextToken());//Number of satellites being tracked
			
			h = st.nextToken();
			if(h.length() == 0){
				hdop = 0;
			}else{
				hdop = Float.parseFloat(h);//Horizontal dilution of position
			}
			
			altitude = Float.parseFloat(st.nextToken());

			// Geoidal separation is 0 with Holux-1200. If decide to use in future check for 0 length before parse.
			//this.geoidalSeparation = Float.parseFloat(st.nextToken()); 
			
			//Improve quality data
			if (longitudeDirection != 'E') {
				longitude = -longitude;
			}
			if (latitudeDirection != 'N') {
				latitude = -latitude;
			}

		}catch(NoSuchElementException e){
			//Empty
			System.err.println("GGASentence.parse() NoSuchElementException thrown: " + e.getMessage());
		}catch(NumberFormatException e){
			//Empty
			System.err.println("GGASentence.parse() NumberFormatException thrown: " + e.getMessage());
		}

	}//End parse
	
}//End class
