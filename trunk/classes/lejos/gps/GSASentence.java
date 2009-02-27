package lejos.gps;
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

class GSASentence extends NMEASentence{
	//GSA
	private String mode = "";
	private int modeValue = 0;
	private final int maximumSV = 12;
	private int[] SV;
	private float PDOP = 0;
	private float HDOP = 0;
	private float VDOP = 0;

	//Header
	public static final String HEADER = "$GPGSA";
	
	/*
	 * Constructor
	 */

	public GSASentence(){
		SV = new int[maximumSV];
	}

	/*
	 * GETTERS & SETTERS
	 */

	/**
	 * Return Mode1.
	 * Mode1 can receive the following values:
	 * 
	 * M=Manual, forced to operate in 2D or 3D
	 * A=Automatic, 3D/2D
	 * 
	 */
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
	 * Return an Array with Satellite IDs
	 * 
	 * @return the array of satellite IDs
	 */
	public int[] getSV(){
		checkRefresh(); 
		return SV;
	}

	/**
	 * Return PDOP
	 * 
	 * @return the PDOP
	 */
	public float getPDOP(){
		checkRefresh();
		return PDOP;
	}

	/**
	 * Return HDOP
	 * 
	 * @return the HDOP
	 */
	public float getHDOP(){
		checkRefresh();
		return HDOP;
	}

	/**
	 * Return VDOP
	 * 
	 * @return the VDOP
	 */
	public float getVDOP(){
		checkRefresh();
		return VDOP;
	}

	/**
	 * Method used to parse a GGA Sentence
	 */
	public void parse(){
		//StringTokenizer st = new StringTokenizer(nmeaSentence,",");
		st = new StringTokenizer(nmeaSentence,",");
		String sv = "";

		try{
			st.nextToken(); // Skip header $GPGSA
			mode = st.nextToken();
			modeValue = Integer.parseInt(st.nextToken());

			for(int i=0;i<=11;i++){
				sv = st.nextToken();
				if(sv.length() > 0){
					SV[i] = Integer.parseInt(sv);
				}else{
					SV[i] = 0;
				}
			}

			PDOP = Float.parseFloat(st.nextToken());
			HDOP = Float.parseFloat(st.nextToken());
			VDOP = Float.parseFloat(st.nextToken());

		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}

	}//End parse
}
