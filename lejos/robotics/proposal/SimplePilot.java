package lejos.robotics.proposal;

/**
 * Added non-functional SimplePilot with three methods under Lawrie's instructions. I applaud the idea that we would 
 * allow robots with limited functionality in here, but it seems too theoretical at this point. I disagree this is 
 * useful, since a Pilot MUST be able to change heading in order to travel to different places. A robot that can't 
 * change heading is useless to us in the API. All robots, whether analog steering, walking, or differential, can 
 * change heading, and thus are compatible with the Pilot interface.
 * 
 * If we do have an interface for robots that can only go forward, why not add 
 * another interface for robots that can only rotate but not move, and another interface for robots
 * that can't more or rotate? It's too abstract in my opinion, and there is nothing stopping us from decomposing
 * these interfaces later without having to deprecate methods. 
 * 
 * However: Some robots probably can't go backward. So maybe we should have Pilot or SimplePilot that can change heading, and
 * go forward only, and then a ReversablePilot that can also go backward?
 * 
 * Or maybe break down each of these movements into an interface, and then build different interfaces using these?
 * That seems to break our objects up a little too much in my opinion. Changing heading is a minimum, forward is a minimum,
 * and lets leave it at that. 
 * 
 * Question: If we had one method, move(Movement) does that help us? Or do we lose the ability to differentiate what Pilots can
 * perform which movements.
 * 
 * @author NXJ Team
 *
 */
public interface SimplePilot {
	/**
	   *Starts the  NXT robot moving  forward.
	   */
	  public void forward();

	  /**
	   * Halts the NXT robot
	   *
	   * @return The movement it just achieved?
	   */
	   public Movement stop();

	  /**
	   * true if the robot is moving 
	   * @return true if the robot is moving under power.
	   */
	  public boolean isMoving();	  
}
