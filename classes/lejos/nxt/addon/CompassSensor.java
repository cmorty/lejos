package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.robotics.*;
/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for a HiTechnic or Mindsensors compass.
 * 
 */
public class CompassSensor extends I2CSensor implements DirectionFinder {
	byte[] buf = new byte[2];
	private static final String MINDSENSORS_ID = "mndsnsrs";
	private boolean isMindsensors; // For comparing HiTechnic vs. Mindsensors
	private float cartesianCalibrate = 0; // Used by both cartesian methods.
        private float velocity, acceleration;
        private float heading, diff; // Expose publically when testing - BG 2009-12-30
	
	// Mindsensors.com constants:
	private final static byte COMMAND = 0x41;
	private final static byte BEGIN_CALIBRATION = 0x43;
	private final static byte END_CALIBRATION = 0x44;
	
	// HiTechnic constants:
	private final static byte MEASUREMENT_MODE = 0x00;
        private Regulator reg = new Regulator();

	public CompassSensor(I2CPort port)
	{
		super(port);		
		isMindsensors = (this.getProductID().equals(MINDSENSORS_ID));
                reg.start();
	}
	
	/**
	 * Returns the directional heading in degrees. (0 to 359.9)
	 * 0 is due North (on Mindsensors circuit board a white arrow indicates
	 * the direction of compass). Reading increases clockwise.
	 * @return Heading in degrees. Resolution is within 0.1 degrees
	 */
	public float getDegrees() {		
		int ret = getData(0x42, buf, 2);
		if(ret != 0) return -1;
		
		if(isMindsensors) { // Check if this is mindsensors
			// NOTE: The following only works when Mindsensors compass in integer mode
			/*int iHeading = (0xFF & buf[0]) | ((0xFF & buf[1]) << 8);
			float dHeading = iHeading / 10.00F;*/
			// Byte mode (default - will use Integer mode later)
			int dHeading = (0xFF & buf[0]);
			dHeading = dHeading * 360;
			dHeading = dHeading / 255;
			return dHeading;
		} else {
			return ((buf[0] & 0xff)<< 1) + buf[1];
		}
	}
	/**
	 * Compass readings increase clockwise from 0 to 360, but Cartesian
	 * coordinate systems increase counter-clockwise. This method returns
	 * the Cartesian compass reading. Also, the resetCartesianZero() method
	 * can be used to designate any direction as zero, rather than relying
	 * on North as being zero.
	 * @return Cartesian direction.
	 */
	public float getDegreesCartesian() {
		float degrees = cartesianCalibrate - getDegrees() ;
		if(degrees>=360) degrees -= 360;
		if(degrees<0) degrees += 360;
		return degrees;
	}
	
	/**
	 * Changes the current direction the compass is facing into the zero 
	 * angle. 
	 *
	 */
	public void resetCartesianZero() {
		cartesianCalibrate = getDegrees();
	}
	
	/**
	 * Starts calibration for Mindsensors.com compass. Must rotate *very* 
	 * slowly ,taking at least 20 seconds per rotation.
	 * Mindsensors: At least 2 full rotations.
	 * HiTechnic: 1.5 to 2 full rotations.
	 * Must call stopCalibration() when done.
	 */
	public void startCalibration() {
		buf[0] = BEGIN_CALIBRATION; 
		// Same value for HiTechnic and Mindsensors
		super.sendData(COMMAND, buf, 1);
	}
	
	/**
	 * Ends calibration sequence.
	 *
	 */
	public void stopCalibration() {
		if(isMindsensors)
			buf[0] = END_CALIBRATION;
		else
			buf[0] = MEASUREMENT_MODE;
		super.sendData(COMMAND, buf, 1);
	}

        /**
         * Returns the current rate-of-turn in degrees
         * @return Angular velocity in degrees.
         */
        public float getAngularVelocity() {
            return velocity;
        }

        /**
         * Returns the current rate at which the angular velocity is increasing or decreasing in degrees-per-second, per second
         * @return Angular acceleration in degrees-per-second per second.
         */
        public float getAngularAcceleration() {
            return acceleration;
        }

        /**
         * This is a private thread class that is used to continously integrate sucessive readings from the compass
         */
        private class Regulator extends Thread {

            protected Regulator() {
                setDaemon(true);
            }

            @Override
            public void run() {
                float lastHeading = getDegreesCartesian();
                float lastVelocity = 0F;
                long lastUpdate = System.currentTimeMillis();
                while (true) {
                    Thread.yield();
                    long now = System.currentTimeMillis();
                    if(now - lastUpdate < 50)
                        continue;

                    // Get heading and change in heading
                    heading = getDegreesCartesian();
                    diff = heading - lastHeading;
                    if(diff > 180)
                        diff = heading - 360 - lastHeading;
                    if(diff < -180)
                        diff = 360 - lastHeading + heading;

                    // Angular velocity
                    float secondsSinceLastReading = (float)(now - lastUpdate) / 1000F;
                    velocity = diff / secondsSinceLastReading;
                    acceleration = (velocity - lastVelocity) / secondsSinceLastReading;

                    // Move On
                    lastVelocity = velocity;
                    lastHeading = heading;
                    lastUpdate = now;
                }
            }
        }

}