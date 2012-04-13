package lejos.addon.icp;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>
 * The <b>IcpStatePoller</b> class polls an iControlPad at regular intervals and
 * gathers its inputs into an IcpState object.  It then notifies any registered
 * IcpStateListeners of the new state object.  A shared Timer object is used to
 * schedule polling runs for all listeners. 
 * </p>
 * 
 * @see IcpState
 * @see IcpStateListener
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public class IcpStatePoller {
    
    /** Default interval (in milliseconds) to poll the iCP.
     * Currently set to the minimum supported value (25ms). */
    public static final int DEFAULT_INTERVAL = 25;

    /** Shared timer object to schedule polling runs */
    public static final Timer TIMER = new Timer("IcpPoller", true);

    /** List of registered polling tasks */
    protected static final List<IcpPollTask> tasks = new ArrayList<IcpPollTask>(1);

    
    /**
     * <p>
     * Default no-args constructor.  Marked private to enforce singleton access.
     * </p>
     */
    private IcpStatePoller() {}

    
    /**
     * <p>
     * Registers an IcpStateListener to receive IcpState objects at the
     * desired interval.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object representing the controller to poll
     * @param l The listener to send state notifications to
     * @param interval The desired polling interval in milliseconds
     */
    public static void addListener(Icp icp, IcpStateListener l, int interval) {
	// try to find this interval if it exists
	for (IcpPollTask t : tasks) {
	    if (t.icp == icp && t.interval == interval) {
		t.addListener(l);
		return;
	    }
	}
	
	// didn't find one; so create one instead
	IcpPollTask t = new IcpPollTask(icp, interval);
	t.addListener(l);
	tasks.add(t);
	TIMER.scheduleAtFixedRate(t, 0, interval);
    }


    /**
     * <p>
     * Registers an IcpStateListener to receive IcpState objects using
     * the default interval.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object representing the controller to poll
     * @param l The listener to send state notifications to
     * 
     * @see IcpStatePoller#addListener(Icp, IcpStateListener, int)
     * @see IcpStatePoller#DEFAULT_INTERVAL
     */
    public static void addListener(Icp icp, IcpStateListener l) {
	addListener(icp, l, DEFAULT_INTERVAL);
    }

    
    /**
     * <p>
     * Unregisters a previously-registered state listener.  Requests
     * to remove unknown listeners are ignored.
     * </p>
     * 
     * @param lejos.addon.icp The Icp object the listener was associated with
     * @param l The listener that was previously registered
     */
    public static void removeListener(Icp icp, IcpStateListener l) {
	// grossly inefficient
	ListIterator<IcpPollTask> itr = tasks.listIterator();
	while (itr.hasNext()) {
	    IcpPollTask t = itr.next();
	    if (t.icp == icp) {
		int size = t.removeListener(l);
		if (size < 1) {
		    t.cancel();
		    itr.remove();
		}
	    }
	}
    }


    /**
     * <p>
     * <b>IcpPollTask</b> tracks a list of IcpStateListeners that share a
     * common Icp object and polling interval.  Additionally, the class extends
     * TimerTask, allowing it to be scheduled using a generic Java Timer
     * object.  When the timer method is invoked, the object asks the Icp
     * object for its state and then notifies all known listeners.
     * </p>
     */
    private static class IcpPollTask extends TimerTask {

	/** The Icp object to poll for state information */
	protected Icp icp;
	
	/** The interval (in milliseconds) to poll the Icp object */
	protected int interval;
	
	/** The IcpStateListener objects to notify whenever the state is polled */
	protected List<IcpStateListener> listeners;


	/**
	 * <p>
	 * Constructor.  Sets the Icp and interval for this polling task.
	 * </p>
	 * 
	 * @param lejos.addon.icp The Icp object to poll
	 * @param interval The interval (in milliseconds) between polls
	 */
	public IcpPollTask(Icp icp, int interval) {
	    this.icp = icp;
	    listeners = new ArrayList<IcpStateListener>();
	    this.interval = interval;
	}


	/**
	 * <p>
	 * Registers a new IcpStateListener with this Icp and interval.
	 * </p>
	 * 
	 * @param l The listener to register
	 */
	public void addListener(IcpStateListener l) {
	    synchronized (listeners) {
		for (IcpStateListener c : listeners) {
		    if (c.equals(l)) {
			return;
		    }
		}
		listeners.add(l);
	    }
	}


	/**
	 * <p>
	 * Unregisters a previously-registered listener.  Requests to
	 * remove an unknown listener are ignored.
	 * </p>
	 * 
	 * @param l The listener to unregister
	 * 
	 * @return int The new size of the listener pool
	 */
	public int removeListener(IcpStateListener l) {
	    synchronized (listeners) {
		listeners.remove(l);
		return listeners.size();
	    }
	}


	/**
	 * <p>
	 * Executed by the Timer object at requested intervals, this
	 * method polls the Icp object and then notifies all listeners
	 * of the new state.
	 * </p>
	 */
	public void run() {
		try {
			synchronized(listeners) {
				// get data from Icp
				IcpState state = icp.getControlState();
				// tell all the listeners about it
				for (IcpStateListener c : listeners) {
					c.pollEvent(state);
				} 
			}
		}
		catch (IcpTimeoutException ite) {
			System.out.println("Caught ite " + System.currentTimeMillis());
		}
		finally {
			System.out.println("Finally run() " + System.currentTimeMillis());
		}

		System.out.println("After finally " + System.currentTimeMillis());
	}

    }
}
