import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.mapping.NXTNavigationModel;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.util.PilotProps;

public class MapTest {
	public static final float MAX_DISTANCE = 50f;
	public static final int DETECTOR_DELAY = 1000;
	
	public static void main(String[] args) throws Exception {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	final DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	final Navigator navigator = new Navigator(robot);
    	UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    	RangeFeatureDetector detector = new RangeFeatureDetector(sonic, MAX_DISTANCE, DETECTOR_DELAY); 
    	NXTNavigationModel model = new NXTNavigationModel();
    	model.addPilot(robot);
    	model.addNavigator(navigator);
    	model.addPoseProvider(navigator.getPoseProvider());
    	model.addFeatureDetector(detector);
    	detector.addListener(model);
    	detector.enableDetection(true);
    	detector.setPoseProvider(navigator.getPoseProvider());
    	
    	// Stop if an obstacle is detected
    	detector.addListener(new FeatureListener() {
			public void featureDetected(Feature feature, FeatureDetector detector) {
				if (robot.isMoving()) robot.stop();
				if (navigator.isMoving()) {
					navigator.stop();
				}			
			}		
    	});
	}
}
