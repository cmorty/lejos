package lejos.nxt.addon;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;
import lejos.robotics.DirectionFinder;

/**
 * Implementation of the DirectionFinder interface that accumulates or Integrates repeated rate-of-turn readings from a GyroSensor
 * into a continuously updated heading. This class is very similar to CompassSensor, except that the calibration methods are not necessary,
 * and the direction returned does not convey true heading (north, south, etc) but rather
 * relative heading change since the last time setDegrees() or resetCartesianZero() was called.
 * @author Brent Gardner
 */
public class GyroDirectionFinder extends GyroSensor implements DirectionFinder
{
    private float cartesianCalibrate = 0;
    private float heading = 0;
    private float accelerationDpsps;

    private boolean calibrating = false;
    private float gyroCalibration = 0F;
    private long calibrationReadingCount = 0;
    private float calibrationSum = 0F;

    private Regulator reg = new Regulator(this);

    public GyroDirectionFinder(SensorPort port)
    {
        super(port);
        reg.setDaemon(true);
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
    public float getVelocity()
    {
        return (float)readValue() - gyroCalibration;
    }

    /**
     * Returns the current rate at which the angular velocity is increasing or decreasing in degrees-per-second, per second
     * @return Angular acceleration in degrees-per-second per second.
     */
    public float getAccelerationDpsps()
    {
        return accelerationDpsps;
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
     * Begins averaging readings to find bias of gyro while at rest
     */
    public void startCalibration()
    {
        gyroCalibration = 0F;
        calibrationReadingCount = 0;
        calibrationSum = 0F;
        calibrating = true;
    }

    /**
     * Sets the bias of the gyro based on readings observed since call to startCalibration()
     */
    public void stopCalibration()
    {
        calibrating = false;
        gyroCalibration = calibrationSum / calibrationReadingCount;
    }

    /**
     * This is a private thread class that is used to continously integrate sucessive readings from the gyro
     */
    private class Regulator extends Thread
    {
        private GyroDirectionFinder parent;

        protected Regulator(GyroDirectionFinder parent)
        {
            this.parent = parent;
            setDaemon(true);
        }

        @Override
        public void run()
        {
            float lastDegreesPerSecond = 0F;
            long lastUpdate = System.currentTimeMillis();
            while (true)
            {
                long now = System.currentTimeMillis();
                if(now - lastUpdate == 0)
                    continue;
                float degreesPerSecond = (float)parent.readValue();

                // Calibration
                if(calibrating)
                {
                    calibrationSum += degreesPerSecond;
                    calibrationReadingCount++;
                }

                // Integration
                degreesPerSecond -= gyroCalibration;
                float secondsSinceLastReading = (float)(now - lastUpdate) / 1000F;
                parent.heading += degreesPerSecond * secondsSinceLastReading;
                parent.accelerationDpsps = degreesPerSecond - lastDegreesPerSecond;

                // Move On
                lastDegreesPerSecond = degreesPerSecond;
                lastUpdate = now;
                Thread.yield();
            }
        }
    }
    
}