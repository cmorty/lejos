package lejos.robotics.objectdetection;

import java.util.ArrayList;
import lejos.robotics.RangeReading;
import lejos.robotics.Touch;
import lejos.geom.Point;

/**
 * This class allows a touch sensor to be used as a defacto range sensor by reporting the position of the toch sensor
 * to the object detection API. The touch sensor will only notify one time when it is pressed and will not be ready to
 * notify again until the touch sensor is released.
 * 
 * @author BB
 *
 */
public class TouchFeatureDetector extends FeatureDetectorAdapter {

	private Touch touch_sensor;
	//private int x_offset, y_offset;
	private float angle = 0;
	private float range = 0;
	private static final int DELAY = 50;
	
	private ArrayList<FeatureListener> listeners = null;
	
	public TouchFeatureDetector(Touch touchSensor) {
		this(touchSensor, 0, 0);
	}
	
	public TouchFeatureDetector(Touch touchSensor, int xOffset, int yOffset) {
		super(DELAY);
		this.touch_sensor = touchSensor;
		//this.x_offset = xOffset;
		//this.y_offset = yOffset;
		
		// Calculate angle a distance of bumper from center:
		Point robot_center = new Point(0, 0);
		Point bumper_p = new Point(xOffset, yOffset);
		range = (float)robot_center.distance(xOffset, yOffset);
		angle = robot_center.angleTo(bumper_p) - 90;
	}

	public DetectableFeature scan() {
		RangeFeature rf = null;
		if(touch_sensor.isPressed()) {
			RangeReading rr = new RangeReading(angle, range);
			rf = new RangeFeature(rr);
		}
		return rf;
	}

	@Override
	protected void notifyListeners(DetectableFeature feature) {
		super.notifyListeners(feature);
		// Wait until bumper is released before continuing to prevent multiple notifications from same press:
		while(touch_sensor.isPressed());
	}
}
