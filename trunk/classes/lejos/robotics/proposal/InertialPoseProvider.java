package lejos.robotics.proposal;


import lejos.geom.Point;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.nxt.addon.TiltSensor;
import lejos.robotics.DirectionFinder;
import lejos.robotics.Move;
import lejos.robotics.MoveListener;
import lejos.robotics.MoveProvider;
import lejos.robotics.Pose;
import lejos.robotics.localization.PoseProvider;

/**
 * Implementation of the MoveProvider interface that Integrates repeated readings from a GyroSensor & Accelerometer
 * into a continuously updated position & heading. 
 * @author Brent Gardner
 */
public class InertialPoseProvider implements PoseProvider, MoveListener
{
    private Pose pose;
    private Point velocity = new Point(0, 0);
    private Point acceleration = new Point(0, 0);
    private float angularVelocity = 0F;

    private Regulator reg = new Regulator();
    private DirectionFinder gyro;
    private TiltSensor accelerometer;
    private Point accelerometerCalibration = new Point(0, 0);
    private boolean calibrating = false;
    private long calibrationReadingCount = 0;
    private Point calibrationSum = new Point(0, 0);
    private boolean moving = false;

    public InertialPoseProvider(TiltSensor accelerometer, DirectionFinder gyro)
    {
        this.accelerometer = accelerometer;
        this.gyro = gyro;
        reg.start();
    }

    public void setMoveProvider(MoveProvider mp)
    {
        mp.addMoveListener(this);
    }

    public Pose getPose()
    {
        return pose;
    }

    public void moveStopped(Move move, MoveProvider mp)
    {
        moving = false;
    }

    public void moveStarted(Move move, MoveProvider mp)
    {
        moving = true;
    }

    /**
     * Begins averaging readings to find bias of gyro & accelerometer while at rest
     */
    public void startCalibration()
    {
    	gyro.startCalibration();
    	accelerometerCalibration.x = 0;
        accelerometerCalibration.y = 0;
        calibrationReadingCount = 0;
        calibrationSum.x = 0;
        calibrationSum.y = 0;
        calibrating = true;
    }

    /**
     * Sets the bias of the gyro & accelerometer based on readings observed since call to startCalibration()
     */
    public void stopCalibration()
    {
    	gyro.stopCalibration();
        calibrating = false;
        accelerometerCalibration.x = calibrationSum.x / calibrationReadingCount;
        accelerometerCalibration.y = calibrationSum.y / calibrationReadingCount;
    }

    /**
     * Correct for drift by resetting the location to a known value - be sure to call this after calibrate
     */
    public void updateLocation(Point location)
    {
        pose.setLocation(location);
    }

    /**
     * Correct for drift by resetting the heading to a known value - be sure to call this after calibrate
     */
    public void updateHeading(float heading)
    {
        pose.setHeading(heading);
    }

    /**
     * Turn an accelerometer reading into a value in meters per second squared
     */
    protected float fixReading(int val)
{
        if(val > 512) val -= 1024;
        return -(float)val / 200.0F * 9.8F;
    }

    /**
     * Returns the current acceleration in meters per second squared
     */
    public float getXAcceleration()
    {
        return fixReading(accelerometer.getXAccel()) - accelerometerCalibration.x;
    }

    /**
     * Returns the current acceleration in meters per second squared
     */
    public float getZAcceleration()
    {
        return fixReading(accelerometer.getZAccel()) - accelerometerCalibration.y;
    }

    /**
     * Returns the angular velocity in degrees per second
     */
    public float getAngularVelocity()
    {
        return gyro.getAngularVelocity();
    }

    /**
     * Responsible for integrating readings into position and heading
     */
    private class Regulator extends Thread
    {
        public Regulator()
        {
            setDaemon(true);
        }

        @Override
        public void run()
        {
            Point dir = new Point(0, 0);
            long lastUpdate = System.currentTimeMillis();
            Point acc = new Point(0, 0);
            while (true)
            {
                // Check time
                Thread.yield();
                if(moving == false)
                    continue;
                long now = System.currentTimeMillis();
                if(now - lastUpdate == 0)
                    continue;
                float delta = (float)(now - lastUpdate) / 1000.0F;
                
                // Read gyro
                angularVelocity = getAngularVelocity();
                pose.setHeading(pose.getHeading() + angularVelocity * delta);
                dir.x = (float)Math.cos(Math.toRadians(pose.getHeading()));
                dir.y = -(float)Math.sin(Math.toRadians(pose.getHeading()));

                // Read accelerometer
                acc.x = getXAcceleration();
                acc.y = getZAcceleration();
                if(calibrating)
                {
                    calibrationSum.x += acc.x;
                    calibrationSum.y += acc.y;
                    calibrationReadingCount++;
                }
                
                // Acceleration
                acc.unProjectWith(dir.normalize()).copyTo(acceleration);

                // Velocity
                velocity.x += acceleration.x * delta;
                velocity.y += acceleration.y * delta;

                // Position
                pose.getLocation().x += velocity.x * delta;
                pose.getLocation().y += velocity.y * delta;

                // Move on
                lastUpdate = now;
            }
        }
    }

}
