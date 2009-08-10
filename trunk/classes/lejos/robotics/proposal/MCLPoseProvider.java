package lejos.robotics.proposal;

import lejos.robotics.Pose;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.proposal.Movement;
import lejos.robotics.proposal.PoseProvider;

public class MCLPoseProvider implements PoseProvider, MoveListener {
	private MCLParticleSet particles;
	private RangeScanner scanner;
	private RangeMap map;
	private boolean readingsRequired = true;
	
	public MCLPoseProvider(RotatePilot pilot, RangeScanner scanner, 
		                   RangeMap map, int numParticles, int border) {
		particles = new MCLParticleSet(map, numParticles, border);
		this.scanner = scanner;
		this.map = map;
		pilot.addMoveListener(this);
	}
	
	public MCLParticleSet getParticles() {
		return particles;
	}

	public void movementStarted(Movement event, MovementProvider p) {		
	}

	public void movementStopped(Movement event, MovementProvider p) {
		readingsRequired = true;
		particles.applyMove(event);
	}

	public Pose getPose() {
		if (readingsRequired) {
			RangeReadings rr = scanner.getRangeValues();
			readingsRequired = false;
			if (!rr.incomplete()) {
				particles.calculateWeights(rr, map);
				particles.resample(); // Cannot indicate robot is lost
			}
		}
		return particles.getEstimatedPose();
	}
}
