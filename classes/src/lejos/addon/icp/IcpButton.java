package lejos.addon.icp;


/**
 * <p>
 * <b>IcpButton</b> is an enumerated type that defines all of the possible
 * iControlPad buttons (not including digital pads and analog joysticks).
 * Additionally, there are static helper methods that allow you to test
 * output bytes from the iCP to see if a particular button is pressed. 
 * </p>
 *
 * @author Jason Healy <jhealy@logn.net>
 */
public enum IcpButton {
    A(8, false),      // bit 3
    B(32, false),     // bit 5
    X(16, false),     // bit 4
    Y(4, false),      // bit 2
    L(16, true),      // bit 4
    R(64, false),     // bit 6
    START(2, false),  // bit 1
    SELECT(1, false); // bit 0

    
    /** Bit mask to apply to iCP bytes that isolate this button */
    public final int mask;
    
    /** The iCP outputs 2 bytes which contain all button state.  Which byte, A (true) or B (false) is associated with this button? */
    public final boolean byteA;

    
    /**
     * <p>
     * "Constructor" for the enumerated constants.  Takes the mask
     * and the byte (A or B, defined as a boolean) that corresponds
     * to this button constant.
     * 
     * @param bitmask The mask that isolates this button in the byte
     * @param ab The byte that contains this button's data; A (true) or B (false)
     */
    IcpButton(int bitmask, boolean ab) {
	mask = bitmask;
	byteA = ab;
    }

    
    /**
     * <p>
     * Given a two-byte output from the iCP, this method determines
     * if the given button is pressed or not.
     * </p>
     * 
     * @param a The "A" byte returned from the iCP
     * @param b The "B" byte returned from the iCP
     * 
     * @return boolean True, if the masked bit for this button is
     *                 on in output bytes; false otherwise
     */
    public boolean isPressed(int a, int b) {
	return ((byteA ? a : b) & mask) != 0;
    }

    
    /**
     * <p>
     * Given a two-byte output from the iCP, returns an array
     * of IcpButton constants that are "on" (pressed).  Any
     * zero bits are ignored.
     * </p>
     * 
     * @param a The "A" byte returned from the iCP
     * @param b The "B" byte returned from the iCP
     * 
     * @return IcpButton[] An array (possibly zero-length) of IcpButton
     *                     constants corresponding to all buttons that
     *                     are pressed (according to the A and B bytes provided)
     */
    public static IcpButton[] getFlaggedButtons(int a, int b) {
	// count up the number of "on" bits
	int count = ((a & L.mask) != 0) ? 1 : 0;
	int bits = b;
	for (int i = 0; i < 7; i++) {
	    count += bits & 1;
	    bits = bits >> 1;
	}
	
	IcpButton[] bs = new IcpButton[count];
	if (bs.length > 0) {
	    int i = 0;
	    for (IcpButton button : values()) {
		if (button.isPressed(a, b)) {
		    bs[i++] = button;
		}
	    }
	}

	return bs;
    }
    

}
