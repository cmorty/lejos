package lejos.addon.gps;


import java.util.*;

/**
 * VTGSentence is a Class designed to manage VTG Sentences from a NMEA GPS Receiver
 * 
 * $GPVTG
 * 
 * Track Made Good and Ground Speed.
 * 
 * eg1. $GPVTG,360.0,T,348.7,M,000.0,N,000.0,K*43
 * eg2. $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*41
 * 
 *            054.7,T      True course made good over ground, degrees
 *            034.4,M      Magnetic course made good over ground, degrees
 *            005.5,N      Ground speed, N=Knots
 *            010.2,K      Ground speed, K=Kilometers per hour
 * 
 * eg3. for NMEA 0183 version 3.00 active the Mode indicator field
 *      is added at the end
 *      $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K,A*53
 *            A            Mode indicator (A=Autonomous, D=Differential,
 *                         E=Estimated, N=Data not valid)
 * 
 * @author Juan Antonio Brenha Moral (major recoding by BB)
 */
public class VTGSentence extends NMEASentence{

	//RMC Sentence
	// TODO: Convert any of these floats to int?
	private float speed = 0; // TODO Probably default values should be negative?
	private float trueCourse = 0;
	private float magneticCourse = 0;

	//Header
	public static final String HEADER = "$GPVTG";

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
	 * Get Speed in Kilometers
	 * 
	 * @return the speed in kilometers per ???
	 */
	public float getSpeed(){
		checkRefresh();
		return speed;  
	}

	/**
	 * Get true course, in degrees.
	 * 
	 * @return the true course in degrees 0.0 to 360.0
	 */
	public float getTrueCourse(){
		checkRefresh();
		return trueCourse;
	}
	
	/**
	 * Get magnetic course, in degrees. Holux-1200 GPS doesn't have a built in magnetic compass so
	 * this value is blank.
	 * 
	 * @return the magnetic course in degrees 0.0 to 360.0
	 */
	public float getMagneticCourse(){
		checkRefresh();
		return magneticCourse;
	}
	
	/**
	 * Parase a RMC Sentence
	 * 
	 * $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K,A*53
	 */
	protected void parse (String sentence){
		st = new StringTokenizer(sentence,",");
		try{
			st.nextToken();//skip header $GPVTG
			trueCourse = Float.parseFloat(st.nextToken());//True course made good over ground, degrees
			st.nextToken();//Letter
			String sTemp = st.nextToken();
			if(sTemp.length() > 0) // This is blank with Holux-1200	
				magneticCourse = Float.parseFloat(sTemp);//Magnetic course made good over ground
			st.nextToken();//Letter
			st.nextToken();//Ground speed, N=Knots TODO: Could offer this value too.
			st.nextToken();//Letter
			speed = Float.parseFloat(st.nextToken());//Ground speed, K=Kilometers per hour
			//st.nextToken();//Letter
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e){
			//Empty
		}
	}//End Parse

}//End Class
