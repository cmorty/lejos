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
 * You can then put your robot down in a mapped room, and what the Monte Carlo Localization
 * algorithm in action.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MCLTest {
	private static final int GEAR_RATIO = 3;
	private static boolean rotatingScanner = false;
	private static final RegulatedMotor HEAD_MOTOR = Motor.A;
	
	public static void main(String[] args) throws Exception {
    	//PilotProps pp = new PilotProps();
    	//pp.loadPersistentValues();
    	//float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.3"));
    	//float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "11.8"));
    	//RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	//RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	//boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));
    	
    	DifferentialPilot robot = new DifferentialPilot(5.6f,11.8f,Motor.B,Motor.C,true);
    	RangeFinder sonic = new UltrasonicSensor(SensorPort.S1);
    	RangeScanner scanner;
    	if (rotatingScanner)scanner = new RotatingRangeScanner(HEAD_MOTOR, sonic, GEAR_RATIO);
    	else scanner = new FixedRangeScanner(robot, sonic);
    	float[] angles = {-45f, 0f, 45f};
    	scanner.setAngles(angles);
    	MCLPoseProvider mcl = new MCLPoseProvider(robot,scanner,null,0,0);
    	PathController navigator = new Navigator(robot,mcl); 	
    	NXTNavigationModel model = new NXTNavigationModel();
    	model.addPilot(robot);
    	model.addNavigator(navigator);
    	model.addMCL(mcl);
	}
}
