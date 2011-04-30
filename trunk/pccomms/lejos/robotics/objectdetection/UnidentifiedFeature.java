package lejos.robotics.objectdetection;

import lejos.robotics.RangeReading;

public class UnidentifiedFeature implements DetectableFeature {

	private RangeReading rr;
	
	public UnidentifiedFeature(RangeReading rr) {
		this.rr = rr;
	}
	
	public RangeReading getRangeReading() {
		return rr;
	}
}
