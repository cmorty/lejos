package lejos.robotics.proposal;

import lejos.robotics.Pose;
import lejos.robotics.localization.Move;
import lejos.robotics.localization.ParticleSet;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.proposal.Movement;
import lejos.robotics.proposal.PilotListener;
import lejos.robotics.proposal.PoseProvider;

public class MCLPoseProvider implements PoseProvider, PilotListener {
	private ParticleSet particles;
	private RangeScanner scanner;
	private RangeMap map;
	
	public MCLPoseProvider(RotatePilot pilot, RangeScanner scanner, RangeMap map, int numParticles) {
		particles = new ParticleSet(map, numParticles);
		this.scanner = scanner;
		this.map = map;
		pilot.addPilotListener(this);
	}
	
	public ParticleSet getParticles() {
		return particles;
	}

	public void movementStarted(Movement event, Object p) {		
	}

	public void movementStopped(Movement event, Object p) {
		Move mv = new Move(event.getAngleTurned(), event.getDistanceTraveled());
		particles.applyMove(mv);
	}

	public Pose getPose() {
		RangeReadings rr = scanner.getRangeValues();
		if (!rr.incomplete()) {
			particles.calculateWeights(rr, map);
			particles.resample(); // Cannot indicate robot is lost
		}
		return particles.getEstimatedPose();
	}
}
