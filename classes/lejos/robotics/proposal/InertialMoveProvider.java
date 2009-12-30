package lejos.robotics.proposal;

import java.util.ArrayList;

import lejos.geom.Point;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.nxt.addon.TiltSensor;
import lejos.robotics.proposal.Pose1;

/**
 * NOTE: This class was originally deleted and resubmitted in case it is beneficial to have an InertialMoveProvider that
 * works with DeadReckonerPoseProvider. Currently it does not because it uses Roger's temporary classes. It will need some
 * cleanup before it will actually work with DeadReckonerPoseProvider. - Brian
 * Implementation of the MoveProvider interface that Integrates repeated readings from a GyroSensor & Accelerometer
 * into a continuously updated position & heading. 
 * @author Brent Gardner
 */
public class InertialMoveProvider implements MoveProvider1
{
    private ArrayList<Pose1> listeners = new ArrayList<Pose1>();

    private Point position = new Point(0, 0);
    private Point velocity = new Point(0, 0);
    private Point acceleration = new Point(0, 0);
    private float orientation = 0F;
    private float angularVelocity = 0F;

    private Regulator reg = new Regulator(this);
    private GyroDirectionFinder gyro;
    private TiltSensor accelerometer;
    private float accelerometerCalibration = 0F;
    private boolean calibrating = false;
    private long calibrationReadingCount = 0;
    private float calibrationSum = 0F;

    public InertialMoveProvider(TiltSensor accelerometer, GyroDirectionFinder gyro)
    {
        this.accelerometer = accelerometer;
        this.gyro = gyro;
        reg.start();
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
        position.x = location.x;
        position.y = location.y;
    }

    /**
     * Correct for drift by resetting the heading to a known value - be sure to call this after calibrate
     */
    public void updateHeading(float heading)
    {
        orientation = heading;
    }

    /**
     * Add a pose to the list of poses to be updated by this class
     */
    public void addPose(Pose1 listener)
    {
        synchronized(this)
        {
            if(listener == null)
                return;
            if(listeners.contains(listener))
                return;
            listeners.add(listener);
        }
    }
    
    /**
     * Update the pose information
     */
    public void updatePose()
    {
    	// Pose is always being updated?
    }

    /**
     * Remove a pose from the list of poses to be updated by this class
     */
    public void removePose(Pose1 listener)
    {
        synchronized(this)
        {
            if(listeners.contains(listener) == false)
                return;
            listeners.remove(listener);
        }
    }

    /**
     * Turn an accelerometer reading into a value in meters per second squared
     */
    protected float FixReading(int val)
{
        if(val > 512) val -= 1024;
        return -(float)val / 200.0F * 9.8F;
    }

    /**
     * Returns the current acceleration in meters per second squared
     */
    public float getAcceleration()
    {
        return FixReading(accelerometer.getXAccel()) - accelerometerCalibration;
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
        private InertialMoveProvider parent;

        public Regulator(InertialMoveProvider parent)
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
                long now = System.currentTimeMillis();
                if(now - lastUpdate == 0)
                    continue;
                float delta = (float)(now - lastUpdate) / 1000.0F;
                
                // Read gyro
                parent.angularVelocity = parent.getAngularVelocity();
                parent.orientation += parent.angularVelocity * delta;
                dir.x = (float)Math.cos(Math.toRadians(parent.orientation));
                dir.y = -(float)Math.sin(Math.toRadians(parent.orientation));

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
                parent.position.x += parent.velocity.x * delta;
                parent.position.y += parent.velocity.y * delta;

                // Notify listeners
                for(Pose1 pose : parent.listeners)
                {
                    pose.setLocation(parent.position);
                    pose.setHeading(parent.orientation);
                }

                // Move on
                lastUpdate = now;
                Thread.yield();
            }
        }
    }

}
