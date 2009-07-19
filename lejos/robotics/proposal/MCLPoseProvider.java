package lejos.robotics.proposal;

import lejos.robotics.RotationPlatform;
import lejos.robotics.Pose;

public class MCLPoseProvider implements PoseProvider {

	/**
	 * 
	 * Q1. My version of MCL used a pilot to turn the robot, so there are alternatives. 
	 * 
	 * There is also the issues of when to take the sensor readings. The only place a 
	 * PoseProvider could do it is when getPose is called and that could be inappropriate or very inefficent. 

	 * 
	 * Actually not a RotationPlatform, should be scanner. Not done yet.
	 * @param pilot
	 * @param rp
	 */
	public MCLPoseProvider(Pilot pilot, RotationPlatform rp) {
		
	}
	
	public Pose getPose() {
		// TODO Auto-generated method stub
		return null;
	}

}
