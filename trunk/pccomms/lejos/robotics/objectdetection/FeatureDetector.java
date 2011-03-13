package lejos.robotics.objectdetection;

/**
 * A FeatureDetector is capable of detecting objects and notifying listeners when it detects something. A Feature is
 * a term for any property that can be added to map data. The FeatureListeneer is notified when an object is detected,
 * even if it has previously detected the same object.
 * @see lejos.robotics.objectdetection.FeatureListener
 *
 */
public interface FeatureDetector {

	/**
	 * Adds a listener to the FeatureDetector. The FeatureListener will be notified when objects are detected. 
	 * 
	 * @param listener The FeatureListener that is notified every time a feature is detected.
	 * 
	 */
	public abstract void addListener(FeatureListener listener);
	
}
