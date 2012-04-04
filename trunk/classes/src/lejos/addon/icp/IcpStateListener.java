package lejos.addon.icp;


/**
 * <p>
 * The <b>IcpStateListener</b> interface defines all methods for classes
 * wishing to register to receive IcpState events from an Icp object
 * at regular intervals.
 * </p>
 * 
 * @see IcpState
 * @see IcpStatePoller
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public interface IcpStateListener {

    /**
     * <p>
     * Receive an IcpState object representing the status of the
     * buttons and controls on an Icp object.
     * </p>
     * 
     * @param s The encapsulated set of input states for a particular iControlPad
     */
    public void pollEvent(IcpState s);

}
