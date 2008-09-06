package lejos.gps;

/**
 * This class models a GPS Latitude
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class Longitude extends Degrees{

	public Longitude(double lonDegrees){
		super(lonDegrees);

		//Establish letter and sign
		if(lonDegrees>0){
			direction = "W";
		}else{
			direction = "E";
			RAWGPS_data = -lonDegrees;
		}
		//Calculate values
		decimalDegrees = this.degreesMinToDegrees("" + lonDegrees, 1);
		decimalDegreesToDMS();
		
		/*
		if((degrees >= -180) && (degrees <= 180)){
			
		}else{
			throw new NumberFormatException();
		}
		*/
	}
}
