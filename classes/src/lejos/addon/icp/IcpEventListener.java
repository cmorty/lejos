package lejos.addon.icp;


/**
 * <p>
 * The <b>IcpEventListener</b> interface defines all methods for classes
 * wishing to register to receive notification of changes to the inputs
 * of an iControlPad (<em>e.g.</em>, button presses, joystick movements,
 * or digital pad changes).
 * </p>
 * 
 * @see IcpEventPoller
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public interface IcpEventListener {

    /**
     * <p>
     * Handles notification of a change in the status of the digital
     * pad (direction change, or complete release of all directions).
     * </p>
     * 
     * @param d The new state of the digital pad
     */
    public void digitalChange(IcpDigital d);

    
    /**
     * <p>
     * Handles notification of a change in Analog Joystick 1.
     * </p>
     * 
     * @param x The new x value (-32 to 32)
     * @param y The new y value (-32 to 32)
     */
    public void analogOneChange(int x, int y);
    

    /**
     * <p>
     * Handles notification of a change in Analog Joystick 2.
     * </p>
     * 
     * @param x The new x value (-32 to 32)
     * @param y The new y value (-32 to 32)
     */
    public void analogTwoChange(int x, int y);
    
    
    /**
     * <p>
     * Handles notification of a button being pressed.
     * </p>
     * 
     * @param b The button that went from unpressed to pressed
     */
    public void buttonPressed(IcpButton b);
    
    
    /**
     * <p>
     * Handles notification of a button being released.
     * </p>
     * 
     * @param b The button that went from pressed to unpressed
     */
    public void buttonReleased(IcpButton b);
    
}
