import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.NXTNavigationModel;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.PathController;
import lejos.util.PilotProps;

/**
 * Used with MCLTest PC sample.
 * Change the parameters to suit your robot.
 * You can run the PilotParams sample to set up the parameters for DifferentialPilot.
 * 
 * Run this sample on the NXT, then run MCLTest on the PC and connect to the NXT.
 * 
 * This sample tests global localization. You can then put your robot down anywhere in a mapped room,
 * and watch the Monte Carlo Localization algorithm in action as it determines the location of the robot.
 * 
 * The robot must use differential steering (unless you change the pilot used) and have an ultrasonic sensor
 * for taking range readings to walls and other features. a fixed or rotating range scanner can be used. In the fixed case,
 * the pilot rotates the robot to take range readings at different angles. In the rotating case, the ultrasonic
 * sensor must be mounted on a rotating platform.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MCLTest {
	private static final int GEAR_RATIO = -12;
	private static final boolean ROTATING_RANGE_SCANNER = true;
	private static final RegulatedMotor HEAD_MOTOR = Motor.A;
	private static final float[] ANGLES = {-45f,0f,45f};
	private static final int BORDER = 0;
	private static final double ROTATE_SPEED = 100f;
	private static final double TRAVEL_SPEED = 100f;
	
	public static void main(String[] args) throws Exception {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));
    	
    	DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	robot.setRotateSpeed(ROTATE_SPEED);
    	robot.setTravelSpeed(TRAVEL_SPEED);
    	RangeFinder sonic = new UltrasonicSensor(SensorPort.S1);
    	RangeScanner scanner;
    	if (ROTATING_RANGE_SCANNER)scanner = new RotatingRangeScanner(HEAD_MOTOR, sonic, GEAR_RATIO);
    	else scanner = new FixedRangeScanner(robot, sonic);
    	scanner.setAngles(ANGLES);
    	// Map and particles will be sent from the PC
    	MCLPoseProvider mcl = new MCLPoseProvider(robot, scanner, null, 0, BORDER);
    	PathController navigator = new Navigator(robot, mcl); 	
    	NXTNavigationModel model = new NXTNavigationModel();
    	model.addPilot(robot);
    	model.addNavigator(navigator);
    	model.addMCL(mcl);
	}
}
