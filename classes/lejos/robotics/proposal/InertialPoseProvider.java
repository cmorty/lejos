package lejos.robotics.proposal;


import lejos.geom.Point;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.nxt.addon.TiltSensor;
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

    private Regulator reg = new Regulator(this);
    private GyroDirectionFinder gyro;
    private TiltSensor accelerometer;
    private float accelerometerCalibration = 0F;
    private boolean calibrating = false;
    private long calibrationReadingCount = 0;
    private float calibrationSum = 0F;
    private boolean moving = false;

    public InertialPoseProvider(TiltSensor accelerometer, GyroDirectionFinder gyro)
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
    	accelerometerCalibration = 0F;
        calibrationReadingCount = 0;
        calibrationSum = 0F;
        calibrating = true;
    }

    /**
     * Sets the bias of the gyro & accelerometer based on readings observed since call to startCalibration()
     */
    public void stopCalibration()
    {
    	gyro.stopCalibration();
        calibrating = false;
        accelerometerCalibration = calibrationSum / calibrationReadingCount; 
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
    public float getAcceleration()
    {
        return fixReading(accelerometer.getXAccel()) - accelerometerCalibration;
    }

    /**
     * Returns the angular velocity in degrees per second
     */
    public float getAngularVelocity()
    {
        return (float)gyro.getAngularVelocity();
    }

    /**
     * Responsible for integrating readings into position and heading
     */
    private class Regulator extends Thread
    {
        private InertialPoseProvider parent;

        public Regulator(InertialPoseProvider parent)
        {
            setDaemon(true);
            this.parent = parent;
        }

        @Override
        public void run()
        {
            Point dir = new Point(0, 0);
            long lastUpdate = System.currentTimeMillis();
            while (true)
            {
                // Check time
                Thread.yield();
                if(parent.moving == false)
                    continue;
                long now = System.currentTimeMillis();
                if(now - lastUpdate == 0)
                    continue;
                float delta = (float)(now - lastUpdate) / 1000.0F;
                
                // Read gyro
                parent.angularVelocity = parent.getAngularVelocity();
                parent.pose.setHeading(parent.pose.getHeading() + parent.angularVelocity * delta);
                dir.x = (float)Math.cos(Math.toRadians(parent.pose.getHeading()));
                dir.y = -(float)Math.sin(Math.toRadians(parent.pose.getHeading()));

                // Read accelerometer
                float acc = parent.getAcceleration();

                // Calibrate
                if(calibrating)
                {
                    calibrationSum += acc;
                    calibrationReadingCount++;
                }
                
                // Acceleration
                parent.acceleration.x = dir.x * acc;
                parent.acceleration.y = dir.y * acc;

                // Velocity
                parent.velocity.x += parent.acceleration.x * delta;
                parent.velocity.y += parent.acceleration.y * delta;

                // Position
                parent.pose.getLocation().x += parent.velocity.x * delta;
                parent.pose.getLocation().y += parent.velocity.y * delta;

                // Move on
                lastUpdate = now;
            }
        }
    }

}
