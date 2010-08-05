package lejos.robotics.inertialProposal;

import java.util.ArrayList;
import lejos.geom.Point;
import lejos.nxt.Sound;
import lejos.robotics.MoveListener;
import lejos.robotics.MoveProvider;
import lejos.robotics.Pose;
import lejos.robotics.TachoMotor;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MoveController;

public class DifferentialFeedbackPilot implements MoveController
{
    private ArrayList<MoveListener> moveListeners = new ArrayList<MoveListener>();
    private TachoMotor leftMotor;
    private TachoMotor rightMotor;
    private Regulator reg = new Regulator();
    private lejos.robotics.Move currentMove;
    private PoseProvider poseProvider;
    public Pose startPose = new Pose(); // Used to track where the robot was when the current move began

    // Used to store the angle/distance the robot should stop when it reaches
    public float targetAngle = Float.NaN;
    public float targetDistance = Float.NaN;
    public float totalDistanceTravelled = 0F;
    public float oldDistance = 0F;
    public int oldLeftCount = 0;
    public int oldRightCount = 0;
    public float cmPerDeg = 0;
    public float anglePerDeg = 0;
    public float thisHeading;
    public int rotationsLeft = 0;
    public float degreesLeft = 0;
    public float velocity = 0;
    public float thisDistance = 0;
    public float distanceLeft = 0;
    public int leftTarget = 0;
    public int rightTarget = 0;
    public int leftCount = 0;
    public int rightCount = 0;
    public float originalDistance = 0;
    public float x, y;

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

        leftMotor.setSpeed(720);
        rightMotor.setSpeed(720);

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
        if(Math.abs(angle) < 5F)
            return;

        // Reset orientation to known value
        startPose.getLocation().x = getPose().getX();
        startPose.getLocation().y = getPose().getY();
        startPose.setHeading(getPose().getHeading());
        currentMove = new lejos.robotics.Move(true, angle, 0);
        NotifyListeners(true);

        // Start turning
        oldLeftCount = leftMotor.getTachoCount();
        oldRightCount = rightMotor.getTachoCount();
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
        oldLeftCount = leftMotor.getTachoCount();
        oldRightCount = rightMotor.getTachoCount();
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
    
    public float getTurnMaxSpeed()
    {
        return maxTurnSpeed;
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

    private float getDistance()
    {
        x = getPose().getX();
        y = getPose().getY();
        return (float)Math.sqrt(Math.pow(x - startPose.getX(), 2) + Math.pow(y - startPose.getY(), 2));
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
                    float lastHeading = getPose().getHeading();
                    long lastSample = System.currentTimeMillis();
                    float degreesToTurn = targetAngle - lastHeading;
                    velocity = 1; // for the while loop
                    do
                    {
                        // Read values
                        Thread.yield();
                        long now = System.currentTimeMillis();
                        if(now - lastSample < 1)
                            continue;
                        thisHeading = getPose().getHeading();
                        velocity = (thisHeading - lastHeading) / (now - lastSample) / 1000F;
                        leftCount = leftMotor.getTachoCount();
                        rightCount = rightMotor.getTachoCount();
                        float degreesTurned = thisHeading - startPose.getHeading();
                        int rotationsMade = leftCount - oldLeftCount;
                        lastSample = now;

                        // Calculate number of rotations remaining
                        if(degreesTurned != 0 && rotationsMade != 0)
                            anglePerDeg = Math.abs(degreesTurned / (float)(rotationsMade));
                        degreesLeft = targetAngle - thisHeading;
                        if(Math.signum(degreesLeft) != Math.signum(degreesToTurn))
                            continue; // Never go backwards
                        if(anglePerDeg == 0)
                            rotationsLeft = degreesLeft > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                        else
                            rotationsLeft = (int)(degreesLeft / anglePerDeg);
                        if(Math.abs(rotationsLeft) <= 90)
                            continue; // Leave the last few rotations to the motor regulator

                        // Actuate motors
                        leftTarget = leftCount - rotationsLeft;
                        rightTarget = rightCount + rotationsLeft;
                        leftMotor.rotateTo(leftTarget, true);
                        rightMotor.rotateTo(rightTarget, true);
                    }
                    while(leftMotor.isMoving() || rightMotor.isMoving() || Math.abs(velocity) > 1F);
                    targetAngle = Float.NaN;
                    NotifyListeners(false);
                }

                // Regulate movement
                if(Float.isNaN(targetDistance) == false)
                {
                    originalDistance = getDistance();
                    float lastDistance = originalDistance;
                    thisDistance = lastDistance;
                    long lastSample = System.currentTimeMillis();
                    velocity = 1; // for the while loop
                    do
                    {
                        // Read values
                        Thread.yield();
                        long now = System.currentTimeMillis();
                        if(now - lastSample < 1)
                            continue;
                        thisDistance = getDistance() * Math.signum(targetDistance);
                        velocity = (thisDistance - lastDistance) / (now - lastSample) / 1000F;
                        leftCount = leftMotor.getTachoCount();
                        rightCount = rightMotor.getTachoCount();
                        int rotationsMade = leftCount - oldLeftCount;
                        totalDistanceTravelled = oldDistance + thisDistance;
                        lastSample = now;

                        // Calculate number of rotations remaining
                        if(thisDistance != 0 && rotationsMade != 0)
                            cmPerDeg = Math.abs(thisDistance / (float)(rotationsMade));
                        distanceLeft = targetDistance - thisDistance;
                        if(Math.signum(distanceLeft) != Math.signum(targetDistance))
                            continue; // If you've passed the target, just stop, don't correct
                        if(cmPerDeg == 0)
                            rotationsLeft = distanceLeft > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                        else
                            rotationsLeft = (int)(distanceLeft / cmPerDeg);
                        if(Math.abs(rotationsLeft) <= 90)
                            continue; // Leave the last few rotations to the motor regulator

                        // Actuate motors
                        leftTarget = leftCount + rotationsLeft;
                        rightTarget = rightCount + rotationsLeft;
                        leftMotor.rotateTo(leftTarget, true);
                        rightMotor.rotateTo(rightTarget, true);
                    }
                    while(leftMotor.isMoving() || rightMotor.isMoving() || Math.abs(velocity) > 1F);
                    targetDistance = Float.NaN;
                    NotifyListeners(false);
                }
            }
        }
    }

	public float getMovementIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

}
