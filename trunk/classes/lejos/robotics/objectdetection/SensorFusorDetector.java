package lejos.robotics.objectdetection;

import java.util.ArrayList;

/**
 * <p>If you have a robot with multiple sensors (touch and range) and would like them to report to one
 * listener, or if you want to control them at the same time (such as disabling them all at once) you can
 * use this class.</p>
 * 
 * <p>This class maintains its own thread for checking the FeatureDetectors.</p>
 * 
 * @author BB
 *
 */
public class SensorFusorDetector extends FeatureDetectorAdapter {

	private ArrayList<FeatureDetector> detectors = null;
	
	public SensorFusorDetector() {
		// TODO: What about different delays from different sensors? Touch is 50 ms, 
		// while ultrasonic is 250 ms. Maybe treat touch sensors different from others?
		// Also, scans() are blocking, so this would add up if one thread doing it. 
		// SOLUTION: Use listeners. Get max scan time (e.g. 250 ms) and wait that long for each listener to
		// report in before reporting all at once.
		super(250);
	}
	
	public void addDetector(FeatureDetector detector) {
		if(detectors == null) detectors = new ArrayList<FeatureDetector>();
		if(!detectors.contains(detector)) detectors.add(detector);
	}
	
	public DetectableFeature scan() {
		// TODO Auto-generated method stub
		return null;
	}

}
