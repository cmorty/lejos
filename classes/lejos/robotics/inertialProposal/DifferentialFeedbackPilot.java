package lejos.robotics.inertialProposal;

import java.util.ArrayList;
import lejos.geom.Point;
import lejos.robotics.Move;
import lejos.robotics.Move.MoveType;
import lejos.robotics.MoveListener;
import lejos.robotics.MoveProvider;
import lejos.robotics.Pose;
import lejos.robotics.TachoMotor;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pilot;

public class DifferentialFeedbackPilot implements Pilot, MoveProvider
{
    private ArrayList<MoveListener> moveListeners = new ArrayList<MoveListener>();
    private TachoMotor leftMotor;
    private TachoMotor rightMotor;
    private Regulator reg = new Regulator();
    private lejos.robotics.Move currentMove;
    private PoseProvider poseProvider;
    private Pose startPose = new Pose(); // Used to track where the robot was when the current move began

    // Used to store the angle/distance the robot should stop when it reaches
    private float targetAngle = Float.NaN;
    private float targetDistance = Float.NaN;
    private float totalDistanceTravelled = 0F;
    private float oldDistance = 0F;

    private float desiredTurnSpeed = Float.POSITIVE_INFINITY;
    private float maxTurnSpeed = Float.POSITIVE_INFINITY;
    private float desiredMoveSpeed = Float.POSITIVE_INFINITY;
    private float maxMoveSpeed = Float.POSITIVE_INFINITY;

    public DifferentialFeedbackPilot(PoseProvider poseProvider, TachoMotor leftMotor, TachoMotor rightMotor)
    {
        this(poseProvider, leftMotor, rightMotor, false);
    }

    public DifferentialFeedbackPilot(PoseProvider poseReporter, TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse)
    {
        this.poseProvider = poseReporter;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        reg.setDaemon(true);
        reg.start();
    }

    public float getRotateSpeed()
    {
        return desiredTurnSpeed;
    }

    public void setRotateSpeed(float val)
    {
        desiredTurnSpeed = val;
    }

    public float getMaxRotateSpeed()
    {
        return maxTurnSpeed;
    }

    public float getMaxTravelSpeed()
    {
        return maxMoveSpeed;
    }

    public float getTravelSpeed()
    {
        return desiredMoveSpeed;
    }

    public void setTravelSpeed(float val)
    {
        desiredMoveSpeed = val;
    }

    public Pose getPose()
    {
        return poseProvider.getPose();
    }

    public TachoMotor getLeftMotor()
    {
        return leftMotor;
    }

    public TachoMotor getRightMotor()
    {
        return rightMotor;
    }

    public lejos.robotics.Move getMovement()
    {
        return currentMove;
    }

    public void addMoveListener(MoveListener listener)
    {
        synchronized(this)
        {
            if(listener == null)
                return;
            if(moveListeners.contains(listener) == false)
                moveListeners.add(listener);
        }
    }

    private void NotifyListeners(boolean started)
    {
        synchronized(this)
        {
            for(MoveListener listener : moveListeners)
            {
                if(started)
                    listener.moveStarted(currentMove, this);
                else
                    listener.moveStopped(currentMove, this);
            }
        }
    }

    public void rotate(float angle, boolean immediateReturn)
    {
        // Reset orientation to known value
        startPose.getLocation().x = getPose().getX();
        startPose.getLocation().y = getPose().getY();
        startPose.setHeading(getPose().getHeading());
        currentMove = new lejos.robotics.Move(true, angle, 0);
        NotifyListeners(true);

        // Start turning
        leftMotor.setSpeed(720);
        rightMotor.setSpeed(720);
        if(angle < 0)
        {
            rightMotor.backward();
            try { Thread.sleep(50); } // Need to wait 50ms because of bus issues?
            catch(Exception ex) { }
            leftMotor.forward();
        }
        else
        {
            rightMotor.forward();
            try { Thread.sleep(50); } // Need to wait 50ms because of bus issues?
            catch(Exception ex) { }
            leftMotor.backward();
        }
        targetDistance = Float.NaN;
        targetAngle = startPose.getHeading() + angle;

        while(immediateReturn == false && isMoving() == true)
            Thread.yield();
    }

    public void travel(float distance, boolean immediateReturn)
    {
        // Notify listeners
        startPose.getLocation().x = getPose().getX();
        startPose.getLocation().y = getPose().getY();
        startPose.setHeading(getPose().getHeading());
        currentMove = new lejos.robotics.Move(distance, 0, true);
        NotifyListeners(true);

        // Start moving
        leftMotor.setSpeed(720);
        rightMotor.setSpeed(720);
        if(distance > 0)
        {
            leftMotor.forward();
            try { Thread.sleep(50); } // Need to wait 50ms because of bus issues?
            catch(Exception ex) { }
            rightMotor.forward();
        }
        else
        {
            leftMotor.backward();
            try { Thread.sleep(50); } // Need to wait 50ms because of bus issues?
            catch(Exception ex) { }
            rightMotor.backward();
        }
        
        targetAngle = Float.NaN;
        oldDistance = totalDistanceTravelled;
        targetDistance = distance;
        while(immediateReturn == false && isMoving() == true)
            Thread.yield();
    }

    public void forward()
    {
        leftMotor.forward();
        rightMotor.forward();
    }

    public void backward()
    {
        leftMotor.backward();
        rightMotor.backward();
    }

    public void setAngle(float degrees)
    {
        getPose().setHeading(degrees);
    }

    public void setSpeed(int speed)
    {
        desiredMoveSpeed = speed;
    }

    public float getTurnSpeed()
    {
        return desiredTurnSpeed;
    }

    public void setTurnSpeed(float speed)
    {
        desiredTurnSpeed = speed;
    }
    
    public float getTurnMaxSpeed()
    {
        return maxTurnSpeed;
    }

    public void setMoveSpeed(float speed)
    {
        desiredMoveSpeed = speed;
    }

    public float getMoveSpeed()
    {
        return desiredMoveSpeed;
    }

    public float getMoveMaxSpeed()
    {
        return maxMoveSpeed;
    }

    public float getAngle()
    {
        return getPose().getHeading();
    }

    public Point getLocation()
    {
        return getPose().getLocation();
    }

    public void setLocation(Point location)
    {
        getPose().getLocation().x = location.x;
        getPose().getLocation().y = location.y;
    }

    public float getTravelDistance()
    {
        return totalDistanceTravelled;
    }

    public void rotate(float angle)
    {
        rotate(angle, false);
    }

    public void reset()
    {
        getPose().setHeading(0);
        totalDistanceTravelled = 0;
    }

    public void travel(float distance)
    {
        travel(distance, false);
    }

    public boolean isMoving()
    {
        return Float.isNaN(targetAngle) == false || Float.isNaN(targetDistance) == false;
    }

    public void travelArc(float radius, float distance)
    {
        travelArc(radius, distance, false);
    }

    public void stop()
    {
        targetAngle = Float.NaN;
        targetDistance = Float.NaN;
        leftMotor.stop();
        rightMotor.stop();
    }
    
    public void travelArc(float radius, float distance, boolean immediateReturn)
    {
    	// TODO: Implement this
    }

    public void steer(float turnRate)
    {
        steer(turnRate, Float.POSITIVE_INFINITY);
    }

    public void steer(float turnRate, float angle)
    {
        steer(turnRate, angle, false);
    }
    
    public void steer(float turnRate, float angle, boolean immediateReturn)
    {

    }
    
    public void arc(float radius)
    {
        arc(radius, Float.POSITIVE_INFINITY, false);
    }

    public void arc(float radius, float angle)
    {
        arc(radius, angle, false);
    }

    public void arc(float radius, float angle, boolean immediateReturn)
    {
    	// TODO: Implement this
    }

    private class Regulator extends Thread
    {
        public Regulator()
        {
            setDaemon(true);
        }

        @Override
        public void run()
        {
            while(true)
            {
                // Regulate turning
                if(Float.isNaN(targetAngle) == false)
                {
                    while(Math.signum(targetAngle - startPose.getHeading()) == Math.signum(targetAngle - getPose().getHeading()))
                        Thread.yield();

                    // Move on
                    stop();
                    NotifyListeners(false);
                }

                // Regulate movement
                if(Float.isNaN(targetDistance) == false)
                {
                    float thisDistance = 0;
                    while(thisDistance < targetDistance)
                    {
                        thisDistance = (float)Math.sqrt(Math.pow(getPose().getX() - startPose.getX(), 2) + Math.pow(getPose().getY() - startPose.getY(), 2));
                        totalDistanceTravelled = oldDistance + thisDistance;
                        Thread.yield();
                    }

                    // Move on
                    NotifyListeners(false);
                    stop();
                }
            }
        }
    }

}
