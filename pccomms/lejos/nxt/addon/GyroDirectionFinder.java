package lejos.nxt.addon;

import lejos.robotics.DirectionFinder;

/**
 * Implementation of the DirectionFinder interface that Integrates repeated rate-of-turn readings from a GyroSensor
 * into a continuously updated heading. This class is very similar to CompassSensor, 
 * except that the direction returned does not convey true heading (north, south, etc) but rather
 * relative heading change since the last time setDegrees() or resetCartesianZero() was called.
 * @author Brent Gardner
 * @author Kirk P. Thompson
 */
public class GyroDirectionFinder implements DirectionFinder
{
    private float cartesianCalibrate = 0;
    private float heading = 0;
    private float acceleration;
    private boolean calibrating = false;
    private Regulator reg = new Regulator();
    private GyroSensor gyro;

    /** Creates and initializes a new <code>GyroDirectionFinder</code> using passed <code>GyroSensor</code> 
     * @param gyro
     * @see GyroSensor
     */
    public GyroDirectionFinder(GyroSensor gyro) {
        this(gyro, false);
    }

    /** Creates and initializes a new <code>GyroDirectionFinder</code> using passed <code>GyroSensor</code> and does
     * the <code>GyroSensor.setOffset()</code> method.
     * @param gyro
     * @see GyroSensor#setOffset()
     * @see #startCalibration
     */
    public GyroDirectionFinder(GyroSensor gyro, boolean calibrate) {
        this.gyro = gyro;
        reg.start();
        if(calibrate == false) return;

        // Optional calibration
        startCalibration();
    }

    /**
     * Resets the current heading to a desired value
     */
    public void setDegrees(float heading) {
        this.heading = heading;
    }

    /**
     * Returns the directional heading in degrees. Includes "winding",
     * so the value could be greater than 360 or less than 0
     * if the robot has done multiple rotations since the last call to resetCartesianZero()
     * @return Heading in degrees.
     */
    public float getDegrees() {
        return heading;
    }

    /**
     * Returns the current rate-of-turn in degrees, as read by the GyroSensor
     * @return Angular velocity in degrees.
     */
    public float getAngularVelocity() {
        return (float)gyro.getAngularVelocity();
    }

    /**
     * Returns the current rate at which the angular velocity is increasing or decreasing in degrees-per-second, per second
     * @return Angular acceleration in degrees-per-second per second.
     */
    public float getAngularAcceleration() {
        return acceleration;
    }

    /**
     * Returns the current rate-of-turn in degrees, as read by the GyroSensor
     * @return Heading in degrees.
     */
    public float getDegreesCartesian() {
        return cartesianCalibrate - getDegrees();
    }

    /**
     * Resets the current heading to a desired value
     */
    public void setDegreesCartesian(float heading) {
        this.heading = cartesianCalibrate - heading;
    }

    /**
     * Resets the current heading to zero
     */
    public void resetCartesianZero() {
        cartesianCalibrate = getDegrees();
    }

    /**
     * Find bias of gyro while at rest (ensure it is at rest). This is done by calling the <code>setOffset()</code> method of 
     * the GyroSensor class
     * passed in the constructor. This takes 5 seconds.
     * 
     * @see GyroSensor#setOffset()
     */
    public void startCalibration() {
        calibrating = true;
    }

    /**
     * NO FUNCTIONALITY EQUIVALENT for GyroSensor so implemented just to satisfy the <code>DirectionFinder</code> interface. 
     * Does nothing.
     */
    public void stopCalibration() {
        calibrating = false;
    }

    /**
     * This is a private thread class that is used to continously integrate sucessive readings from the gyro
     */
    private class Regulator extends Thread {
        protected Regulator() {
            setDaemon(true);
        }

        @Override
        public void run() {
            float lastDegreesPerSecond = 0F;
            long lastUpdate = System.currentTimeMillis(), now;
            float degreesPerSecond, secondsSinceLastReading;
            while (true) {
                Thread.yield();
                now = System.currentTimeMillis();
                if(now - lastUpdate<4) continue;
                degreesPerSecond=(float)gyro.getAngularVelocity();
                
                // reduce "perceived" drift since the sensor resolution is 1 deg/sec. This will increase error...
                // Ccomment or remove if this behavior is undesired. I don't know if Brent required a wandering value but
                // doing this presents better to the human observer (no perceived drift). KPT 4/7/11
                if (Math.abs(degreesPerSecond)<1.0)degreesPerSecond=0;

                // Calibration flagged...
                if(calibrating) {
                    gyro.setOffset(); // 5 seconds consumed here
                    calibrating = false;
                }

                // Integration
                secondsSinceLastReading = (float)(now - lastUpdate) / 1000F;
                heading += degreesPerSecond * secondsSinceLastReading;
                acceleration = (degreesPerSecond - lastDegreesPerSecond) / secondsSinceLastReading;

                // Move On
                lastDegreesPerSecond = degreesPerSecond;
                lastUpdate = now;
            }
        }
    }
    
}