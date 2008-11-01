package javax.microedition.location;

/**
 * This class has been designed to manage coordinates 
 * using JSR-179 Location API
 * http://www.jcp.org/en/jsr/detail?id=179
 * 
 * @author Juan Antonio Brenha Moral
 */
public class Coordinates{
	private double latitude;
	private double longitude;
	private double altitude;
	
	/**
	 * Identifier for string coordinate representation Degrees, Minutes, decimal fractions of a minute
	 * See Also:Constant Field Values
	 */
	public static final int DD_MM=2;

	 /**
	  * Identifier for string coordinate representation Degrees, Minutes, Seconds and decimal fractions of a second
	 * See Also:Constant Field Values
	 */
	public static final int DD_MM_SS=1;

	static final double EARTH_RADIUS = 6378137D;
	
	static float calculatedDistance = (0.0F / 0.0F);
	static float calculatedAzimuth = (0.0F / 0.0F);
	
	/* Constructor */

	/**
	 * Create a Coordinate object with 3 parameters:
	 * latitude, longitude and altitude
	 * 
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 */
	public Coordinates(double latitude, double longitude,double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public Coordinates(double latitude, double longitude) {
		this(latitude,longitude,0);
	}

	/**
	 * Get latitude
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/*
	 * Set Latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/*
	 * Set Longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get Longitude
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/*
	 * Set Altitude in meters
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Get Altitude
	 * 
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}

  /**
     * Calculates the azimuth between the two points according to 
     * the ellipsoid model of WGS84. The azimuth is relative to true north. 
     * The Coordinates object on which this method is called is considered 
     * the origin for the calculation and the Coordinates object passed 
     * as a parameter is the destination which the azimuth is calculated to. 
     * When the origin is the North pole and the destination 
     * is not the North pole, this method returns 180.0. 
     * When the origin is the South pole and the destination is not 
     * the South pole, this method returns 0.0. If the origin is equal 
     * to the destination, this method returns 0.0. 
     * The implementation shall calculate the result as exactly as it can. 
     * However, it is required that the result is within 1 degree of the correct result.
     *
     */
	public double azimuthTo(Coordinates to){
		if(to == null){
			throw new NullPointerException();
		}else{
			calculateDistanceAndAzimuth(getLatitude(), getLongitude(), to.getLatitude(), to.getLongitude());
			return calculatedAzimuth;
		}
	}
	
	//ToDo
	static public String convert(double coordinate, int outputType){
		return "";
	}

	//ToDo
	static public float convert(String coordinate){
		return 0;
	}

	/**
	 *
     * Calculates the geodetic distance between the two points according 
     * to the ellipsoid model of WGS84. Altitude is neglected from calculations.
     * 
     * The implementation shall calculate this as exactly as it can. 
     * However, it is required that the result is within 0.36% of 
     * the correct result.
	 * 
	 * @param to the point to calculate the geodetic to
	 * @return the distance
	 */
	public double distance(Coordinates to){
		if(to == null){
			throw new NullPointerException();
		}else{
			calculateDistanceAndAzimuth(getLatitude(), getLongitude(), to.getLatitude(), to.getLongitude());
			return calculatedDistance / 1000;//To get values in Kilometers
		}
	}

	/**
	 * Convert a value in Radians into Degrees
	 * 
	 * @param d
	 * @return
	 */
	private static double toDegrees(double d){
		return (d * 360D) / 6.2831853071795862D;
	}

	/**
	 * Convert a value in degrees in Radians
	 * 
	 * @param d
	 * @return
	 */
	private static double toRadians(double d){
		return (d * 6.2831853071795862D) / 360D;
	}

	private static void calculateDistanceAndAzimuth(double d, double d1, double d2, double d3){
        double d4 = toRadians(d);
        double d5 = toRadians(d1);
        double d6 = toRadians(d2);
        double d7 = toRadians(d3);
        double d8 = 0.0033528106647474805D;
        double d9 = 0.0D;
        double d10 = 0.0D;
        double d20 = 0.0D;
        double d22 = 0.0D;
        double d24 = 0.0D;
        double d25 = 0.0D;
        double d26 = 0.0D;
        double d28 = 0.0D;
        double d29 = 0.0D;
        double d30 = 0.0D;
        double d31 = 0.0D;
        double d32 = 0.0D;
        double d33 = 5.0000000000000003E-10D;
        int i = 1;
        byte byte0 = 100;
        if(d4 == d6 && (d5 == d7 || Math.abs(Math.abs(d5 - d7) - 6.2831853071795862D) < d33))
        {
            calculatedDistance = 0.0F;
            calculatedAzimuth = 0.0F;
            return;
        }
        if(d4 + d6 == 0.0D && Math.abs(d5 - d7) == 3.1415926535897931D)
            d4 += 1.0000000000000001E-05D;
        double d11 = 1.0D - d8;
        double d12 = d11 * Math.tan(d4);
        double d13 = d11 * Math.tan(d6);
        double d14 = 1.0D / Math.sqrt(1.0D + d12 * d12);
        double d15 = d14 * d12;
        double d16 = 1.0D / Math.sqrt(1.0D + d13 * d13);
        double d17 = d14 * d16;
        double d18 = d17 * d13;
        double d19 = d18 * d12;
        d9 = d7 - d5;
        for(d32 = d9 + 1.0D; i < byte0 && Math.abs(d32 - d9) > d33; d9 = ((1.0D - d31) * d9 * d8 + d7) - d5)
        {
            i++;
            double d21 = Math.sin(d9);
            double d23 = Math.cos(d9);
            d12 = d16 * d21;
            d13 = d18 - d15 * d16 * d23;
            d24 = Math.sqrt(d12 * d12 + d13 * d13);
            d25 = d17 * d23 + d19;
            d10 = Math.atan2(d24, d25);
            double d27 = (d17 * d21) / d24;
            d28 = 1.0D - d27 * d27;
            d29 = 2D * d19;
            if(d28 > 0.0D)
                d29 = d25 - d29 / d28;
            d30 = -1D + 2D * d29 * d29;
            d31 = (((-3D * d28 + 4D) * d8 + 4D) * d28 * d8) / 16D;
            d32 = d9;
            d9 = ((d30 * d25 * d31 + d29) * d24 * d31 + d10) * d27;
        }

        double d34 = mod(Math.atan2(d12, d13), 6.2831853071795862D);
        d9 = Math.sqrt((1.0D / (d11 * d11) - 1.0D) * d28 + 1.0D);
        d9++;
        d9 = (d9 - 2D) / d9;
        d31 = ((d9 * d9) / 4D + 1.0D) / (1.0D - d9);
        d32 = (d9 * d9 * 0.375D - 1.0D) * d9;
        d9 = d30 * d25;
        double d35 = ((((((d24 * d24 * 4D - 3D) * (1.0D - d30 - d30) * d29 * d32) / 6D - d9) * d32) / 4D + d29) * d24 * d32 + d10) * d31 * 6378137D * d11;
        if((double)Math.abs(i - byte0) < d33)
        {
            calculatedDistance = (0.0F / 0.0F);
            calculatedAzimuth = (0.0F / 0.0F);
            return;
        }
        d34 = (180D * d34) / 3.1415926535897931D;
        calculatedDistance = (float)d35;
        calculatedAzimuth = (float)d34;
        if(d == 90D)
            calculatedAzimuth = 180F;
        else
        if(d == -90D)
            calculatedAzimuth = 0.0F;
    }

    private static double mod(double d, double d1){
        return d - d1 * Math.floor(d / d1);
    }
}
