import java.io.IOException;
import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.objectdetection.*;
import lejos.util.PilotProps;

/**
 * This bumper-car example uses a FusorDetector to detect and report objects from multiple sensors. Use 
 * any pilot robot with an ultrasonic sensor plugged into port 4, and a touch sensor plugged into port 2.
 * The touch sensor is the bumper for detecting objects the ultrasonic fails to detect.
 * Make sure to set the proper pilot parameters in the constructor for your specific robot.
 * @author BB
 *
 */
public class FusorBumper implements FeatureListener {

	private static final int MAX_DETECT = 50;
	private static final int RANGE_READING_DELAY = 500;
	private static final int TOUCH_X_OFFSET = -4;
	private static final int TOUCH_Y_OFFSET = 16;

	private DifferentialPilot robot;

	public FusorBumper() throws IOException {
		PilotProps pp = new PilotProps();
		pp.loadPersistentValues();
		float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.32"));
		float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.35"));
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));

		robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
		robot.forward();
	}

	public static void main(String[] args ) throws Exception {
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		FeatureDetector usdetector = new RangeFeatureDetector(us, MAX_DETECT,RANGE_READING_DELAY);

		Touch ts = new TouchSensor(SensorPort.S2);
		FeatureDetector tsdetector = new TouchFeatureDetector(ts, TOUCH_X_OFFSET, TOUCH_Y_OFFSET); 

		FusorDetector fusion = new FusorDetector();
		fusion.addDetector(tsdetector);
		fusion.addDetector(usdetector);

		fusion.addListener(new FusorBumper());

		Button.waitForPress();
	}

	public void featureDetected(Feature feature, FeatureDetector detector) {
		detector.enableDetection(false);
		robot.travel(-MAX_DETECT + feature.getRangeReading().getRange()); // go back relative to distance from the feature
		robot.rotate(90 * Math.random());
		detector.enableDetection(true);
		robot.forward();
	}
}
