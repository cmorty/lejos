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
 * @author Juan Antonio Brenha Moral
 */
class GSVSentence extends NMEASentence{
	
	//GGA
	private float satellitesTracked = 0;
	private final int maximumSatellites = 4;//0,1,2,3
	NMEASatellite [] ns;
	
	//Header
	public static final String HEADER = "$GPGSV";

	/*
	 * Constructor
	 */
	public GSVSentence(){
		// TODO: Does GPS really only connect to four? Why not more?
		ns = new NMEASatellite[maximumSatellites];
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked() {
		checkRefresh();
		return Math.round(satellitesTracked);
		// TODO: Why is Juan using Math.round()?
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
	public void parse(){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(nmeaSentence,",");
		float PRN = 0;
		float elevation = 0;
		float azimuth = 0;
		float SNR = 0;

		
		try{
			st.nextToken(); // Skip header $GPGSV
			st.nextToken();//Message number
			satellitesTracked = Float.parseFloat((String)st.nextToken());//Number of satellites being tracked

			// TODO: This code has redundancies! Should use array of Satellites.
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns[0].setPRN(Math.round(PRN));
			ns[0].setElevation(Math.round(elevation));
			ns[0].setAzimuth(Math.round(azimuth));
			ns[0].setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns[1].setPRN(Math.round(PRN));
			ns[1].setElevation(Math.round(elevation));
			ns[1].setAzimuth(Math.round(azimuth));
			ns[1].setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns[2].setPRN(Math.round(PRN));
			ns[2].setElevation(Math.round(elevation));
			ns[2].setAzimuth(Math.round(azimuth));
			ns[2].setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns[3].setPRN(Math.round(PRN));
			ns[3].setElevation(Math.round(elevation));
			ns[3].setAzimuth(Math.round(azimuth));
			ns[3].setSNR(Math.round(SNR));
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

	}//End parse
	
}//End class
