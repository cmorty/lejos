package lejos.robotics.proposal;

import lejos.robotics.Movement;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * An enhanced Pilot that is capable of traveling in arcs.
 * @author NXJ Team
 *
 */
public interface ArcPilot extends BasicPilot {
	
	/**
	 * The minimum steering radius this vehicle is capable of when traveling in an arc.
	 * Theoretically this should be identical for both forward and reverse travel. In practice?
	 * 
	 * @return the radius in degrees
	 */
	public float getMinRadius();
	
	/**
	 * Set the radius of the minimum turning circle
	 * 
	 * @param radius the radius in degrees
	 */
	public void setMinRadius(float radius);

	/**
	  * Starts the  NXT robot moving along an arc with a specified radius.
	  * <p>
	  * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	  * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	  * If <code>radius</code> is zero, the robot rotates in place.
	  * <p>
	  * Postcondition: Motor speeds are unpredictable.
	  * <p>
	  * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	  * 
	  * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
	  *          side of the robot is on the outside of the turn.
	  */
	 public void arc(float radius);

	/**
	 * Moves the NXT robot along an arc with a specified radius and  angle,
	 * after which the robot stops moving. This method does not return until the robot has
	 * completed moving <code>angle</code> degrees along the arc.
	 * <p>
	 * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	 * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	 * If <code>radius</code> is zero, is zero, the robot rotates in place.
	 * <p>
	 * Robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
	 * If <code>angle</code> is positive, the robot will move travel forwards.<br>
	 * If <code>angle</code> is negative, the robot will move travel backwards.
	 * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
	 * <p>
	 * Postcondition: Motor speeds are unpredictable.
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	 * 
	 * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
	 *          side of the robot is on the outside of the turn.
	 * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
	 * @see #travelArc(float, float)
	 */
	 public Movement arc(float radius, float angle);

	/**
	 * Moves the NXT robot along an arc with a specified radius and  angle,
	 * after which the robot stops moving. This method has the ability to return immediately
	 * by using the <code>immediateReturn</code> parameter. 
	 * <p>
	 * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	 * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	 * If <code>radius</code> is zero, is zero, the robot rotates in place.
	 * <p>
	 * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
	 * If <code>angle</code> is positive, the robot will move travel forwards.<br>
	 * If <code>angle</code> is negative, the robot will move travel backwards.
	 * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
	 * <p>
	 * Postcondition: Motor speeds are unpredictable.
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	 * 
	 * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
	 *          side of the robot is on the outside of the turn.
	 * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
	 * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
	 *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
	 * @see #travelArc(float, float, boolean)
	 */
	public Movement arc(float radius, float angle, boolean immediateReturn);

	/**
	 * Moves the NXT robot a specified distance along an arc mof specified radius,
	 * after which the robot stops moving. This method does not return until the robot has
	 * completed moving <code>distance</code> along the arc. The units (inches, cm) for <code>distance</code> 
	 * must be the same as the units used for <code>radius</code>.
	 * <p>
	 * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	 * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	 * If <code>radius</code> is zero, the robot rotates in place
	 * <p>
	 * The robot will stop when it has moved along the arc <code>distance</code> units.<br> 
	 * If <code>distance</code> is positive, the robot will move travel forwards.<br>
	 * If <code>distance</code> is negative, the robot will move travel backwards.
	 * If <code>distance</code> is zero, the robot will not move and the method returns immediately.
	 * <p>
	 * Postcondition: Motor speeds are unpredictable.
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	 * 
	 * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
	 *          side of the robot is on the outside of the turn.
	 * @param distance to travel, in same units as <code>radius</code>. The sign of the distance determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
	 * @see #arc(float, float)
	 * 
	 */
	 public Movement travelArc(float radius, float distance);

	/**
	 * Moves the NXT robot a specified distance along an arc of specified radius,
	 * after which the robot stops moving. This method has the ability to return immediately
	 * by using the <code>immediateReturn</code> parameter.  
	 * The units (inches, cm) for <code>distance</code> should be the same as the units used for <code>radius</code>.
	 * <b>Warning: Your code <i>must</i> call updatePostion() when the robot has stopped, 
	 * otherwise, the robot position is lost.</b>
	 * 
	 * <p>
	 * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	 * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	 * If <code>radius</code> is zero, ...
	 * <p>
	 * The robot will stop when it has moved along the arc <code>distance</code> units.<br> 
	 * If <code>distance</code> is positive, the robot will move travel forwards.<br>
	 * If <code>distance</code> is negative, the robot will move travel backwards.
	 * If <code>distance</code> is zero, the robot will not move and the method returns immediately.
	 * <p>
	 * Postcondition: Motor speeds are unpredictable.
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
	 * 
	 * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
	 *          side of the robot is on the outside of the turn.
	 * @param distance to travel, in same units as <code>radius</code>. The sign of the distance determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
	 * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
	 *        updatePostion() when the robot has stopped. Otherwise, the robot position is lost. 
	 * @see #arc(float, float, boolean)
	 * 
	 */
	public Movement travelArc(float radius, float distance, boolean immediateReturn);
}
