package lejos.addon.gps;

import java.util.*;

/**
 * This class has been designed to manage a GSA Sentence
 * 
 * GPS DOP and active satellites
 * 
 * eg1. $GPGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
 * eg2. $GPGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*35
 * 
 * 1    = Mode:
 *        M=Manual, forced to operate in 2D or 3D
 *        A=Automatic, 3D/2D
 * 2    = Mode:
 *        1=Fix not available
 *        2=2D
 *        3=3D
 * 3-14 = IDs of SVs used in position fix (null for unused fields)
 * 15   = PDOP
 * 16   = HDOP
 * 17   = VDOP
 * 
 * @author Juan Antonio Brenha Moral (major recoding by BB)
 * 
 */

public class GSASentence extends NMEASentence{
	//GSA
	private String mode = "";
	private int modeValue = 0;
	private static final int MAXIMUM_SATS = 12;
	private int[] prn;
	// Initialize with -1, means value not available:
	private float pdop = -1;
	private float hdop = -1;
	private float vdop = -1;
	
	//Header
	public static final String HEADER = "$GPGSA";
	
	/*
	 * Constructor
	 */
	public GSASentence(){
		prn = new int[MAXIMUM_SATS];
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
	 * Return Mode1.
	 * Mode1 can receive the following values:
	 * 
	 * M=Manual, forced to operate in 2D or 3D
	 * A=Automatic, 3D/2D
	 * 
	 */
	// TODO: Should return a char
	public String getMode(){
		checkRefresh();
		return mode;
	}

	/**
	 * Return Mode2.
	 * Mode1 can receive the following values:
	 * 
	 * 1=Fix not available
	 * 2=2D
	 * 3=3D
	 * 
	 */
	public int getModeValue(){
		checkRefresh();
		return modeValue;
	}

	/**
	 * Return an Array with Satellite prn (Pseudo Randon Noise) code.
	 * 
	 * @return the array of satellite IDs
	 */
	public int[] getPRN(){
		checkRefresh(); 
		return prn;
	}
	
	/**
	 * Return pdop
	 * 
	 * @return the pdop
	 */
	public float getPDOP(){
		checkRefresh();
		return pdop;
	}

	/**
	 * Return hdop
	 * 
	 * @return the hdop
	 */
	public float getHDOP(){
		checkRefresh();
		return hdop;
	}

	/**
	 * Return vdop
	 * 
	 * @return the vdop
	 */
	public float getVDOP(){
		checkRefresh();
		return vdop;
	}
	
	/**
	 * Method used to parse a GGA Sentence
	 */
	protected void parse(String sentence){
		st = new StringTokenizer(sentence,",");
		
		try{
			st.nextToken(); // Skip header $GPGSA
			mode = st.nextToken();
			modeValue = Integer.parseInt(st.nextToken());
			for(int i=0;i<MAXIMUM_SATS;i++){
				String sv = st.nextToken();
				if(sv.length() > 0){
					prn[i] = Integer.parseInt(sv);
				}else{
					prn[i] = -1;
				}
			}

			pdop = Float.parseFloat(st.nextToken());
			hdop = Float.parseFloat(st.nextToken());
			vdop = Float.parseFloat(st.nextToken());

		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

	}//End parse
}
