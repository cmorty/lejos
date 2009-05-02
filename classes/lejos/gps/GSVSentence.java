package lejos.gps;


import java.util.*;

/**
 * This class has been designed to manage a GSV Sentence
 * 
 * GPS Satellites in view
 * 
 * eg. $GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
 *     $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
 *     $GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D
 * 
 * 
 *     $GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*67
 * 
 * 1    = Total number of messages of this type in this cycle
 * 2    = Message number
 * 3    = Total number of SVs in view
 * 4    = SV PRN number
 * 5    = Elevation in degrees, 90 maximum
 * 6    = Azimuth, degrees from true north, 000 to 359
 * 7    = SNR, 00-99 dB (null when not tracking)
 * 8-11 = Information about second SV, same as field 4-7
 * 12-15= Information about third SV, same as field 4-7
 * 16-19= Information about fourth SV, same as field 4-7
 * 
 * @author Juan Antonio Brenha Moral (major recoding by BB)
 */
public class GSVSentence extends NMEASentence{
	
	// TODO: Is this correct to limit maximumSatellites to 4? Perhaps it changes as 
	// satellitesTracked changes. Maybe this should just use satellitesTracked?
	//GGA
	private int satellitesTracked = 0;
	public static final int MAXIMUM_SATELLITES = 4;//0,1,2,3
	NMEASatellite [] ns;
	
	//Header
	public static final String HEADER = "$GPGSV";

	/*
	 * Constructor
	 */
	public GSVSentence(){
		// TODO: Does GPS really only connect to four? Why not more?
		// It would be better to use ArrayList or some other growable collection, using
		// the satellite ID as the key.
		// Check if GSV sentences cycle through diff't sat ids. If so this
		// will affect how data is allocated to satellites in array.
		ns = new NMEASatellite[MAXIMUM_SATELLITES];
		for(int i=0;i<MAXIMUM_SATELLITES;i++)
			ns[i] = new NMEASatellite();
	}
	
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
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked() {
		checkRefresh();
		return satellitesTracked;
	}

	/**
	 * Return a NMEA Satellite object
	 * 
	 * @param index the index of the satellite
	 * @return theNMEASatellite object for the selected satellite
	 */
	public NMEASatellite getSatellite(int index){
		checkRefresh();
		return ns[index];
	}
	
	/**
	 * Method used to parse a GSV Sentence
	 */
	protected void parse(){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(nmeaSentence,",");
		
		// TODO: I don't see any reason for using try-catch block here.
		try{
			st.nextToken(); // Skip header $GPGSV
			st.nextToken();//Message number
			satellitesTracked = Integer.parseInt(st.nextToken());//Number of satellites being tracked

			for(int i=0;i<MAXIMUM_SATELLITES;i++) {
				int PRN = Integer.parseInt(st.nextToken());
				// TODO: Elevation and azimuth have no decimals?
				int elevation = Integer.parseInt(st.nextToken());
				int azimuth = Integer.parseInt(st.nextToken());
				int SNR = Integer.parseInt(st.nextToken());
				
				ns[i].setPRN(PRN);
				ns[i].setElevation(elevation);
				ns[i].setAzimuth(azimuth);
				ns[i].setSNR(SNR);
			}
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e){
			//Empty
		}

	}//End parse
	
}//End class
