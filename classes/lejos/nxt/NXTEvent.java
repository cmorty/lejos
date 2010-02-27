package lejos.nxt;

/**
 * This class allows communication of event data between the leJOS firmware and
 * the leJOS low level classes. It can be used to detect I/O completion, Port
 * values changing, button presses etc. To use create a class having the required
 * device type and filter (this may identify a particular port, or I/O operation).
 * Then call the waitEvent function to wait for events from the firmware. This
 * call will block until either the firmware signals an event or the timeout
 * occurs. Upon completion the eventData field will contain information about
 * the event(s) that have been reported. Events themselves are normally reset
 * by calling the associated function to read/write the associated device. If an
 * event is not cleared, it will be reported again in subsequent calls to eventWait.
 *
 *
 * <br>NOTE: This is a low level system interface and should probably not be used
 * directly by user code.
 *
 * @author andy
 */
public class NXTEvent {
    private volatile int state;
    private NXTEvent sync;
    private int updatePeriod;
    private int updatePeriodCnt;
    private int type;
    private int filter;
    private volatile int eventData;
    private volatile int userEvents;

    /** Event type for the Bluetooth device */
    public final static int BLUETOOTH = 1;
    /** Event type for the USB device */
    public final static int USB = 2;
    /** Event type for the RS485 device */
    public final static int RS485 = 3;
    /** Event type for the Analogue ports */
    public final static int ANALOG_PORTS = 4;
    /** Event type for the i2c ports */
    public final static int I2C_PORTS = 5;
    /** Event type for the NXT Buttons */
    public final static int BUTTONS = 6;

    // Internal state flags
    private final static int WAITING = 1;
    private final static int SET = 2;

    public final static int TIMEOUT = 1 << 31;
    public final static int INTERRUPTED = 1 << 30;
    /** These bits are reserved in the eventData field to indicate that a user
     * event has occurred. User events are created by the  notifyEvent method.
     */
    public final static int USER1 = 1 << 29;
    public final static int USER2 = 1 << 28;
    public final static int USER3 = 1 << 27;
    public final static int USER4 = 1 << 26;

    /**
     * Value used to make a timeout be forever.
     */
    public final static long WAIT_FOREVER = 0x7fffffffffffffffL;


    /**
     * Register this event with the system.
     * Events must be registered before they are waited on.
     * @return >= 0 if the event has been registered < 0 if not.
     */
    public native int registerEvent();

    /**
     * Unregister this event. After calling this function the event should not
     * be waited on.
     * @return >= 0 if the event was unregistered < 0 if not
     */
    public native int unregisterEvent();

    /**
     * Wait for an event to occur or for the specified timeout. If a timeout occurs
     * then the TIMEOUT event bit will be set in the result. This bit is the
     * sign bit so timeouts can be detected by testing for a -ve result.
     * @param timeout the timeout in ms. Note a value of <= 0 will return immeadiately.
     * @return the event flags
     */
    public synchronized int waitEvent(long timeout)
    {
        if (timeout <= 0L) return TIMEOUT;
        sync = this;
        updatePeriodCnt = 0;
        eventData = 0;
        state = WAITING;
        try
        {
            // If we already have a user event don't wait
            if ((userEvents & filter) != 0)
                state |= SET;
            else
            {
                // This horrible code turns inetrrupted exceptions into events.
                // If the caller is not interested in INTERRUPTED events then we
                // ignore (and preserve) the state. Note that in the case of a
                // wait with a timeout, if we are ignoring interrupt and the wait
                // is interrupted (other then at the start of the wait), then we
                // will wait longer than requested (because the wait is re-started).
                // This could be fixed but I don't think the extra cost/complxity
                // is worth it...
                boolean ignoredInterrupt = false;
                for(;;)
                {
                    try {
                        wait(timeout);
                        break;
                    }
                    catch (InterruptedException e)
                    {
                        // are we interested in interrupts?
                        if ((filter & INTERRUPTED) != 0)
                        {
                            // yes so capture it and exit
                            state |= SET;
                            eventData |= INTERRUPTED;
                            break;
                        }
                        else
                        {
                            // No ignore it.
                            ignoredInterrupt = true;
                            continue;
                        }
                    }
                }
                // Preserve interrupted state if required
               if (ignoredInterrupt)
                   Thread.currentThread().interrupt();
            }
        }
        finally
        {
            // Trim the user events
            userEvents &= filter;
            if ((state & SET) == 0)
                userEvents |= TIMEOUT;
            eventData |= userEvents;
            // User events get reset now
            userEvents = 0;
            state = 0;
        }
        return eventData;
    }


    /**
     * Wait for an event to occur using the specified filter
     * or for the specified timeout.
     * @param filter The type specific filter for this wait.
     * @param timeout the timeout in ms. Note a value of <= 0 will return immeadiately.
     * @return the event flags or 0 if the event timed out
     */
    public synchronized int waitEvent(int filter, long timeout)
    {
        this.filter = filter;
        return waitEvent(timeout);
    }

    /**
     * Wait for multiple events.
     * <br> Note: The first event in the array is the primary event. This is the
     * only event that can be used with the notifyEvent method.
     * @param events an array of events to wait on.
     * @param timeout the wait timeout. Note a value of <= 0 will return immeadiately.
     * @return true if an event occurred, false otherwise.
     */
    public static boolean waitEvent(NXTEvent[] events, long timeout)
    {
        // We always use the first event as the one to synchronize on.
        NXTEvent sync = events[0];
        synchronized(sync)
        {
            // Make all of the events share the same notifyer
            for(int i = 1; i < events.length; i++)
            {
                events[i].eventData = 0;
                events[i].sync = sync;
                events[i].updatePeriodCnt = 0;
            }
            return (sync.waitEvent(timeout) & TIMEOUT) == 0;
        }
    }

    /**
     * This call can be used to raise a user event. User events will wake a thread
     * from an eventWait call. When waiting on multiple events only the primary
     * event can use user events.
     * @param event
     * @return true if a thread was waiting for this event, false otherwise.
     */
    public synchronized boolean notifyEvent(int event)
    {
        userEvents |= event;
        if (((state & WAITING) != 0) && ((userEvents & filter) != 0))
        {
            // Stop the system from sending further notifications
            state |= SET;
            notify();
            return true;
        }
        else
            return false;
    }

    /**
     * Clear an event. At the moment only user events can be cleared by this
     * method.
     * @param event The events to be cleared.
     */
    public synchronized void clearEvent(int event)
    {
        userEvents &= ~event;
    }

    public int getEventData()
    {
        return eventData;
    }
    /**
     * Set the filter to be applied to this event.
     * @param filter The new filter value.
     */
    public synchronized void setFilter(int filter)
    {
        this.filter = filter;
    }

    /**
     * Return the current filter settings.
     * @return
     */
    public synchronized int getFilter()
    {
        return filter;
    }

    private static NXTEvent cache;
    /**
     * Create a new event ready for use.
     * @param type The event type.
     * @param filter The event specific filter.
     * @param update The update period used when checking the event.
     * @return The new event object.
     */
    public static synchronized NXTEvent allocate(int type, int filter, int update)
    {
        NXTEvent event = cache;
        if (event == null)
            event = new NXTEvent();
        else
            cache = null;

        event.type = type;
        event.filter = filter;
        event.updatePeriod = update;
        event.registerEvent();
        event.userEvents = 0;
        return event;
    }

    /**
     * Release an event.
     */
    public void free()
    {
        unregisterEvent();
        synchronized(NXTEvent.class)
        {
            cache = this;
        }
    }
}
