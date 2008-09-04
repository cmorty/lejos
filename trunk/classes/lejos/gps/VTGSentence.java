package lejos.gps;

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
 * @author Juan Antonio Brenha Moral
 */
public class VTGSentence extends NMEASentence{

	//RMC Sentence
	private String nmeaHeader = "";
	private float speed = 0;

	//Header
	public static final String HEADER = "$GPVTG";

	/*
	 * GETTERS & SETTERS
	 */

	/**
	 * Get Speed in Kilometers
	 * 
	 * @return
	 */
	public float getSpeed(){
		return speed;  
	}

	/**
	 * Parase a RMC Sentence
	 * 
	 * $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K,A*53
	 */
	public void parse (){
		st = new StringTokenizer(nmeaSentence,",");

		try{
			nmeaHeader = st.nextToken();//$GPVTG
			st.nextToken();//True course made good over ground, degrees
			st.nextToken();//Letter
			st.nextToken();//Magnetic course made good over ground
			st.nextToken();//Letter
			st.nextToken();//Ground speed, N=Knots
			st.nextToken();//Letter
			speed = Float.parseFloat((String)st.nextToken());//Ground speed, K=Kilometers per hour
			//st.nextToken();//Letter
		}catch(NoSuchElementException e){
			//Empty
		}catch(NumberFormatException e2){
			//Empty
		}
	}//End Parse

}//End Class
