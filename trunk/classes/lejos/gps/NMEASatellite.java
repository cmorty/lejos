package lejos.gps;

/**
 * This class models data extracted from NMEA GSV Sentence
 * 
 * $GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*67
 * 
 * 4    = SV PRN number
 * 5    = Elevation in degrees, 90 maximum
 * 6    = Azimuth, degrees from true north, 000 to 359
 * 7    = SNR, 00-99 dB (null when not tracking)
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class NMEASatellite{
	private int PRN = 0;
	private int elevation = 0;
	private int azimuth = 0;
	private int SNR = 0;

	/*
	 * Constructors
	 */

	public NMEASatellite(){
		//
	}
	
	/**
	 * Constructor which indicate information about:
	 * PRN, Elevation, Azimuth and SNR
	 * 
	 * @param p
	 * @param e
	 * @param a
	 * @param s
	 */
	public NMEASatellite(int p, int e, int a, int s){
		PRN = p;
		elevation = e;
		azimuth = a;
		SNR = s;
	}

	/*
	 * Getters & Setters 
	 */

	/**
	 * Return PRN Data from a Satellite
	 */
	public int getPRN(){
		return PRN;
	}
	
	/**
	 * Set PRN
	 * 
	 * @param p
	 */
	public void setPRN(int p){
		PRN = p;
	}

	/**
	 * Return Elevation Data from a Satellite
	 */
	public int getElevation(){
		return elevation;
	}
	
	/**
	 * Set Elevation
	 * 
	 * @param e
	 */
	public void setElevation(int e){
		elevation = e;
	}

	/**
	 * Return Azimuth Data from a Satellite
	 */
	public int getAzimuth(){
		return azimuth;
	}
	
	/**
	 * Set Azimuth
	 * 
	 * @param a
	 */
	public void setAzimuth(int a){
		azimuth = a;
	}

	/**
	 * Return SNR Data from a Satellite
	 */
	public int getSNR(){
		return SNR;
	}
	
	/**
	 * Set SNR
	 * 
	 * @param s
	 */
	public void setSNR(int s){
		SNR = s;
	}
}
