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
public class Move {
	/**
	 * The type of  movement made in sufficient detail to allow errors
	 * in the movement to be modelled.
	 */
	public enum MoveType {TRAVEL, ROTATE, ARC, STOP};
	protected float distanceTraveled, angleTurned;
	protected MoveType moveType;
	protected float arcRadius = Float.POSITIVE_INFINITY;
	protected boolean isMoving;
	protected long timeStamp;
	
	/**
	 * Create a movement object to record a movement made by a pilot. This method automatically calculates the 
	 * MoveType based on the data as follows:<br>
	 * <li>(distance NOT 0) AND (angle NOT 0) --> ARC
	 * <li>(distance = 0) AND (angle NOT 0) --> ROTATE
	 * <li>(distance NOT 0) AND (angle = 0) --> TRAVEL
	 * <li>(distance = 0) AND (angle = 0) --> STOP
	 * 
	 * @param type the movement type
	 * @param distance the distance traveled in pilot units
	 * @param angle the angle turned in degrees
	 * @param isMoving true iff the movement was created while the robot was moving
	 */
	public Move(float distance, float angle, boolean isMoving) {
		this.moveType = Move.calcMoveType(distance, angle);
		this.distanceTraveled = distance;
		this.angleTurned = angle;
		this.isMoving = isMoving;
		// TODO: This works fine, but could use convertDistanceToAngle() instead here?
		if (Math.abs(angle) > 0.5) {
			double turnRad = Math.toRadians(angle);
			arcRadius = (float) ((double) distance / turnRad);
		}
		this.timeStamp = System.currentTimeMillis();
	}
	
	/**
	 * Helper method to calculate the MoveType based on distance, angle, radius parameters.
	 * 
	 * @param distance
	 * @param angle
	 * @return
	 */
	private static MoveType calcMoveType(float distance, float angle) {
		if(distance == 0 & angle == 0) return MoveType.STOP;
		else if(distance != 0 & angle == 0) return MoveType.TRAVEL;
		else if(distance == 0 & angle != 0) return MoveType.ROTATE;
		else return MoveType.ARC;
	}
	
	/**
	 * Alternate constructor that uses angle and turn radius instead. Useful for constructing arcs, but it
	 * can't represent a straight line of travel with a set distance (use the other constructor to specify distance).
	 *  This method automatically calculates the MoveType based on the data as follows:<br>
	 * <li>(radius NOT 0) AND (angle NOT 0) --> ARC
	 * <li>(radius = 0) AND (angle NOT 0) --> ROTATE
	 * <li>(radius = 0) AND (angle = 0) --> STOP
	 * <li>(radius = +infinity) AND (angle = 0) --> TRAVEL (throws IllegalArgumentException)
	 * <li>NOTE: When radius is infinity, it is impossible to calculate distance (NaN) therefore this throws exception.
	 * @param type
	 * @param isMoving
	 * @param angle
	 * @param turnRadius
	 * 
	 * @throws IllegalArgumentException when turnRadius == Float.POSITIVE_INFINITY or Float.NEGATIVE_INFINITY
	 */
	public Move(boolean isMoving, float angle, float turnRadius) throws IllegalArgumentException {
		// TODO: Sven is of the mindset that it should just calculate NaN and then the exception will be thrown in some
		// other part of the program, rather than explicitly disallowing infinity. See his Jan 4, 2009 email.
		if(turnRadius == Float.POSITIVE_INFINITY|turnRadius == Float.NEGATIVE_INFINITY) 
			throw new IllegalArgumentException("Can't use infinity in Move(boolean, float, float) constructor.");
		this.distanceTraveled = Move.convertAngleToDistance(angle, turnRadius);
		this.moveType = Move.calcMoveType(this.distanceTraveled, angle);
		this.angleTurned = angle;
		this.isMoving = isMoving;
		arcRadius = turnRadius;
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
	public MoveType getMoveType() {
		return moveType;
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
	 * @return true iff the robot was moving when this Move object was created
	 */
	public boolean isMoving() {
		return isMoving;
	}
	
	/**
	 * Static utility method for converting distance (given turn radius) into angle.
	 * @param distance
	 * @param turnRadius
	 * @return
	 */
	public static float convertDistanceToAngle(float distance, float turnRadius){
		return (float)((distance * 360) / (2 * Math.PI * turnRadius));
	}
	
	/**
	 * Static utility method for converting angle (given turn radius) into distance.
	 * @param angle
	 * @param turnRadius
	 * @return
	 */
	public static float convertAngleToDistance(float angle, float turnRadius){
		return (float)((angle * 2 * Math.PI * turnRadius) / 360);
	}
	
}
