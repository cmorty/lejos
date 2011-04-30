package lejos.robotics.objectdetection;

import lejos.robotics.RangeReading;

/**
 * A DetectableFeature is a map feature that is detected and reported by a FeatureDetector.
 * @author BB based on concepts by Lawrie Griffiths
 */
public interface DetectableFeature {

	// TODO: It's possible that a detector would detect the coordinate position of something, not 
	// really have the relative position to vehicle available.
	
	/**
	 * Returns the RangeReading for this particular detected feature.
	 * 
	 * @return RangeReading object containing angle and range.
	 */
	public RangeReading getRangeReading();
}
