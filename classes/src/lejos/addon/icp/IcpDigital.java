package lejos.addon.icp;


/**
 * <p>
 * <b>IcpDigital</b> is an enumerated type that defines all of the possible
 * iControlPad digital pad states (<em>e.g.</em>, left, right, up, down).
 * Additionally, there are static helper methods that allow you to test
 * output bytes from the iCP to see if a particular direction is pressed. 
 * </p>
 *
 * @author Jason Healy <jhealy@logn.net>
 */
public enum IcpDigital {
    NONE(0),        // nothing active
    UP(1),          // bit 0 
    DOWN(8),        // bit 3
    LEFT(4),        // bit 2
    RIGHT(2),       // bit 1
    UP_LEFT(5),     // bits 0 and 2
    UP_RIGHT(3),    // bits 0 and 1
    DOWN_LEFT(12),  // bits 3 and 2
    DOWN_RIGHT(10); // bits 3 and 1

    
    /** Mask to cover all possible DPad bits (the byte is shared with other inputs) */
    public static final int ALL_MASK = 15;
    
    /** Bit mask to identify this particular direction */
    private final int mask;
    
    
    /**
     * <p>
     * "Constructor" for the enumerated constants.  Associates the
     * specific bitmask for this direction with the constant.
     * </p>
     * 
     *  @param bitmask The bitmask for this direction
     */
    IcpDigital(int bitmask) {
	mask = bitmask;
    }

    
    /**
     * <p>
     * Given an output byte from the iControlPad, this method
     * determines if a particular direction is activated.
     * </p>
     * 
     * @param b The byte from the iCP containing the DPad data
     * 
     * @return boolean True, if the bit(s) associated with this
     *                 directional constant are on; false otherwise
     */
    private boolean isPressed(int b) {
	return ((b & ALL_MASK) ^ mask) == 0;
    }
    
    
    /**
     * <p>
     * Helper method.  Takes the output byte from the iControlPad
     * and returns the corresponding IcpDigital object that represents
     * the state of the DPad.
     * </p>
     * 
     * @param b The bytes from the iCP containing the DPad data
     * 
     * @return IcpDigital The state of the DPad
     */
    public static IcpDigital decode(int b) {
	for (IcpDigital d : values()) {
	    if (d.isPressed(b)) {
		return d;
	    }
	}
	
	return NONE; // default
    }

}
