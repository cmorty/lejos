package lejos.pc.comm;

/**
 * Container for holding the output state values.
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.2 September 9, 2006
 * @see NXTCommand
 */
public class OutputState {
	/**
	 * Status of the NXTCommand.getOutputState command.
	 */
	public byte status; 
	
	/**
	 * The port number - range: 0 to 2
	 */
	public int outputPort; 
	
	/**
	 * The power setting : -100 to 100
	 */
	public byte powerSetpoint; 
	
	/**
	 * The motor mode - see NXTProtocol for enumeration
	 */
	public int mode; 
	
	/**
	 * The regulation mode - see NXTProtocol for enumeration
	 */
	public int regulationMode; 
	
	/**
	 * The turn ratio: -100 to 100
	 */
	public byte turnRatio; 
	
	/**
	 * The run state - see NXTProtocol for enumeration
	 */
	public int runState; 
	
	/**
	 * Current limit on a movement in progress, if any
	 */
	public long tachoLimit; 
	
	/**
	 * Internal count. Number of counts since last reset of the motor counter)
	 */
	public int tachoCount; 
	
	/**
	 * Current position relative to last programmed movement
	 */
	public int blockTachoCount; 
	
	/**
	 * Current position relative to last reset of the rotation sensor for this motor)
	 */
	public int rotationCount; 

	public OutputState(int port) {
		outputPort = port;
	}
}
