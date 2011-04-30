package lejos.robotics.objectdetection;

/**
 * A FeatureDetector is capable of detecting objects and notifying listeners when it detects something. A Feature is
 * a term for any property that can be added to map data. The FeatureListeneer is notified when an object is detected,
 * even if it has previously detected the same object.
 * @see lejos.robotics.objectdetection.FeatureListener
 * @author BB based on concepts by Lawrie Griffiths
 *
 */
public interface FeatureDetector {

	/**
	 * Adds a listener to the FeatureDetector. The FeatureListener will be notified when objects are detected. 
	 * 
	 * @param listener The FeatureListener that is notified every time a feature is detected.
	 * 
	 */
	public void addListener(FeatureListener listener);
	
	// TODO: Is null the best thing to return if it doesn't detect anything? 
	/**
	 * <p>Performs a single scan for an object and returns the results. If an object is not detected, this
	 * method returns <b>null</b>.</p>
	 * <p><i>Warning: Make sure to check for a null object before trying to read data from the returned 
	 * DetectableFeature object, otherwise your code will throw a null pointer exception.</i></p>  
	 * @return A feature it has detected. null if nothing found. 
	 */
	public DetectableFeature scan();
	
	/**
	 * Enable or disable detection of objects.
	 * 
	 * @param on true enables detection and notifications, false disables this class until it is enabled again.
	 */
	public void enableDetection(boolean on);
	
	/**
	 * Indicates if automatic scanning mode and listener notification is currently enabled. (true by default)
	 * @return true if enabled, false if not
	 */
	public boolean isEnabled();
	
}
