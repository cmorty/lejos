package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Models a movement performed by a pilot
 * 
 * @author Lawrie Griffiths
 *
 */
public class Movement {
	/**
	 * The type of  movement made in sufficient detail to allow errors
	 * in the movement to be modelled.
	 */
	public enum MovementType {TRAVEL, ROTATE, ARC};
	protected float distanceTraveled, angleTurned;
	protected MovementType movementType;
	protected float arcRadius = Float.POSITIVE_INFINITY;
	protected boolean isMoving;
	protected long timeStamp;
	
	/**
	 * Create a movement object to record a movement made by a pilot
	 * 
	 * @param type the movement type
	 * @param distance the distance traveled in pilot units
	 * @param angle the angle turned in degrees
	 * @param isMoving true iff the movement was created while the robot was moving
	 */
	public Movement(MovementType type, float distance, float angle, boolean isMoving) {
		this.movementType = type;
		this.distanceTraveled = distance;
		this.angleTurned = angle;
		this.isMoving = isMoving;
		if (Math.abs(angle) > 0.5) {
			double turnRad = Math.toRadians(angle);
			arcRadius = (float) ((double) distance / turnRad);
		}
		this.timeStamp = System.currentTimeMillis();
	}

	/**
	 * Get the distance traveled. This can be in a straight line or an arc path.
	 * 
	 * @return the distance traveled
	 */
	public float getDistanceTraveled() {
		return distanceTraveled;
	}
	
	/**
	 * Get the angle turned by a rotate or an arc operation.
	 * 
	 * @return the angle turned
	 */
	public float getAngleTurned() {
		return angleTurned;
	}
	
	/**
	 * Get the type of the movement performed
	 * 
	 * @return the movement type
	 */
	public MovementType getMovementType() {
		return movementType;
	}
	
	/**
	 * Get the radius of the arc
	 * 
	 * @return the radius of the arc
	 */
	public float getArcRadius() {
		return arcRadius;
	}
	/**
	 * Test if move was in progress
	 * 
	 * @return true iff the robot was moving when this Movement object was created
	 */
	public boolean isMoving() {
		return isMoving;
	}
}
