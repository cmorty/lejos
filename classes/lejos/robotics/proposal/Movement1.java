package lejos.robotics.proposal;

/**
 * Models a movement performed by a pilot
 * 
 * @author nxj team
 *
 */
public class Movement1 {
	/**
	 * The type of  movement made in sufficient detail to allow errors
	 * in the movement to be modelled.
	 */
	public enum MovementType {ODOMETRY_TRAVEL, ODOMETRY_ROTATE, ODOMETRY_ARC};
	protected float distanceTraveled, angleTurned;
	protected MovementType movementType;
	protected float arcRadius;
	
	/**
	 * Create a movement object to record a movement made by a pilot
	 * 
	 * @param type the movement type
	 * @param distance the distance traveled in pilot units
	 * @param angle the angle turned in degrees
	 */
	public Movement1(MovementType type, float distance, float angle) {
		this.movementType = type;
		this.distanceTraveled = distance;
		this.angleTurned = angle;
		if (type == MovementType.ODOMETRY_ARC) {
			double turnRad = Math.toRadians(angle);
			arcRadius = (float) ((double) distance / turnRad);
		}
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
}
