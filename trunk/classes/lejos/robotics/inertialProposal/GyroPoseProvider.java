package lejos.robotics.inertialProposal;

import lejos.geom.Point;
import lejos.nxt.Motor;
import lejos.nxt.addon.GyroDirectionFinder;
import lejos.robotics.Move;
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

    public void moveStopped(Move move, MoveProvider mp)
    {
        currentMove = null;
    }

    public void moveStarted(Move move, MoveProvider mp)
    {
        if(move.getMoveType() == Move.MoveType.ROTATE)
            gyro.setDegreesCartesian(pose.getHeading()); // Reset the gyro
        currentMove = move;
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
            Point tempVec = new Point(0, 0);
            Point dir = new Point(0, 0);
            long lastUpdate = System.currentTimeMillis();
            while (true)
            {
                // Check time
                Thread.yield();
                long now = System.currentTimeMillis();
                if(now - lastUpdate == 0)
                    continue;

                // Read gyro
                if(currentMove != null && currentMove.getMoveType() == Move.MoveType.ROTATE)
                    pose.setHeading(getHeading());
                dir.x = (float)Math.cos(Math.toRadians(pose.getHeading()));
                dir.y = -(float)Math.sin(Math.toRadians(pose.getHeading()));

                // Read tachos
                if(currentMove != null && currentMove.getMoveType() == Move.MoveType.TRAVEL)
                {
                    long tachoCount = driveMotor.getTachoCount();
                    long tachoDiff = tachoCount - lastTachoCount;
                    float deltaDist = (float)tachoDiff / 360F * wheelDiameter * (float)Math.PI;

                    // Position
                    tempVec.x = dir.x * deltaDist;
                    tempVec.y = dir.y * deltaDist;
                    pose.getLocation().x += tempVec.x;
                    pose.getLocation().y += tempVec.y;
                    lastTachoCount = tachoCount;
                }

                // Move on
                lastUpdate = now;
            }
        }
    }

}
