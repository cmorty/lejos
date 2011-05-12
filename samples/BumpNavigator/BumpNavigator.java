
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.NavPathController;
import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;
import lejos.util.PilotProps;

import java.io.IOException;
import java.util.Random;

/**
 * BumpNavigator is a simple obstacle avoiding robot with a single destination.
 * Requires  two touch sensors.
 * Since it relies on dead reckoning to keep track of its
 * pose,  the accuracy of navigation degrades with each obstacle.  Does not
 * map the obstacles, but uses a randomized avoiding strategy..
 * Classes used:   DifferentialPilot, NavPathController
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Roger Glassey
 */
public class BumpNavigator {

    /**
     * allocates a BumpNavigator
     * @param pilot  construct this pilot first
     * @param leftTouch -  touch sensor in left side
     * @param rightTouch - touch sensor on right side
     */
    // 
    public BumpNavigator(final NavPathController aNavigator, final SensorPort leftTouch, final SensorPort rightTouch) {
        leftBump = new TouchSensor(leftTouch);
        rightBump = new TouchSensor(rightTouch);
        nav = aNavigator;
    }

    /**
     * attempt to reach a destination at coordinates x,y despite obstacles.
     * @param x
     * @param y
     */
    public void goTo(float x, float y) {
        nav.goTo(new WayPoint(x, y), true);
        while (nav.isGoing()) {
            int hit = readSensors(); // returns if obstacle is hit or travel is complete
            if (hit != 0)
            {
                nav.interrupt();  // interrupt going to destination               
                while (hit != 0)
                {
                    hit = avoid(hit);  // keep avoiding till no obstacle is hit
                }
                nav.resume(); // goint to destination
            }
        }
    }

    /**
     * @return side on which hit was detected;  0 means none.
     */
    private int readSensors() {
        if (leftBump.isPressed())return 1;
        if (rightBump.isPressed()) return -1;
        return 0;
    }

    /**
     * causes the robot to back up, turn away from the obstacle
     * returns when obstacle is cleared or if an obstacle is detected while traveling
     * @param side  on which hit was detected, 0 if avoiding was complete without hit
     * @return  side on which hit was detected during travel
     */
    public int avoid(int side) {

        if (side == 0) return 0;
        pilot.travel(-5 - rand.nextInt(5));
        int angle = 60 + rand.nextInt(60);
        pilot.rotate(-side * angle);
        pilot.travel(10 + rand.nextInt(60), true);
         side = readSensors();
        while(side == 0 && pilot.isMoving())side = readSensors();
        pilot.stop();
        return  side;  // watch for hit while moving forward
    }
    
    /**
     * test of BumpNavitator. destination is 200 cm  directly ahead.
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "14.2"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
        DifferentialPilot p = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
        NavPathController nav = new NavPathController(p);
        BumpNavigator robot = new BumpNavigator(nav, SensorPort.S1, SensorPort.S4);
        robot.pilot = p;
        System.out.println(" any button ");
        Button.waitForPress();
        robot.goTo(200, 0);
    }
    private NavPathController nav;
    private DifferentialPilot pilot;
    private Pose pose = new Pose();
    Random rand = new Random();
    TouchSensor leftBump;
    TouchSensor rightBump;
}
