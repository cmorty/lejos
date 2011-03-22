package lejos.nxt.addon;

import lejos.robotics.DirectionFinder;


/**
 * Implementation of the DirectionFinder interface that Integrates repeated rate-of-turn readings from a GyroSensor
 * into a continuously updated heading. This class is very similar to CompassSensor, 
 * except that the direction returned does not convey true heading (north, south, etc) but rather
 * relative heading change since the last time setDegrees() or resetCartesianZero() was called.
 * @author Brent Gardner
 */
public class GyroDirectionFinder implements DirectionFinder
{
    private float cartesianCalibrate = 0;
    private float heading = 0;
    private float acceleration;

    private Regulator reg = new Regulator();
    private GyroSensor gyro;

    /** Create a <tt>GyroDirectionFinder</tt> instance.
     * Use this constructor to assign an instantiated <tt>GyroSensor</tt> 
     * @param gyro
     */
    public GyroDirectionFinder(GyroSensor gyro)
    {
        this.gyro = gyro;
        reg.start();
    }

    /**
     * Resets the current heading to a desired value
     */
    public void setDegrees(float heading)
    {
        this.heading = heading;
    }

    /**
     * Returns the directional heading in degrees. Includes "winding",
     * so the value could be greater than 360 or less than 0
     * if the robot has done multiple rotations since the last call to resetCartesianZero()
     * @return Heading in degrees.
     */
    public float getDegrees()
    {
        return heading;
    }

    /**
     * Returns the current rate-of-turn in degrees, as read by the GyroSensor
     * @return Angular velocity in degrees.
     */
    public float getAngularVelocity()
    {
        return gyro.readRawValue();
    }

    /**
     * Returns the current rate at which the angular velocity is increasing or decreasing in degrees-per-second, per second
     * @return Angular acceleration in degrees-per-second per second.
     */
    public float getAngularAcceleration()
    {
        return acceleration;
    }

    /**
     * Returns the current rate-of-turn in degrees, as read by the GyroSensor
     * @return Heading in degrees.
     */
    public float getDegreesCartesian()
    {
        return cartesianCalibrate - getDegrees();
    }

    /**
     * Resets the current heading to a desired value
     */
    public void setDegreesCartesian(float heading)
    {
        this.heading = cartesianCalibrate - heading;
    }

    /**
     * Resets the current heading to zero
     */
    public void resetCartesianZero()
    {
        cartesianCalibrate = getDegrees();
    }

    /**
     * Calls the GyroSensor.initBias() method.
     * @see GyroSensor#initBias()
     */
    public void startCalibration() {
        gyro.initBias();
    }

    /** Does nothing since GyroSensor has auto-bias calculation.
     * @see GyroSensor#initBias()
     */
    public void stopCalibration() {
    }

    /**
     * This is a private thread class that is used to continously integrate sucessive readings from the gyro
     */
    private class Regulator extends Thread
    {
        protected Regulator()
        {
            this.setDaemon(true);
        }

        @Override
        public void run()
        {
            float lastDegreesPerSecond = 0F;
            long lastUpdate = System.currentTimeMillis();
            float degreesPerSecond;
            float secondsSinceLastReading;
            long now;
            
            while (true)
            {
                this.yield();
                now = System.currentTimeMillis();
                if(now - lastUpdate<3)
                    continue;
                degreesPerSecond = gyro.readValue();
                secondsSinceLastReading = (float)(now - lastUpdate) / 1000.0f;
                heading += degreesPerSecond * secondsSinceLastReading;
                acceleration = (degreesPerSecond - lastDegreesPerSecond) / secondsSinceLastReading;

                // Move On
                lastDegreesPerSecond = degreesPerSecond;
                lastUpdate = now;
            }
        }
    }
    
}