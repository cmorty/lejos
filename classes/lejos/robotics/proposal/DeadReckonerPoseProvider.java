package lejos.robotics.proposal;

import lejos.robotics.Pose;

/**
 * A PoseProvider that keeps track of coordinates using dead reckoning, by monitoring Pilot movements.
 * 
 * Question: What about a robot that is helped along with a Compass? Should the compass go in a DeadReckoner
 *  constructor, or a Pilot constructor? The PoseProvider seems more logical but Lawrie wants it to only
 *  accept a Pilot. 
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
