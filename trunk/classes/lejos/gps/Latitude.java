package lejos.gps;

/**
 * This class models a GPS Latitude
 * 
 * @author Juan Antonio Brenha Moral
 */
public class Latitude extends Degrees{

	public Latitude(double latDegrees){
		super(latDegrees);

		//Establish letter and sign
		if(latDegrees >0)
			direction = "N";
		else{
			direction = "S";
			RAWGPS_data= -latDegrees;
		}
		//Calculate values
		decimalDegrees = this.degreesMinToDegrees("" + latDegrees, 0);
		decimalDegreesToDMS();

		/*
		if((degrees >= -90) && (degrees <= 90)){
			
		}else{
			throw new NumberFormatException();
		}
		*/
	}
}
