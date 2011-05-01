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
	
	// TODO: Add timestamp to RangeReading or DetectableFeature? Will help identify velocity and vector of object. 
}
