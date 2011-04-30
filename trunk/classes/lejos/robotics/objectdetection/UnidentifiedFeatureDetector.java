package lejos.robotics.objectdetection;

import java.util.ArrayList;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;

/**
 * <p>The UnidentifiedObjectDetector used a RangeFinder to locate objects (known as features when mapping). This class is
 * unable to identify the feature and merely reports the range and angle to the object.</p>
 * 
 * <p>To create a more complex FeatureDetector, extend this class and override the {@link FeatureDetector#scan()} method.
 * It is possible to add more complex functionality in this method, such as only returning a "hit" if the scanner detects
 * an object in the same location twice in a row. You can also have the scan identify the object (such as a camera using
 * facial recognition to identify a person) and then report that in a DetectableFeature object that extends UnidentifiedFeature.</p>
 *  
 * @author BB based on concepts by Lawrie Griffiths
 *
 */
public class UnidentifiedFeatureDetector implements FeatureDetector {
	
	private ArrayList<FeatureListener> listeners = null;
	private RangeFinder range_finder = null;
	private float max_dist = 100;
	private int delay = 0;
	private boolean enabled = true;
	// TODO: Accept optional RangeScanner?
	
	/**
	 * If a range finder is used, assumes object is detected straight ahead so heading is 
	 * always 0 for the returned RangeReading. 
	 * @param rf The range finder sensor. e.g. UltrasonicSensor
	 * @param maxDistance The upper limit of distance it will report. e.g. 40 cm.
	 * @param delay The interval range finder checks for objects. e.g. 250 ms.
	 * @see lejos.nxt.UltrasonicSensor
	 */
	public UnidentifiedFeatureDetector(RangeFinder rf, float maxDistance, int delay) {
		this.range_finder = rf;
		setMaxDistance(maxDistance);
		this.delay = delay;
		Thread x = new MonitorThread();
		x.setDaemon(true);
		x.start();
	}

	/**
	 * Sets the maximum distance to register detected objects from the range finder.
	 * @param distance The maximum distance. e.g. 40 cm.
	 */
	public void setMaxDistance(float distance) {
		this.max_dist = distance;
	}
	
	/**
	 * Returns the maximum distance the FeatureDetector will return for detected objects. 
	 * @return The maximum distance. e.g. 40 cm.
	 */
	public float getMaxDistance(){
		return this.max_dist;
	}
	
	public void addListener(FeatureListener l){
		if(listeners == null )listeners = new ArrayList<FeatureListener>();
		listeners.add(l);
	}

	private void notifyListeners(DetectableFeature feature) {
		if(listeners != null) { 
			for(FeatureListener l : listeners) {
				l.featureDetected(feature);
			}
		}
	}
	
	/**
	 * TODO: Thread to monitor the range finder. If we go Listener API with RangeSensor, this thread will no longer be necessary.
	 *
	 */
	private class MonitorThread extends Thread{

		public void run() {
			while(true) {
				/* TODO: Andy has a suggestion of moving the code in this thread out into the API so that users could override
				this if they want to use multiple sensors or other types of sensors. Use scan() method. */
				DetectableFeature f = (enabled?scan():null);
				if(f != null) notifyListeners(f);
				
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void enableDetection(boolean enable) {
		this.enabled = enable;
	}

	public DetectableFeature scan() {
		UnidentifiedFeature feature = null;
		
		float range = range_finder.getRange();
		if(range > 0 & range < max_dist) {
			int angle = 0;
			feature = new UnidentifiedFeature(new RangeReading(angle, range));
			
		}
		return feature;
	}
}
