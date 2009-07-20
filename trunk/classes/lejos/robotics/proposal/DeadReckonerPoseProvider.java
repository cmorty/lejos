package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 * A PoseProvider that keeps track of coordinates using dead reckoning, by monitoring Pilot movements.
 * 
 * Provisional name: DeadReckonerPoseProvider
 * Alternate names:  DeadReckonerPoseProvider, DeadReckoner, OrienteeringPoseProvider, OdometryPoseProvider 
 * 
 */
public class DeadReckonerPoseProvider implements PoseProvider {

	/**
	 * Internally, the constructor  listens to movements from the Pilot. This allows it to keep
	 * track of all vector movements made.
	 * 
	 * @param pilot
	 */
	public DeadReckonerPoseProvider(Pilot pilot) {
		
	}
		
	public Pose getPose() {
		// TODO Auto-generated method stub
		return null;
	}

}
