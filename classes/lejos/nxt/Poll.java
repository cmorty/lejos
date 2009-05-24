/**
 * NXT access classes.
 */
package lejos.nxt;

/**
 * Provides blocking access to events from the NXT. Poll is a bit
 * of a misnomer (since you don't 'poll' at all) but it takes its
 * name from the Unix call of the same name.
 */
public class Poll
{
    public static final short SENSOR1_MASK = 0x01;
    public static final short SENSOR2_MASK = 0x02;
    public static final short SENSOR3_MASK = 0x04;
    public static final short SENSOR4_MASK = 0x08;
    
    public static final short ALL_SENSORS  = 0x0F;

    public static final short ENTER_MASK    = 0x10;
    public static final short LEFT_MASK    = 0x20;
    public static final short RIGHT_MASK    = 0x40;
    public static final short ESCAPE_MASK    = 0x80;
    public static final short ALL_BUTTONS  = 0xF0;
    public static final short BUTTON_MASK_SHIFT  = 4;

    public static final short SERIAL_MASK = 0x100; // Not used
    public static final short SERIAL_SHIFT = 8;

    private static Poll monitor = new Poll(true);
    
    // This is reflected in the kernel structure  
    private short changed;  // The 'changed' mask.

    /**
     * Private constructor. Sets up the poller in the kernel.
     */ 
    private Poll(boolean dummy) {
        setPoller();
    }
    
    /**
     * Constructor.
     */ 
    public Poll() {
    }
    
    /**
     * Wait for the sensor/button values to change then return.
     *
     * @param mask bit mask of values to monitor.
     * @param millis wait for at most millis milliseconds. 0 = forever.
     * @return a bit mask of the values that have changed
     * @throws InterruptedException
     */
    public final int poll(int mask, int millis) throws InterruptedException {
        synchronized (monitor) {
            int ret = mask & monitor.changed;
            
            // The inputs we're interested in may have already changed
            // since we last looked so check before we wait.
            while (ret == 0) {
                monitor.wait(millis);
                
                // Work out what's changed that we're interested in.
                ret = mask & monitor.changed;
            }

            // Clear the bits that we're monitoring. If anyone else
            // is also monitoring these bits its tough.
            monitor.changed &= ~mask;
            return ret;
        }
    }

    /**
     * Set a throttle on the regularity with which inputs
     * are polled.
     * @param throttle number of sensor reads between polls.
     * Default value is 1. 0 means poll as often as possible.
     * Sensor reads occur every 3ms.
     */
    public native final void setThrottle(int throttle);


    /**
     * Sets up and starts the the poller in the kernel.
     */
    private native final void setPoller();
/*
    {
        new Thread() {
            public void run() {
                do {
                    synchronized (monitor) {
                        if (anthing has changed) {
                            monitor.changed = whatever has changed
                            notifyAll();
                        }
                    }
                } while (true);
            }
        }.start();
    }
*/
}
