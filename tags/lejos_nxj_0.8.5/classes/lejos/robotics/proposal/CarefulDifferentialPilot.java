package lejos.robotics.proposal;

import lejos.robotics.RangeFinder;
import lejos.robotics.TachoMotor;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public class CarefulDifferentialPilot extends DifferentialPilot {
	private RangeFinder rangeFinder;
	private float tolerance;

	public CarefulDifferentialPilot(
			RangeFinder rangeFinder, float tolerance,
			float wheelDiameter, float trackWidth,
			TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.rangeFinder = rangeFinder;
		this.tolerance = tolerance;
	}

	@Override
	protected boolean continueMoving() {
		return (rangeFinder.getRange() > tolerance);
	}
}
