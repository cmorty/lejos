package lejos.robotics.inertialProposal;

import lejos.geom.Point;
import lejos.nxt.Motor;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.robotics.Move;
import lejos.robotics.Move.MoveType;
import lejos.robotics.MoveListener;
import lejos.robotics.MoveProvider;
import lejos.robotics.Pose;
import lejos.robotics.localization.PoseProvider;

public class GyroPoseProvider implements PoseProvider, MoveListener
{
    private Regulator reg = new Regulator();
    private GyroDirectionFinder gyro;
    private Motor driveMotor;
    private float wheelDiameter = 0;
    private long lastTachoCount = 0;
    private Pose pose = new Pose();
    private Move currentMove;

    public GyroPoseProvider(Motor leftMotor, float wheelDiameter, GyroDirectionFinder gyro)
    {
        this.driveMotor = leftMotor;
        this.gyro = gyro;
        this.wheelDiameter = wheelDiameter;

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

    public void moveStarted(Move event, MoveProvider mp)
    {
        synchronized(this)
        {
            if(event.getMoveType() == Move.MoveType.ROTATE)
                gyro.setDegreesCartesian(pose.getHeading()); // Reset the gyro
            currentMove = event;
        }
    }

    public void moveStopped(Move event, MoveProvider mp)
    {
        synchronized(this)
        {
            currentMove = null;
        }
    }

    public lejos.robotics.Move CurrentMove()
    {
        synchronized(this)
        {
            return currentMove;
        }
    }

    public void updateLocation(Point newLocation)
    {
        pose.setLocation(newLocation);
    }

    public void updateHeading(float heading)
    {
        gyro.setDegreesCartesian(heading);
    }

    public float getHeading()
    {
        return gyro.getDegreesCartesian();
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
            lejos.robotics.Move cm;
            while (true)
            {
                // Check time
                Thread.yield();
                cm = CurrentMove();
                if(cm == null)
                    continue;

                // Rotate or travel
                if(cm.getMoveType() == MoveType.ROTATE)
                    WaitRotateComplete();
                if(cm.getMoveType() == MoveType.TRAVEL)
                    WaitTravelComplete();
            }
        }

        private void WaitRotateComplete()
        {
            lejos.robotics.Move cm = CurrentMove();
            while(cm != null && cm.getMoveType() == MoveType.ROTATE)
            {
                pose.setHeading(getHeading());
                Thread.yield();
                cm = CurrentMove();
            }
        }

        private void WaitTravelComplete()
        {
            lejos.robotics.Move cm = CurrentMove();
            driveMotor.resetTachoCount();
            Point dir = new Point(0, 0);
            dir.x = (float)Math.cos(Math.toRadians(pose.getHeading()));
            dir.y = (float)Math.sin(Math.toRadians(pose.getHeading()));
            lastTachoCount = 0;
            while(cm != null && cm.getMoveType() == MoveType.TRAVEL)
            {
                long tachoCount = driveMotor.getTachoCount();
                long tachoDiff = tachoCount - lastTachoCount;
                float deltaDist = (float)tachoDiff / 360F * wheelDiameter * (float)Math.PI;
                pose.getLocation().x += dir.x * deltaDist;
                pose.getLocation().y += dir.y * deltaDist;

                // Move on
                lastTachoCount = tachoCount;
                Thread.yield();
                cm = CurrentMove();
            }
        }

    }
    public void setPose(Pose aPose){pose = aPose;}

}
