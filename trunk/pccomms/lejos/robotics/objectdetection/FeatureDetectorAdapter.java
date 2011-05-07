package lejos.robotics.objectdetection;

import java.util.ArrayList;

/**
 * An adapter to make it easier to implement FeatureDetector classes. The scan() method is the only method 
 * which must be implemented by the actual class.
 *  
 * @author BB
 *
 */
public abstract class FeatureDetectorAdapter implements FeatureDetector {

	private ArrayList<FeatureListener> listeners = null;	
	private boolean enabled = true;
	private int delay = 0; 
	
	public FeatureDetectorAdapter(int delay) {
		this.delay = delay;
		Thread x = new MonitorThread();
		x.setDaemon(true);
		x.start();
	}
	
	public void addListener(FeatureListener l){
		if(listeners == null )listeners = new ArrayList<FeatureListener>();
		listeners.add(l);
	}

	public void enableDetection(boolean enable) {
		this.enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Thread to monitor the range finder.
	 *
	 */
	private class MonitorThread extends Thread{

		@Override
		public void run() {
			while(true) {
				// Only performs scan if detection is enabled.
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

	protected void notifyListeners(DetectableFeature feature) {
		if(listeners != null) { 
			for(FeatureListener l : listeners) {
				l.featureDetected(feature);
			}
		}
	}
	
	public abstract DetectableFeature scan();

}
