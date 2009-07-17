package lejos.robotics.proposal;

/**
 * I am unsure how to get one class to encapsulate varying movement data types like:
 * heading change, travel distance, arc angle, and arc diameter
 * 
 * Enumeration?
 * 
 * Alternate names: Movement, Movement, VectorEvent, PilotEvent
 * @author NXJ Team
 *
 */
public class Movement {
	
	public static int HEADING = 1;
	public static int TRAVEL = 2;
	public static int ARC_TRAVEL = 4;
	
	public int getMovementType() {
		return 0;
	}
	
	public int getHeadingChange() {
		return 0;
	}
	
	public float getTravelDistance() {
		return 0.0f;
	}
	
	public float getArcAngle() {
		return 0.0f;
	}
	
	public float getArcRadius() {
		return 0.0f;
	}
}
