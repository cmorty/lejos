import java.io.IOException;
import java.util.Collection;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.Pose;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.NavPathController;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.pathfinding.PathFinder;
import lejos.robotics.pathfinding.RandomPathFinder;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.util.PilotProps;

/**
 * Test of Monte Carlo Localisation, Pose Controllers and Path finders.
 * 
 * The robot is put down in a random position in the mapped area. 
 * It makes random moves until if works out where it is. 
 * It then uses a path finder to find a route home. 
 * A pose controller is used to follow the route.
 * 
 * The robot is an robot that is supported by the DifferentialPilot class.
 * A range scanner located above the robot's center of rotation is required.
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Homer {
  private static final int BORDER = 10; 
  private static final int NUM_PARTICLES = 200;
  private static final float RANGE_READING_ANGLE = 45;
  private static final float[] ANGLES = {-RANGE_READING_ANGLE, 0, RANGE_READING_ANGLE};
  private static final int MAX_DISTANCE = 40;
  
  // Distance from ultrasonic sensor to front of robot in cm
  private static final float PROJECTION = 10.0f;
  
  private UltrasonicSensor range = new UltrasonicSensor(SensorPort.S1);
  private RangeReadings readings = new RangeReadings(3);
  
  // Array of lines for the map
  private final Line[] lines = { 
        new Line(32, 0, 32, 88), new Line(32, 88, 0, 88), 
        new Line(0, 88, 0, 340), new Line(0, 340, 95, 340), 
        new Line(95, 340, 95, 294), new Line(95, 294, 132, 294), 
        new Line(132, 294, 132, 0), new Line(132, 0, 32, 0)};
  
  private static final Rectangle bound = new Rectangle(0,0,132,340); 
  private RangeMap map = new LineMap(lines, bound);
  private DifferentialPilot pilot;
  private MCLParticleSet particles;
  private MCLPoseProvider mcl;
  private RangeScanner scanner;
  
  public static void main(String[] args) throws IOException {
    Homer simpson = new Homer();
    simpson.run();
  }
  
  public void run() throws IOException {  
    //RConsole.openBluetooth(0);
    //System.setOut(new PrintStream(RConsole.openOutputStream()));
    
   	PilotProps pp = PilotProps.loadDefaultProperties();
	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
	
	range.continuous();
    // Create the robot and MCL pose provider and get its particle set
    pilot = new DifferentialPilot( 
        wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    scanner = new FixedRangeScanner(pilot,range);
    scanner.setAngles(ANGLES);
    mcl = new MCLPoseProvider(pilot,scanner, map, NUM_PARTICLES, BORDER);
    particles = mcl.getParticles();
    particles.setDebug(true);

    // Make random moves until we know where we are
    Pose start = localize(); 
  
    // Find a route home
    Pose home = new Pose(50, 300, -90);
    PathFinder pf = new RandomPathFinder(map, readings);
    NavPathController pc = new  NavPathController(pilot, mcl);
    
    System.out.println("Located: (" + (int) start.getX() + "," + (int) start.getY() + ")");
    
    // Go home
    try {
      Collection<WayPoint> route = pf.findRoute(start, new WayPoint(home));
      
      for(WayPoint wp: route) {
        System.out.println("Go to (" + (int) wp.x + "," + (int) wp.y + ")");
        pc.goTo(wp,false);  
        Pose  pose = mcl.getPose();
        goodEstimate(pose); // Just for diagnostics
        // Pose controller should have a goTo(Pose) method to do this
        pilot.rotate(wp.getHeading() - pose.getHeading());
      }
    } catch (DestinationUnreachableException e) {
      System.out.println("Unreachable");
    }
    Button.waitForPress();
  }
  
  /**
   * Make a random move
   */
  public void randomMove() {
    float angle = (float) Math.random() * 360;
    float distance = (float) Math.random() * MAX_DISTANCE;
    
    if (angle > 180f) angle -= 360f;
    
    readings = scanner.getRangeValues();

    // Get forward range
    float forwardRange = readings.getRange(1);

    // Don't move forward if we are near a wall
    if (forwardRange < 0
        || distance + BORDER + PROJECTION < forwardRange)
      pilot.travel(distance);
    
    pilot.rotate(angle);
  }
  
  /**
   * Check if estimated pose is accurate enough
   */
  boolean goodEstimate(Pose pose) {
    int width = mcl.getErrorRect().width;
    int height = mcl.getErrorRect().height;
    System.out.println("At " + (int) pose.getX() + "," + (int)  pose.getY() + " Error: " + width + "," + height + " Weight: " + particles.getMaxWeight());
    return width < 50 && height < 50;
  }
  
  /**
   * Make random moves until the estimated pose is good enough
   */
  private Pose localize() {
    for(;;) {
      Pose pose = mcl.getPose();
      if (goodEstimate(pose)) return pose;
      else randomMove();
    }
  }
}

