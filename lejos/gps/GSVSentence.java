package lejos.pgs;

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
public class GSVSentence extends NMEASentence{
	
	//GGA
	private String nmeaHeader = "";
	private float satellitesTracked = 0;
	private final int maximumSatellites = 4;//0,1,2,3
	NMEASatellite ns1;
	NMEASatellite ns2;
	NMEASatellite ns3;
	NMEASatellite ns4;
	
	//Header
	public static final String HEADER = "$GPGSV";

	/*
	 * Constructor
	 */
	public GSVSentence(){
		//ns = new NMEASatellite[maximumSatellites];
		ns1 = new NMEASatellite();
		ns2 = new NMEASatellite();
		ns3 = new NMEASatellite();
		ns4 = new NMEASatellite();
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
		return Math.round(satellitesTracked);
	}

	/**
	 * Return a NMEA Satellite object
	 * 
	 * @param index
	 * @return
	 */
	public NMEASatellite getSatellite(int index){
		NMEASatellite ns = new NMEASatellite();
		if(index == 0){
			ns = ns1;
		}else if(index == 1){
			ns = ns2;
		}else if(index == 2){
			ns = ns3;
		}else if(index == 3){
			ns = ns4;
		}
		return ns;
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
			nmeaHeader = st.nextToken();//GPS Satellites in view
			st.nextToken();//Message number
			satellitesTracked = Float.parseFloat((String)st.nextToken());//Number of satellites being tracked

			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns1.setPRN(Math.round(PRN));
			ns1.setElevation(Math.round(elevation));
			ns1.setAzimuth(Math.round(azimuth));
			ns1.setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns2.setPRN(Math.round(PRN));
			ns2.setElevation(Math.round(elevation));
			ns2.setAzimuth(Math.round(azimuth));
			ns2.setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns3.setPRN(Math.round(PRN));
			ns3.setElevation(Math.round(elevation));
			ns3.setAzimuth(Math.round(azimuth));
			ns3.setSNR(Math.round(SNR));
			PRN = Float.parseFloat((String)st.nextToken());
			elevation = Float.parseFloat((String)st.nextToken());
			azimuth = Float.parseFloat((String)st.nextToken());
			SNR = Float.parseFloat((String)st.nextToken());
			ns4.setPRN(Math.round(PRN));
			ns4.setElevation(Math.round(elevation));
			ns4.setAzimuth(Math.round(azimuth));
			ns4.setSNR(Math.round(SNR));
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

	}//End parse
	
}//End class
