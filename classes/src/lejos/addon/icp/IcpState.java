package lejos.addon.icp;


/**
 * <p>
 * The <b>IcpState</b> object encapsulates the status of all user inputs
 * on the iControlPad (buttons, digital pad, analog joysticks).  It allows
 * clients to compare this state with other states, and test individual
 * controls for their specific state/values.
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public class IcpState {
    
    /** Digital Pad byte "A" */
    protected int dA;
    
    /** Digital Pad byte "B" */
    protected int dB;
    
    /** Analog Joystick 1 X value */
    protected int a1X;
    
    /** Analog Joystick 1 Y value */
    protected int a1Y;
    
    /** Analog Joystick 2 X value */
    protected int a2X;
    
    /** Analog Joystick 2 Y value */
    protected int a2Y;
    
    
    /**
     * <p>
     * No-args constructor.  Sets all byte values to zero.
     * </p>
     */
    public IcpState() {
	this(0, 0, 0, 0, 0, 0);
    }

    
    /**
     * <p>
     * Constructor.  Requires all byte values from the iControlPad
     * and stores them internally.
     * </p>
     * 
     * @param digitalA The "A" byte for the digital pad
     * @param digitalB The "B" byte for the digital pad
     * @param analog1X Analog joystick 1 X value
     * @param analog1Y Analog joystick 1 Y value
     * @param analog2X Analog joystick 2 X value
     * @param analog2Y Analog joystick 2 Y value
     */
    public IcpState(int digitalA, int digitalB, int analog1X,
	    int analog1Y, int analog2X, int analog2Y) {
	dA = digitalA;
	dB = digitalB;
	a1X = analog1X;
	a1Y = analog1Y;
	a2X = analog2X;
	a2Y = analog2Y;
    }

    
    /**
     * <p>
     * Tests this IcpState with another for equality.
     * </p>
     * 
     * @param other The object to test for equality
     * 
     * @return boolean True, if the other object is an IcpState
     *                 object and all of its internal byte values
     *                 are exactly the same
     */
    public boolean equals(Object other) {
	if (other instanceof IcpState) {
	    IcpState o = (IcpState)other;
	    return (dA == o.dA && dB == o.dB &&
		    a1X == o.a1X && a1Y == o.a1Y &&
		    a2X == o.a2X && a2Y == o.a2Y);
	}
	return false;
    }


    /**
     * <p>
     * Gets the value of analog joystick 1's x coordinate
     * </p>
     * 
     * @return int The x value, in the range -32 to 32
     */
    public int getAnalog1X() {
	return a1X;
    }

    
    /**
     * <p>
     * Gets the value of analog joystick 1's y coordinate
     * </p>
     * 
     * @return int The y value, in the range -32 to 32
     */
    public int getAnalog1Y() {
	return a1Y;
    }

    
    /**
     * <p>
     * Gets the value of analog joystick 2's x coordinate
     * </p>
     * 
     * @return int The x value, in the range -32 to 32
     */
    public int getAnalog2X() {
	return a2X;
    }

    
    /**
     * <p>
     * Gets the value of analog joystick 2's y coordinate
     * </p>
     * 
     * @return int The y value, in the range -32 to 32
     */
    public int getAnalog2Y() {
	return a2Y;
    }


    /**
     * <p>
     * Returns the IcpDigital enum constant that describes the
     * current state of the digital pad (<em>e.g.</em>, a
     * direction or "none").  See the enum for list of possible
     * values.
     * </p>
     * 
     * @return IcpDigital The constant describing the state of the digital pad
     * 
     * @see IcpDigital
     */
    public IcpDigital getDigital() {
	return IcpDigital.decode(dA);
    }

    
    /**
     * <p>
     * Returns an array of buttons that are listed as "pressed" in this state.
     * </p>
     * 
     * @return IcpButton[] Array (possibly zero-length) of pressed buttons.
     */
    public IcpButton[] getPressedButtons() {
	return IcpButton.getFlaggedButtons(dA, dB);
    }

    
    /**
     * <p>
     * Returns an array of buttons that have a different state when compared
     * between this IcpState object and the one given.
     * </p>
     * 
     * @param s The other IcpState to compare against
     * 
     * @return IcpButton[] Array of buttons whose states in this IcpState differ
     *                     from those in the provided state s
     */
    public IcpButton[] getChangedButtons(IcpState s) {
	return IcpButton.getFlaggedButtons(dA ^ s.dA, dB ^ s.dB);
    }
    

    /**
     * <p>
     * Tests if a particular button is marked as "pressed" in this state.
     * </p>
     * 
     * @param b The button to test
     * 
     * @return boolean True, if the given button is marked as pressed; false otherwise
     */
    public boolean buttonPressed(IcpButton b) {
	return b.isPressed(dA, dB);
    }

    
}
