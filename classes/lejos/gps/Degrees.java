package lejos.gps;

/**
 * This class has been developed to model Latitude and Longitude
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class Degrees{

	//Data from GPS device
	protected double RAWGPS_data = 0.0d;
	protected double decimalDegrees = 0.0d;
	protected int minuteInt = 0;
	protected float minuteFract = 0;

	//Direction
	protected String direction = "";

	/*Constructor*/
	
	/**
	 * Constructor, which needs a initial value, data extracted from 
	 * GPS Receiver
	 */
	public Degrees(double deg){
		RAWGPS_data = deg;
	}

	/* Getters & Setters */

	/**
	 * Return Latitude/Longitude Letter
	 */
	public String getDirectionLeter(){
		return direction;
	}

	/**
	 * Return MinuteInt
	 * 
	 * @return
	 */
	public int getMinuteInt(){
		minuteInt = degreesMinutesToIntegerMinutes();
		return minuteInt;
	}
	
	/**
	 * Return MinuteFract
	 * 
	 * @return
	 */
	public float getMinuteFract() {
		minuteFract = degreesMinutesToFractionalMinutes();
		return minuteFract;
	}


	/**
	 * Any GPS Receiver gives Lat/Lon data in the following way:
	 * 
	 * http://www.gpsinformation.org/dale/nmea.htm
	 * http://www.teletype.com/pages/support/Documentation/RMC_log_info.htm
	 * 
	 * 4807.038,N   Latitude 48 deg 07.038' N
	 * 01131.000,E  Longitude 11 deg 31.000' E
	 * 
	 * This data is necessary to convert to Decimal Degrees.
	 * 
	 * Latitude values has the range: -90 <-> 90
	 * Longitude values has the range: -180 <-> 180
	 * 
	 * If Latitude then CoordenateType = 0
	 * If Longitude then CoordenateType = 1
	 * 
	 * @param DD_MM
	 * @param CoordenateType
	 * @return
	 */
	protected float degreesMinToDegrees(int CoordenateType) {//throws NumberFormatException
		float decDegrees = 0;
		float tempDegrees = 0;
		float tempMinutes = 0;
		float tempSeconds = 0;
		String doubleCharacterSeparator = ".";
		String DD_MM = "" + RAWGPS_data;
		String DDMM = "";

		//1. Count characters until character '.'
		int dotPosition = DD_MM.indexOf(doubleCharacterSeparator);
		if(CoordenateType == 0){
			//Latitude Management
			DDMM = DD_MM.substring(0, dotPosition);
			if(DDMM.length() == 4){
				tempDegrees = Float.parseFloat(DDMM.substring(0, 2));
				tempMinutes = Float.parseFloat(DD_MM.substring(2));
			}else if(DDMM.length() == 3){
				tempDegrees = Float.parseFloat(DDMM.substring(0, 1));
				tempMinutes = Float.parseFloat(DD_MM.substring(1));
			}

			if((tempDegrees >=0) && (tempDegrees <=90)){
				//throw new NumberFormatException();
			}
		}else{
			//Longitude Management
			DDMM = DD_MM.substring(0, dotPosition);
			if(DDMM.length() == 5){
				tempDegrees = Float.parseFloat(DDMM.substring(0, 3));
				tempMinutes = Float.parseFloat(DD_MM.substring(3));
			}else if(DDMM.length() == 4){
				tempDegrees = Float.parseFloat(DDMM.substring(0, 2));
				tempMinutes = Float.parseFloat(DD_MM.substring(2));
			}else if(DDMM.length() == 3){
				tempDegrees = Float.parseFloat(DDMM.substring(0, 1));
				tempMinutes = Float.parseFloat(DD_MM.substring(1));
			}

			if((tempDegrees >=0) && (tempDegrees <=180)){
				//throw new NumberFormatException();
			}
		}
		//Idea
		//http://id.mind.net/%7Ezona/mmts/trigonometryRealms/degMinSec/degMinSec.htm
		decDegrees = (float)(tempDegrees + (tempMinutes * (1.0 / 60.0)) + (tempSeconds * (1.0 / 3600.0)));
		
		return decDegrees;
	}

	/**
	 * Convert DegreesMinutes to IntegerMinutes
	 * 
	 * @return
	 */
	protected int degreesMinutesToIntegerMinutes() {//throws NumberFormatException

		int ddmmVal = 0;
		int minutes = 0;
		String doubleCharacterSeparator = ".";
		String DD_MM = "" + RAWGPS_data;
		String DDMM;

		//1. Count characters until character '.'
		int dotPosition = DD_MM.indexOf(doubleCharacterSeparator);

		DDMM = DD_MM.substring(0, dotPosition-1);
		
		ddmmVal = Integer.parseInt(DDMM);
		
		// Now change the wierd dddmm value to minutes
		minutes = (ddmmVal / 100 ) * 60 + (ddmmVal % 100);
		return minutes;
	}
	
	/**
	 * Convert DegreesMinutes to FractionalMinutes
	 * 
	 * @return
	 */
	protected float degreesMinutesToFractionalMinutes() {//throws NumberFormatException

		float fractionalMinutes = 0.0f;
		String doubleCharacterSeparator = ".";
		String DD_MM = "" + RAWGPS_data;
		String DDMM;

		//1. Count characters until character '.'
		int dotPosition = DD_MM.indexOf(doubleCharacterSeparator);

		DDMM = DD_MM.substring(dotPosition);
		
		fractionalMinutes = Float.parseFloat(DDMM);
		
		return fractionalMinutes;
	}
}
