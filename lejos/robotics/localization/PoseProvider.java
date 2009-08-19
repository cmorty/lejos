package lejos.robotics.localization;

import lejos.robotics.Pose;

/**
 * Provides the coordinate and heading info via a Pose object.
 * @author NXJ Team
 *
 */
public interface PoseProvider {
	
	public Pose getPose();
		
}
