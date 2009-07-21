package lejos.robotics.proposal;

import lejos.robotics.*;

public class MCLPoseProvider implements PoseProvider {

	/**
	 * Range scan enumeration. Could make actual Java enumeration here 
	 */
	public int EVERY_MOVEMENT = 0;
	public int EVERY_READING = 1;
	// TODO Add a few options for how often to take range scan
	
	/**
	 *
	 * 
	 * @param pilot
	 * @param rp
	 */
	public MCLPoseProvider(Pilot pilot, RangeScanner scanner) {
		
	}
	
	/**
	 * A proposed method to allow user to determine how often they want this class
	 * to perform a scan. Could use constants at top of class.
	 * 
	 * @param scanType
	 */
	public void setScanFrequency(int scanType) {
		
	}
	
	public Pose getPose() {
		// TODO Auto-generated method stub
		return null;
	}

}
