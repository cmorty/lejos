package lejos.robotics;

/**
 * TODO: This should probably be an interface in case a simultaneous laser range finder can return the same values.
 * Might also want to do an abstract Scanner class which has RotationPlatform rotation logic so code isn't duplicated.
 * 
 *  TODO: What about a RangeScanner that can move up and down too?
 *  
 * @author BB
 *
 */
public class RangeScanner {

	/**
	 * TODO: Defaults to 3 scans separated by 45 degree angle? 
	 * @param range
	 * @param platform
	 */
	public RangeScanner(RangeFinder range, RotationPlatform platform) {
		this(range, platform, 3, 45);
	}
	
	/**
	 * 
	 * @param rangeFinder
	 * @param platform 
	 * @param samples The number of range samples to retrieve when getRangeValues() is called
	 * @param angleSeparation The angle between samples. Will center on 0 degrees (forward)
	 */
	public RangeScanner(RangeFinder rangeFinder, RotationPlatform rotator, int samples, int angleSeparation) {
		
	}
	
	public int [] getRangeValues() {
		// TODO: Implement the control logic of RotationPlatform for this
		return new int[0];
	}
	
}
