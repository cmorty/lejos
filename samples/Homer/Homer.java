import java.awt.Rectangle;
import java.io.PrintStream;
import java.util.Collection;
import lejos.geom.Line;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Pose;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.proposal.ArcPoseController;
import lejos.robotics.proposal.DestinationUnreachableException;
import lejos.robotics.proposal.DifferentialPilot;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.proposal.MapPathFinder;
import lejos.robotics.proposal.PathFinder;
import lejos.robotics.proposal.PoseController;
import lejos.robotics.proposal.WayPoint;

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
 * @author Lawrie Griffiths
 *
 */
public class Homer implements RangeScanner {
  // Tyre diameter and wheel base
  private static final float TYRE_DIAMETER = 5.6f;
  private static final float WHEEL_BASE = 16.0f;

  private static int BORDER = 10; 
  private static int NUM_PARTICLES = 200;
  private static int MAX_RELIABLE_RANGE_READING = 150;
  private static float RANGE_READING_ANGLE = 45;
  private static int FORWARD_READING = 1;
  private static int MAX_DISTANCE = 40;
  
  // Distance from ultrasonic sensor to front of robot in cm
  private static final float PROJECTION = 10.0f;
  
  private static UltrasonicSensor range = new UltrasonicSensor(SensorPort.S1);
  private static RangeReadings readings = new RangeReadings(3);
  
  // Array of lines for the map
  private static final Line[] lines = { 
        new Line(32, 0, 32, 88), new Line(32, 88, 0, 88), 
        new Line(0, 88, 0, 340), new Line(0, 340, 95, 340), 
        new Line(95, 340, 95, 294), new Line(95, 294, 132, 294), 
        new Line(132, 294, 132, 0), new Line(132, 0, 32, 0)};
  
  private static final Rectangle bound = new Rectangle(0,0,132,340); 
  private static RangeMap map = new LineMap(lines, bound);
  private static DifferentialPilot pilot;
  private static MCLParticleSet particles;
  private static MCLPoseProvider mcl;
  
  public static void main(String[] args) {
    Homer simpson = new Homer();
    simpson.run();
  }
  
  public void run() {  
    //RConsole.openBluetooth(0);
    //System.setOut(new PrintStream(RConsole.openOutputStream()));
    
    // Create the robot and MCL pose provider and get its particle set
    pilot = new DifferentialPilot( 
        TYRE_DIAMETER, WHEEL_BASE, Motor.B, Motor.C, true);
    mcl = new MCLPoseProvider(pilot,this, map, NUM_PARTICLES, BORDER);
    particles = mcl.getParticles();
    particles.setDebug(true);
    
    // Make random moves until we know where we are
    Pose start = localize(); 
  
    // Find a route home
    Pose home = new Pose(50, 300, -90);
    PathFinder pf = new MapPathFinder(map, readings);
    PoseController pc = new  ArcPoseController(pilot, mcl);
    
    System.out.println("Located: (" + (int) start.getX() + "," + (int) start.getY() + ")");
    
    // Go home
    try {
      Collection<WayPoint> route = pf.findRoute(start, home);
      
      for(WayPoint wp: route) {
        System.out.println("Go to (" + (int) wp.x + "," + (int) wp.y + ")");
        Pose pose = pc.goTo(wp);
        goodEstimate(pose); // Just for diagnostics
        // Pose controller should have a goTo(Pose) method to do this
        pilot.rotate(wp.getHeading() - pose.getHeading());
      }
    } catch (DestinationUnreachableException e) {
      System.out.println("Unreachable");
    }
    Button.waitForPress();
  }

  @Override
  public RangeReadings getRangeValues() {
    takeReadings();
    return readings;
  }
  
  /**
   * Take a single range reading
   * 
   */
  private void takeReading(float angle, int i) {
    int rangeByte = (int) range.getRange();
    float range;

    if (rangeByte > MAX_RELIABLE_RANGE_READING) range = -1f;
    else range = ((float) rangeByte);
    readings.setRange(i,angle, range);
  }
  
  /**
   * Take a set of 3 readings
   */
  public void takeReadings() {
    // Take forward reading
    takeReading(0f, FORWARD_READING);

    // Take left reading
    pilot.rotate(-RANGE_READING_ANGLE);
    takeReading(-RANGE_READING_ANGLE, 0);

    // Take right reading
    pilot.rotate(2 * RANGE_READING_ANGLE);
    takeReading(RANGE_READING_ANGLE, 2);
    pilot.rotate(-RANGE_READING_ANGLE);
  }
  
  /**
   * Make a random move
   */
  public void randomMove() {
    float angle = (float) Math.random() * 360;
    float distance = (float) Math.random() * MAX_DISTANCE;
    
    if (angle > 180f) angle -= 360f;

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
    int width = particles.getErrorRect().width;
    int height = particles.getErrorRect().height;
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

