import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.*;
import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;
import lejos.util.PilotProps;
import java.io.IOException;
import java.util.Random;

/**
 * <p>BumpNavigator is a simple obstacle avoiding robot that randomly travels to 
 * locations within a 2m x 2m space (you can enlarge or shrink this with the 
 * AREA_WIDTH and AREA_LENGTH constants). Press the button after each waypoint
 * is reached to make it travel to a new waypoint.</p>  
 * 
 * <p>The robot requires  two touch sensors, the left in port 2 and the right in 
 * port 3. Since it relies on dead reckoning to keep track of its
 * pose,  the accuracy of navigation degrades with each obstacle. It does not
 * map the obstacles, but instead uses a randomized avoiding strategy.</p>
 * 
 * <p>Classes used:   DifferentialPilot, NavPathController, FeatureDetector</p>
 * 
 * <p>You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.</p>
 * 
 * @author Roger Glassey
 */
public class BumpNavigator implements FeatureListener {

	public static int AREA_WIDTH = 200;
	public static int AREA_LENGTH = 200;
	
	public static int LEFT_SIDE = 1;
	public static int RIGHT_SIDE = -1;
	
	private WayPoint target;
	
    /**
     * allocates a BumpNavigator
     * @param pilot  construct this pilot first
     * @param leftTouch -  touch sensor in left side
     * @param rightTouch - touch sensor on right side
     */
    // 
    public BumpNavigator(final NavPathController aNavigator, final SensorPort leftTouch, final SensorPort rightTouch) {
        TouchSensor leftBump = new TouchSensor(leftTouch);
        TouchSensor rightBump = new TouchSensor(rightTouch);
        
        // Create object detection for each bumper and add BumpNavigator as a listener:
        FeatureDetector fd1 = new TouchFeatureDetector(leftBump, -4.5, 10);
        fd1.addListener(this);
        FeatureDetector fd2 = new TouchFeatureDetector(rightBump, 4.5, 10);
        fd2.addListener(this);
        
        nav = aNavigator;
    }
    
    public void goTo(double x, double y) {
    	target = new WayPoint(x, y);
    	nav.goTo(target);
    }
    
    /**
     * test of BumpNavitator. destination is 200 cm  directly ahead.
     * @param args
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.32"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.35"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
        DifferentialPilot p = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
        NavPathController nav = new NavPathController(p);
        
        BumpNavigator robot = new BumpNavigator(nav, SensorPort.S2, SensorPort.S3);
        robot.pilot = p;
        
        // TODO: For version 1.0. 
        // This is overly complex to make NavPathController and a FeatureListener do something simple like
        // a bumper car. Might want to look at ways to change API so we can simplify this type of sample. Coding
        // this type of example is not very intuitive with the current API.
        
        // Repeatedly drive to random points:
        while(!Button.ESCAPE.isPressed()) {
        	System.out.println("Target: ");
        	double x_targ = Math.random() * AREA_WIDTH;
        	double y_targ = Math.random() * AREA_LENGTH;
        	System.out.println("X: " + (int)x_targ);
        	System.out.println("Y: " + (int)y_targ);
        	System.out.println("Press ENTER key");
        	Button.ENTER.waitForPressAndRelease();
        	
        	// When an obstacle is encountered and stop() is called, the method goTo() returns 
        	// even though it didn't reach the target waypoint... 
	        robot.goTo(x_targ, y_targ);
	        
	        // ...therefore this delay is needed.
	        while(p.isMoving())
	        	Thread.sleep(500);
	        
	        // Output arrival:
	        Pose curPose = nav.getPoseProvider().getPose();
	        System.out.println("Arrived: " + (int)curPose.getX() + ", " + (int)curPose.getY());
	        Sound.beepSequenceUp();
        }
    }
    private NavPathController nav;
    private DifferentialPilot pilot;
    Random rand = new Random();
        
    /**
     * causes the robot to back up, turn away from the obstacle.
     * returns when obstacle is cleared or if an obstacle is detected while traveling.
     */
	public void featureDetected(Feature feature, FeatureDetector detector) {
		detector.enableDetection(false);
		Sound.beepSequence();
		int side = 0;
		
		// Identify which bumper was pressed:
		if(feature.getRangeReading().getAngle() < 0)
			side = LEFT_SIDE;
		else
			side = RIGHT_SIDE;
	
		// Perform a movement to avoid the obstacle.
	    pilot.travel(-5 - rand.nextInt(5));
	    int angle = 60 + rand.nextInt(60);
	    pilot.rotate(-side * angle);
	    detector.enableDetection(true);
	    pilot.travel(10 + rand.nextInt(60));
	    nav.goTo(target);
	}
}