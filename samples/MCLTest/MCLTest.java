import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.NXTNavigationModel;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.PathController;
import lejos.util.PilotProps;

public class MCLTest {
	public static void main(String[] args) throws Exception {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.3"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "11.8"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));
    	
    	DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	PathController navigator = new Navigator(robot); 	
    	NXTNavigationModel model = new NXTNavigationModel();
    	RangeFinder sonic = new UltrasonicSensor(SensorPort.S1);
    	RangeScanner scanner = new RotatingRangeScanner(Motor.A, sonic);
    	float[] angles = {-45f, 0f, 45f};
    	scanner.setAngles(angles);
    	model.addPilot(robot);
    	model.addNavigator(navigator);
    	model.addPoseProvider(navigator.getPoseProvider());
	}
}
