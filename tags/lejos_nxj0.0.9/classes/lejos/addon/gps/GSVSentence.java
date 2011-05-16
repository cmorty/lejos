package lejos.addon.gps;

import java.util.*;

/**
 * This class has been designed to manage a GSV Sentence
 * 
 * GPS Satellites in View
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
 * @author recoded by BB
 */
/*
 * DEVELOPER NOTES:
 * This sentence is a little harder to parse because it uses data from multiple sentences
 * and there might be any number from 1 to 4 of them, perhaps more.
 */
public class GSVSentence extends NMEASentence{
	
	//GGA
	private int satellitesInView = 0;
	//public static final int MAXIMUM_SATELLITES = 4;//0,1,2,3
	private Satellite [] ns;
	
	// Globals used for parsing multiple sentences in parse() method.
	// TODO: Might be able to delete these three:
	private int currentSentence;
	private int currentSatellite;
	private int totalSentences;
	private String [] sentences;
	
	//Header
	public static final String HEADER = "$GPGSV";
	
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
	 * Returns the number of satellites currently in view.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesInView() {
		// TODO: Currently this only works in GPSInfo example because it retrieves getSatellite() as soon as a GSV sentence is received.
		// If you try to get this at any other time it won't work.
		checkRefresh();
		return satellitesInView;
	}

	/**
	 * Return a NMEA Satellite object.  
	 * 
	 * @param index the index of the satellite
	 * @return theNMEASatellite object for the selected satellite
	 */
	public Satellite getSatellite(int index){
		// Must be synchronized so that all 3-4 GSV sentences are read before it tries to give out data.
		// TODO: Make sure all 4 of 4 sentences are read. Do quick check here. Otherwise array is temporarily filled with 0s, causes output to flicker.
		// TODO: GPS notifier should only call on LAST of GSV sentences received. Ignore others in sequence until data is present.
		checkRefresh();
		
		return ns[index];
	}
	
	protected void setSentence(String sentence, int sentenceNumber, int total) {
		if(sentenceNumber == 1) sentences = new String[total];
		sentences[sentenceNumber - 1] = sentence;
	}
	
	/**
	 * Method used to parse a GSV Sentence. Note this ignores 'sentence' and
	 * instead uses an internal array containing a full set of GSV sentences.
	 */
	protected void parse(String sentence){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		
		// TODO: I don't see any reason for using try-catch block here.
		try{
			// Loop through all sentences parsing data:
			for(int i=0;i<sentences.length;i++) {
				st = new StringTokenizer(sentences[i],",");
				st.nextToken(); // Skip header $GPGSV
				totalSentences = Integer.parseInt(st.nextToken());// Total messages
				currentSentence = Integer.parseInt(st.nextToken());//Message number
				satellitesInView = Integer.parseInt(st.nextToken());//Number of satellites being tracked
	
				if(currentSentence == 1) {
					ns = new Satellite[satellitesInView];
					for(int j=0;j<ns.length;j++)
						ns[j] = new Satellite();
					currentSatellite = 0;
				}

				for(;currentSatellite<(currentSentence * 4);currentSatellite++) {
				//for(;st.hasMoreTokens();currentSatellite++) {
					int PRN = Integer.parseInt(st.nextToken());
					int elevation = Integer.parseInt(st.nextToken());
					int azimuth = Integer.parseInt(st.nextToken());
					int SNR = Integer.parseInt(st.nextToken());
					
					ns[currentSatellite].setPRN(PRN);
					ns[currentSatellite].setElevation(elevation);
					ns[currentSatellite].setAzimuth(azimuth);
					ns[currentSatellite].setSignalNoiseRatio(SNR);
				}
			}
		}catch(NoSuchElementException e){
			System.err.println("GSVSentence Exception");
		}catch(NumberFormatException e){
			System.err.println("GSVSentence NFException");
		}

	}//End parse
	
}//End class
