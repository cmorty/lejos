package lejos.robotics.proposal;

/**
 * This enumeration represents movement data. 
 * 
 * I am unsure how to get one class to encapsulate varying movement data types like:
 * heading change, travel distance, arc angle/distance, and arc radius
 * 
 * Alternate names: Movement, Movement, VectorEvent, PilotEvent
 * @author BB
 *
 */
public enum Movement {
	
	HEADING, TRAVEL, ARC_TRAVEL;
	
	private float distance;
	private int heading_change;
	private int radius;
	private float arc_distance;
	
	public int getHeadingChange() {
		return heading_change;
	}
	
	public void setHeadingChange(int angle) {
		this.heading_change = angle;
	}
	
	public float getTravelDistance() {
		return distance;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public float getArcRadius() {
		return radius;
	}
	
	public void setArcRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	 * 
	 * Alternate: Use angle (degrees) instead of distance (e.g. cm)
	 * @return
	 */
	public float getArcDistance() {
		return arc_distance;
	}
	
	public void setArcDistance(float distance) {
		this.arc_distance = distance;
	}
}
