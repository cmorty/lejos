package lejos.nxt.debug;

/**
 * This class provides the primary interface to the debug capabilities of leJOS.
 * It has two main functions:
 * 1. To provide access to the internal VM and program structures:
 *		Threads
 *		Stacks
 *		Methods
 *		Classes
 * 2. Provide an event based mechanism that allows Java applications to wait for
 *    debug events from the VM.
 * @author andy
 */
public class DebugInterface
{
    private static final DebugInterface monitor = new DebugInterface(true);
    public static final int DBG_EXCEPTION = 1;
    public static final int DBG_USER_INTERRUPT = 2;
    public static final int DBG_EVENT_DISABLE = 0;
    public static final int DBG_EVENT_ENABLE = 1;
    public static final int DBG_EVENT_IGNORE = 2;
    // This is reflected in the kernel structure  
    public int typ;                // type of debug event
    public Exception exception;
    public Thread thread;
    public int pc;
    public int frame;
    public int method;
    public int methodBase;
    public int classBase;
    public int fieldBase;
    public Thread[] threads;

    private native static int getDataAddress (Object obj);
    
    /**
     * Private constructor. Sets up the event interface in the kernel.
     */
    private DebugInterface(boolean dummy)
    {
        setDebug();
        typ = 0;
    }

    public static DebugInterface get()
    {
        return monitor;
    }

    public void clear()
    {
        typ = 0;
    }

    /**
     * Wait for a debug event from the kernel
     *
     * @param millis wait for at most millis milliseconds. 0 = forever.
     * @return The new debug event
     * @throws InterruptedException
     */
    public final int waitEvent(int millis) throws InterruptedException
    {
        synchronized (monitor)
        {
            if (monitor.typ == 0)
                monitor.wait(millis);
            return monitor.typ;
        }
    }

    /**
     * Initalise the debug interface
     */
    private native final void setDebug();


    /**
     * Allow events to be enabled/disabled/ignored. Disabled events will
     * return to the default behaviour. Enabled events will be reported via
     * this interface. Ignored events will be discarded.
     * @param event
     * @param option
     * @return previous state of this event.
     */
    public native static final int eventOptions(int event, int option);
    


}
