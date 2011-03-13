package lejos.robotics.objectdetection;

import java.util.ArrayList;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;

/**
 * The UnidentifiedObjectDetector used a RangeFinder to locate objects (known as features when mapping). This class is
 * unable to identify the feature and merely reports the range and angle to the object.
 * @author BB
 *
 */
public class UnidentifiedFeatureDetector implements FeatureDetector {
	
	private ArrayList<FeatureListener> listeners = null;
	private RangeFinder range_finder = null;
	private float max_dist = 100;
	private int delay = 0;
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

	// TODO: Notify with DetectableFeature instead?
	private void notifyListeners(RangeReading rr) {
		if(listeners != null) { 
			for(FeatureListener l : listeners) {
				l.featureDetected(rr);
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
				
				float range = range_finder.getRange();
				if(range > 0 & range < max_dist) {
					int angle = 0;
					RangeReading rr = new RangeReading(angle, range);
					notifyListeners(rr);
				}
				
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
