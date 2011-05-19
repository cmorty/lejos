package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.util.EndianTools;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Class for controlling dGPS sensor from Dexter Industries
 *
 * @author Mark Crosbie  <mark@mastincrosbie.com>
 * 22 January, 2011
 *
*/
public class GPSSensor extends I2CSensor {
	/*
	 * Documentation can be found here: http://www.dexterindustries.com/download.html#dGPS
	 */
	
	public static final byte DGPS_I2C_ADDR   = 0x06;      /*!< Barometric sensor device address */
	public static final byte DGPS_CMD_UTC    = 0x00;      /*!< Fetch UTC */
	public static final byte DGPS_CMD_STATUS = 0x01;      /*!< Status of satellite link: 0 no link, 1 link */
	public static final byte DGPS_CMD_LAT    = 0x02;      /*!< Fetch Latitude */
	public static final byte DGPS_CMD_LONG   = 0x04;      /*!< Fetch Longitude */
	public static final byte DGPS_CMD_VELO   = 0x06;      /*!< Fetch velocity in cm/s */
	public static final byte DGPS_CMD_HEAD   = 0x07;      /*!< Fetch heading in degrees */
	public static final byte DGPS_CMD_DIST   = 0x08;      /*!< Fetch distance to destination */
	public static final byte DGPS_CMD_ANGD   = 0x09;      /*!< Fetch angle to destination */
	public static final byte DGPS_CMD_ANGR   = 0x0A;      /*!< Fetch angle travelled since last request */
	public static final byte DGPS_CMD_SLAT   = 0x0B;      /*!< Set latitude of destination */
	public static final byte DGPS_CMD_SLONG  = 0x0C;      /*!< Set longitude of destination */
	
	/**
	* Constructor
	* @param sensorPort the sensor port the sensor is connected to
	*/
    public GPSSensor(I2CPort sensorPort) {
        super(sensorPort, DGPS_I2C_ADDR, I2CPort.STANDARD_MODE, TYPE_LOWSPEED);
    }
    
    /**
	* Return status of link to the GPS satellites
	* LED on dGPS should light if satellite lock acquired
	* @return true if GPS link is up, else false
	*/
    public boolean linkStatus() {
   		byte reply[] = new byte[1];

    	this.getData(DGPS_CMD_STATUS, reply, 0, 1);
    	return (reply[0] == 1);
    }

	/**
	* Get the current time stored on the dGPS
	* @return current UTC time stored on the device
	*/ 
    public int getUTC() {
   	 	byte reply[] = new byte[4];
    	int r = this.getData(DGPS_CMD_UTC, reply, 0, 4);

    	if(r < 0) return r;

    	return EndianTools.decodeIntBE(reply, 0);
    }


    /**
     * Read the current latitude in degrees (positive=North, negative=South)
     * @return current latitude in decimal degrees
     */
    public int getLat(){
    	byte reply[] = new byte[4];

    	this.getData(DGPS_CMD_LAT, reply, 0, 4);
    	
    	return EndianTools.decodeIntBE(reply, 0);
    }

	/**
	* Read the current longitude in degrees (positive=East, negative=West)
	* @return current longitude in decimal degrees
	*/ 
    public int getLong() {
   	 	byte reply[] = new byte[4];

    	this.getData(DGPS_CMD_LONG, reply, 0, 4);

    	return EndianTools.decodeIntBE(reply, 0);
    }


	/**
	 * Read the current velocity in cm/s
	 * @return current velocity in cm/s
	 */
    public int getVelocity() {
    	byte reply[] = new byte[4];
   
    	this.getData(DGPS_CMD_VELO, reply, 1, 3);
    	reply[0] = 0;

    	return EndianTools.decodeIntBE(reply, 0);
    }

    /**
     * Read the current heading in degrees
     * @return current heading in degrees
     */
    public int getHeading() {
   	 byte reply[] = new byte[2];

    	this.getData(DGPS_CMD_HEAD, reply, 0, 2);

    	return EndianTools.decodeUShortBE(reply, 0);
    }

    /**
     * Read the current relative heading in degrees
     * Angle travelled since last request. See dGPS manual.
     * @return relative head
     */
     public int getRelativeHeading() {
    	 byte reply[] = new byte[2];

    	 this.getData(DGPS_CMD_ANGR, reply, 0, 2);

     	return EndianTools.decodeUShortBE(reply, 0);
     }

	/**
	* Distance to destination in meters
	* @return distance to destination in meters
	*/
     public int getDistanceToDest() {
    	 byte reply[] = new byte[4];

    	 this.getData(DGPS_CMD_DIST, reply, 0, 4);

    	 return EndianTools.decodeIntBE(reply, 0);
     }

	/**
	* Angle to destination in degrees
	* @return angle to destination in degrees
	*/
     public int getAngleToDest() {
    	 byte reply[] = new byte[2];
    	 
    	 this.getData(DGPS_CMD_ANGD, reply, 0, 2);

    	 return EndianTools.decodeUShortBE(reply, 0);
     }

	/**
	* Set destination latitude coordinates
	* @param latitude destination's latitude in decimal degrees
	* @return 0 if no error else error code
	*/
     public int setLatitude(int latitude) {
    	 // We set the latitude in the dGPS
    	 byte args[] = new byte[4];
    	 EndianTools.encodeIntBE(latitude, args, 0);

    	 return this.sendData(DGPS_CMD_SLAT, args, 0, 4);
     }


	/**
	* Set destination longitude coordinates
	* @param longitude destination's longitude in decimal degrees
	* @return 0 if no error else error code
	*/
     public int setLongitude(int longitude) {
    	 // We set the longitude in the dGPS
    	 byte args[] = new byte[4];
    	 EndianTools.encodeIntBE(longitude, args, 0);

    	 return this.sendData(DGPS_CMD_SLONG, args, 0, 4);
     }
}
