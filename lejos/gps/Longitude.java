package lejos.gps;

/**
 * This class models a GPS Latitude
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class Longitude extends Degrees{

	/**
	 * Constructor
	 * 
	 * @param lonDegrees
	 */
	public Longitude(double lonDegrees){
		super(lonDegrees);

		//Establish letter
		if(lonDegrees>0){
			direction = "W";
		}else{
			direction = "E";
		}

		/*
		if((degrees >= -180) && (degrees <= 180)){
			
		}else{
			throw new NumberFormatException();
		}
		*/
	}

	/**
	 * Get Latitude in format Decimal Degrees.
	 * This format is used with Coordinates Objects
	 * 
	 * @return the longitude in decimal degrees
	 */
	public double getDecimalDegrees(){
		String RAWData = "" + RAWGPS_data;
		if(RAWData.length() >= 8){
			decimalDegrees = this.degreesMinToDegrees(1);

			//Establish the sign
			if(decimalDegrees>0){
				decimalDegrees = -decimalDegrees;
			}
		}else{
			decimalDegrees = 0.0d;
		}
		return decimalDegrees;
	}
}
