package lejos.gps;
/**
 * This class models a GPS Latitude
 * 
 * @author Juan Antonio Brenha Moral
 */
public class Latitude extends Degrees{

	/**
	 * Constructor
	 * 
	 * @param latDegrees
	 */
	public Latitude(double latDegrees){
		super(latDegrees);

		//Establish letter
		if(latDegrees >0)
			direction = "N";
		else{
			direction = "S";
		}

		/*
		if((degrees >= -90) && (degrees <= 90)){
			
		}else{
			throw new NumberFormatException();
		}
		*/
	}
	
	/**
	 * Get Latitude in format Decimal Degrees.
	 * This format is used with Coordinates Objects
	 * 
	 * @return
	 */
	public double getDecimalDegrees(){
		String RAWData = "" + RAWGPS_data;
		if(RAWData.length() >= 8){
			decimalDegrees = this.degreesMinToDegrees(0);

			//Establish the sign
			if(decimalDegrees >0){
				//
			}else{
				decimalDegrees= -decimalDegrees;
			}
		}else{
			decimalDegrees = 0.0d;
		}

		return decimalDegrees;
	}
}
