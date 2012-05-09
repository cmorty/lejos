import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.navigation.*;

/**
 * Testing the compass pilot.
 * @author Roger Glassey
 */
public class CompassPilotTst
{

    public static void main(String[] args)
    {
        System.out.println("CompassPilot Test");
        Button.waitForAnyPress();
        CompassHTSensor compass = new CompassHTSensor(SensorPort.S3);
        CompassPilot pilot = new CompassPilot(compass, 2.25f, 4.8f, Motor.A, Motor.C);
        pilot.calibrate();
        LCD.drawInt((int) pilot.getHeading(), 4, 0, 0);
        Button.waitForAnyPress();
        pilot.rotate(90 - pilot.getCompassHeading());
        pilot.getCompass().resetCartesianZero();
        pilot.travel(20);
        LCD.drawInt((int) pilot.getMovementIncrement(), 4, 0, 1);
        LCD.drawInt((int) pilot.getCompassHeading(), 5, 8, 1);
        pilot.rotate(90);
        pilot.travel(-20);
        LCD.drawInt((int) pilot.getMovement().getDistanceTraveled(), 4, 0, 2);
        LCD.drawInt((int) pilot.getCompassHeading(), 5, 8, 2);
        Button.waitForAnyPress();
    }
}

