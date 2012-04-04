package lejos.addon.icp;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * <p>
 * The <b>IcpEventPoller</b> class polls an iControlPad at regular
 * intervals for the state of its inputs.  Whenever an input changes
 * (<em>e.g.</em>, a button is released, a joystick moved, or the
 * digital pad pressed), this class notifies all registered listeners
 * of the change.
 * </p>
 * 
 * @see IcpEventListener
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public class IcpEventPoller {

    /** List of encapsulated listeners to notify of change events */
    protected static final List<IcpEventAdapter> adapters = new ArrayList<IcpEventAdapter>(1);


    /**
     * <p>
     * Registers an IcpEventListener to receive updates at the
     * desired interval.  Note that because events are only fired when
     * the iControlPad inputs are changed by the user, the actual time
     * between updates may be much longer than the given interval; the
     * interval is a <b>minimum</b> time between updates.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object representing the controller to poll
     * @param l The listener to send event notifications to
     * @param interval The desired polling interval in milliseconds (minimum)
     */
    public static void addListener(Icp icp, IcpEventListener l, int interval) {
	// try to find this interval if it exists
	for (IcpEventAdapter a : adapters) {
	    if (a.icp == icp && a.interval == interval) {
		a.addListener(l);
		return;
	    }
	}
	
	// didn't find one; so create one instead
	IcpEventAdapter a = new IcpEventAdapter(icp, interval);
	a.addListener(l);
	IcpStatePoller.addListener(icp, a, interval);
    }

    
    /**
     * <p>
     * Registers an IcpEventListener using the default polling interval.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object representing the controller to poll
     * @param l The listener to send event notifications to
     * 
     * @see IcpEventPoller#addListener(Icp, IcpEventListener, int)
     * @see IcpStatePoller#DEFAULT_INTERVAL
     */
    public static void addListener(Icp icp, IcpEventListener l) {
	addListener(icp, l, IcpStatePoller.DEFAULT_INTERVAL);
    }

    
    /**
     * <p>
     * Unregisters a previously-registered event listener.  Requests
     * to remove unknown listeners are ignored.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object the listener was associated with
     * @param l The listener that was previously registered
     */
    public static void removeListener(Icp icp, IcpEventListener l) {
	// grossly inefficient
	ListIterator<IcpEventAdapter> itr = adapters.listIterator();
	while (itr.hasNext()) {
	    IcpEventAdapter a = itr.next();
	    if (a.icp == icp) {
		int size = a.removeListener(l);
		if (size < 1) {
		    IcpStatePoller.removeListener(a.icp, a);
		    itr.remove();
		}
	    }
	}
    }

    
    /**
     * <p>
     * <b>IcpEventAdapter</b> uses the IcpState and IcpStatePoller objects to
     * perform the low-level polling activities, and then distills polled
     * states into change events.  These change events are then passed along
     * to all registered listeners.
     * </p>
     * 
     * @see IcpStatePoller
     * @see IcpState
     */
    private static class IcpEventAdapter implements IcpStateListener {

	/** The Icp object to poll for state information */
	protected Icp icp;
	
	/** The interval (in milliseconds) to poll the Icp object for new state */
	protected int interval;
	
	/** The list of listeners to notify when the state changes */
	protected List<IcpEventListener> listeners = new ArrayList<IcpEventListener>();
	
	/** The previous IcpState, used to compare to the current state when finding changes */
	protected IcpState p = new IcpState();


	/**
	 * <p>
	 * Constructor.  Sets the Icp to poll and the polling interval.
	 * </p>
	 * 
	 * @param lejos.addon.icp The Icp object to poll
	 * @param interval The interval (in milliseconds) between polls
	 */
	public IcpEventAdapter(Icp icp, int interval) {
	    this.icp = icp;
	    this.interval = interval;
	}

	/**
	 * <p>
	 * Registers a new IcpEventListener to receive change events.
	 * </p>
	 * 
	 * @param l The listener to register
	 */
	public synchronized void addListener(IcpEventListener l) {
	    listeners.add(l);
	}
	
	
	/**
	 * <p>
	 * Unregisters a previously-registered IcpEventListener.  Requests
	 * to unregister an unknown listener are ignored.
	 * </p>
	 * 
	 * @param l The listener to unregister
	 * 
	 * @return int The new size of the listener pool
	 */
	public synchronized int removeListener(IcpEventListener l) {
	    listeners.remove(l);
	    return listeners.size();
	}
	
	
	/**
	 * <p>
	 * Receives a polled state from the Icp, compares to the previous state,
	 * and notifies all listeners of any changes.
	 * </p>
	 * 
	 * @param s The new state of the iCP
	 * 
	 * @see IcpStateListener#pollEvent(IcpState)
	 */
	public synchronized void pollEvent(IcpState s) {
	    // only fire events if something changed
	    if (!p.equals(s)) {
		// digital pad
		if (p.getDigital() != s.getDigital()) {
		    for (IcpEventListener l : listeners) {
			l.digitalChange(s.getDigital());
		    }
		}
		// analog pads
		if (p.getAnalog1X() != s.getAnalog1Y() || p.getAnalog1Y() != s.getAnalog1Y()) {
		    for (IcpEventListener l : listeners) {
			l.analogOneChange(s.getAnalog1X(), s.getAnalog1Y());
		    }
		}
		if (p.getAnalog2X() != s.getAnalog2Y() || p.getAnalog2Y() != s.getAnalog2Y()) {
		    for (IcpEventListener l : listeners) {
			l.analogTwoChange(s.getAnalog2X(), s.getAnalog2Y());
		    }
		}
		// buttons
		for (IcpButton b : s.getChangedButtons(p)) {
		    if (s.buttonPressed(b)) {
			for (IcpEventListener l : listeners) {
			    l.buttonPressed(b);
			}
		    }
		    else {
			for (IcpEventListener l : listeners) {
			    l.buttonReleased(b);
			}
		    }
		}
	    }
	    // store the current state for next time
	    p = s;
	}
    }
}