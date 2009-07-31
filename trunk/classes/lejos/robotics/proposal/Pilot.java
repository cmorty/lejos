package lejos.robotics.proposal;

/**
 * 
 * The Pilot is theoretically there to perform vector movements, such as 
forward(), travel(100) and setHeading(90). The vehicle type should really be 
largely inconsequential to this interface (of course *implementations* like 
DifferentialPilot are vehicle specific). After all, robots are really just 
objects with vectors. As long as Pilot handles different vectors (including 
arc, which isn't a vector per se) we should be good to handle any moving 
object, no matter what the design specifics. I think we should make Pilot 
heavily vector oriented in the Javadoc language and method names.

 * @author NXJ Team
 */
public interface Pilot extends UnidirectionalPilot {

	/**
	 * Adds a PilotListener that will be notified of all Pilot movement events.
	 * @param p
	 */
	public void addPilotListener(PilotListener listener);
	
	  
	  /**
	   *Starts the  NXT robot moving  backward .
	   */
	  public void reverse();

	  /**
	   * There is no guarantee that after you call these methods it is actually capable of completing the full movement.
	   *  Many things might prevent the movement from being completed, and the API should be informed about what 
	   *  *actual* movement occurred, to the best of the Pilot's ability. For example, if the Pilot is a walker, 
	   *  perhaps it can only move forward in increments of 5 cm. So if you tell it to move 17 cm, perhaps it only moves 
	   *  15 cm and then returns. Or maybe it can only rotate in increments of 15. It should report the actual movement 
	   *  it completed.
	   *  
	   *  Furthermore, I'm not sure if the integrity of the API is compromised with this method, since a robot that isn't
	   *  capable of moving backward might be asked to do so with this method. Will need to consider this more.
	   *  
	   * @param vector
	   * @return
	   */
	  public Movement move(Movement vector);
	  
	  /**
	   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
	   * Method returns when movement is done.
	   * 
	   * @param distance The positive or negative distance to move the robot.
	   */
	  public Movement travel(float distance);

	  /**
	   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
	   * @param distance The positive or negative distance to move the robot, in wheel diameter units.
	   * @param immediateReturn If immediateReturn is true then the method returns immediately.
	   */
	  public Movement travel(float distance, boolean immediateReturn);

	  /**
	   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
	   * Method returns when rotation is done.
	   * 
	   * Note: If you call changeHeading() on a Pilot that is omni-directional (and hence doesn't need to physically 
	   *  rotate) it internally sets the value and returns the same movement angle you specified.
	   * 
	   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
	   */
	  public Movement changeHeading(float angle);

	  /**
	   * Rotates the NXT robot the specifed number of degress; direction determined by the sign of the parameter.
	   * Motion stops  when rotation is done.
	   * 
	   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
	   * @param immediateReturn If immediateReturn is true then the method returns immediately
	   */
	  public Movement changeHeading(float angle, boolean immediateReturn);
	  
	 }
