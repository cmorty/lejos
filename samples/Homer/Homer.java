import java.util.ArrayList;
import java.util.Collection;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.Pose;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.pathfinding.ShortestPathFinder;
import lejos.util.PilotProps;
import java.io.IOException;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.localization.*;//MCLPoseProvider;
import lejos.nxt.comm.RConsole;
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
 * @author Lawrie Griffiths, modified by Roger Glassey
 *
 */
public class Homer {
  // Tyre diameter and wheel base
 
  private static final int BORDER = 10; 
  private static final int NUM_PARTICLES = 200;
  private static final int MAX_RELIABLE_RANGE_READING = 180;
  private static final float RANGE_READING_ANGLE = 45;
  private static final int FORWARD_READING = 1;
  private static final int MAX_DISTANCE = 80;
  
  // Distance from ultrasonic sensor to front of robot in cm
  private static final float PROJECTION = 10.0f;
  
  private UltrasonicSensor range = new UltrasonicSensor(SensorPort.S1);
  private RangeReadings readings = new RangeReadings(3);
  
  // Array of lines for the map
 private final Line[] lines = {
//        new Line(32, 0, 32, 88), new Line(32, 88, 0, 88),
//        new Line(0, 88, 0, 340), new Line(0, 340, 95, 340),
//        new Line(95, 340, 95, 294), new Line(95, 294, 132, 294),
//        new Line(132, 294, 132, 0), new Line(132, 0, 32, 0)
          new Line(0,0,225,0), new Line(0,0,0,85) ,
          new Line(225,0, 225,132), new Line(225,132, 55, 132),
          new Line(0,90, 55,85), new Line(55,85,55,132)
 };
  
  private static final Rectangle bound = new Rectangle(0,0,225,132); 
  private RangeMap map = new LineMap(lines, bound);
  private DifferentialPilot pilot;
  private MCLParticleSet particles;
  private MCLPoseProvider mcl;
  private lejos.robotics.localization.MCLPoseProvider  mcl1;
  private RangeScanner scanner;
  private static Pose pose;
  private static boolean debug;

  public static void main(String[] args)throws IOException  {
       
    Homer simpson = new Homer();
    debug = true;
    simpson.run();
  }
  
  public Homer()
  {
        PilotProps pp = new PilotProps();
        try {
            pp.loadPersistentValues();
            float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
            float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "14.4"));
            RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
            RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
            boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE, "false"));
            pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
        } catch (IOException e) {  }
        UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
        scanner = scanner = new FixedRangeScanner(pilot,sonar);
        float[] angles = {-RANGE_READING_ANGLE, 0, RANGE_READING_ANGLE};
        scanner.setAngles(angles);
        mcl = new MCLPoseProvider(pilot, scanner, map, NUM_PARTICLES, BORDER);
        mcl.generateParticles();
        particles = mcl.getParticles();
        pilot.setTravelSpeed(30);
        range.continuous();
    }
  
  
  public void run() {  
        mcl.setDebug(debug);
     if(debug){
         RConsole.openBluetooth(0);
        System.out.println("Homer  wants console ");
        System.setOut(RConsole.getPrintStream());
      }
   
    Pose start = localize();
//    Pose start = new Pose(65,120,0);
   if(debug) System.out.println("Localize found "+start);
    // Find a route home
    Pose home = new Pose(30, 30, -90);

    ShortestPathFinder pf = new ShortestPathFinder((LineMap)map);
     pf.lengthenLines(5);
    if(debug) System.out.println("Located: " + (int) start.getX() + "," + (int) start.getY()
            + " Heading "+start.getHeading());

    // Go home
    Collection<WayPoint> route = null;
      try {
          route = pf.findRoute(start, new WayPoint(home));
          if(debug) System.out.println(" route built ");
      } catch (DestinationUnreachableException e) {
          System.out.println("Unreachable");
      }
      ArrayList<WayPoint> rt = (ArrayList<WayPoint>) route;
      for (int i = 0; i < rt.size(); i++) {
          if(debug) System.out.println(rt.get(i).getPose());
      }
      int count = 0;
      for (WayPoint wp : route) {
          if(debug) System.out.print("Waypoint " + (int) wp.x + "," + (int) wp.y + ")");
          if (count != 0) {
              if(debug) System.out.print("Go to (" + (int) wp.x + "," + (int) wp.y + ")");
              pose = mcl.getPose();
              if(debug) System.out.println(" From " + pose + " count " + count);
              boolean goodPose = goodEstimate(pose);
              if (!goodPose) {
                  pose = localize();
              }
              if(debug) System.out.println(" good estimate " +goodPose) ;
              float angle = pose.angleTo(wp.getPose().getLocation()) - pose.getHeading();
              pilot.rotate(angle);
              if(debug) System.out.println(" rotated " + angle);
              float distance = pose.distanceTo(wp.getPose().getLocation());
              pilot.travel(distance);
              if(debug) System.out.println(" traveled " + distance);
          }
          count++;
      }
        getReadings();
        mcl.update(readings);
       pose = mcl.getPose();
    System.out.println( " at home "+pose);

  }

  public RangeReadings getRangeValues() {
//      System.out.println(" take readings called ");
    takeReadings();
    if(readings.incomplete()) if(debug) System.out.println(" INCOMPLETE ");
    return readings;
  }
  
  
  /**
   * Take a set of 3 readings
   */
  public void takeReadings() {
    scanner.getRangeValues();
  }
  
  /**
   * Make a random move
   */
   public void randomMove() {
    float angle = -180+(float) Math.random() * 360;
    while(mcl.isBusy());
    pilot.rotate(angle);
    float distance = MAX_DISTANCE * (float) Math.random();
   if(debug) System.out.print("Random Move  angle:"+angle );
    // Get forward range
    float forwardRange = scanner.getRangeFinder().getRange();
    // Don't move forward if we are near a wall
    if (forwardRange > 180) forwardRange = 30;
    if(forwardRange < 20) distance = forwardRange - 30;
     if ( distance > forwardRange -20 ) distance  = forwardRange - 30 ;
    if(debug) System.out.print(" FWD "+forwardRange);
    if(debug) System.out.println(" Distance "+distance);
//    System.out.println(" busy "+mcl.isBusy());
      pilot.travel(distance);
    }
   
  private boolean getReadings()
    {
          boolean incomplete = true;
        int count = 0;
        float cumAngle = 0;
//        System.out.println("get complete  readings ");
        do {
               readings = scanner.getRangeValues();
               incomplete = readings.incomplete();
               if(debug) System.out.println("readings incomplete "+incomplete+
                       "  count  "+count);
//               readings.printReadings();
               if (!incomplete) break;
                float randAngle = -180 + 360 * (float) Math.random();
                pilot.rotate(randAngle);
                if(debug) System.out.println(" rotate "+randAngle);
                cumAngle += randAngle;
                count++;
        } while (incomplete && count < 20);
        pilot.rotate(-cumAngle);
        return count < 5;

  }
  /**
   * gets  readings, returns false if incomplete
   * else updates from these,
   * if update fails, generates new particle set.
   * @return
   */
    private boolean updateParticles() {
        if (! getReadings()){
           if(debug) System.out.println("No Good Readings ");
           return false;
        }
        boolean updateOK = false;
//        System.out.println("Update Particles called ");
        updateOK = mcl.update(readings);
        if(debug) System.out.println(" Max weight " + mcl.getParticles().getMaxWeight());
        if (!updateOK) {
            if(debug) System.out.println(" generate particles ");
            mcl.generateParticles(); // either improbable readings
            // or resample failed+
            if(debug) System.out.println("BAD POSE Generated new particles ");
            particles = mcl.getParticles();
//            if(debug) System.out.println(" nave new particles ");
        }
        return updateOK;

    }
  /**
   * Check if estimated pose is accurate enough
   */
  boolean goodEstimate(Pose pose) {
    float sx = mcl.getSigmaX();
    float sy = mcl.getSigmaY();
    float xr = mcl.getXRange();
    float yr = mcl.getYRange();
    if(debug) System.out.println("At " + (int) pose.getX() + "," + (int)  pose.getY() +
            " H "+(int)pose.getHeading()+"  X;Y range_sigma  " + xr+ "_" + sx +
            " :_ "+yr+"_"+sy+" Weight " + particles.getMaxWeight());

    return sx <5 && sy < 5 && xr < 40 && yr < 40 ;
  }
  
  /**
   * Make random moves until the estimated pose is good enough
   */
  private Pose localize() {

     updateParticles();
     while(mcl.isBusy());

    pose = mcl.getPose();
    boolean goodEst = false;
    do
    {
    if(debug) System.out.println(" localize call getPose ");
        pose = mcl.getPose();

        goodEst = goodEstimate(pose);
        if(debug) System.out.println("Localize good estimate "+goodEst);
       if (!goodEst)
       {
          randomMove();
          while(mcl.isBusy());
          updateParticles();
        }
  } while (!goodEst);
    return pose;
    }

  public void setAngles(float[] angles) {
  }
  
}

