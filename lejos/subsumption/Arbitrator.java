package lejos.subsumption;

/**
 * Arbitrator controls which behavior should currently be active in 
 * a behavior control system. Make sure to call start() after the 
 * Arbitrator is instantiated.<br>
 *  This class has three major responsibilities: <br> 
 * 1. Determine the highest priority  behavior that returns <b> true </b> to takeControl()<br>   
 * 2. Suppress the currently active behavior if its prioirty is less than highest
 * priority. <br>   
 * 3. When the active behavior  exits its action() method, activate  the highest priority behavior 
 * (unless it was the most recently active behavior.)     
 * <br> Requirement for a Behavior:  When suppress() is called, terminate  action() immediately. 
 * @see Behavior
 * @author Roger Glassey
 */
public class Arbitrator {

    private final int NONE = -1;
    private Behavior[] _behavior;
    // highest priority behavior that wants control
    private int _highestPriority = NONE;
    private int _current = NONE; // currently active behavior
    private boolean _returnWhenInactive;
    /**
     * Monitor is an inner class.  It polls the behavior array to find the behavior of hightst
     * priority.  If higher than the current active behavior, it calls current.suppress()
     */
    private Monitor monitor;

    /**
     * Allocates an Arbitrator object and initializes it with an array of
     * Behavior objects. The index of a behavior in this array is its priority level, so 
     * the behavior of the largest index has the highest the priority level. 
     * The behaviors in an Arbitrator can not
     * be changed once the arbitrator is initialized.<BR>
     * <B>NOTE:</B> Once the Arbitrator is initialized, the method start() must be
     * called to begin the arbitration.
     * @param behaviorList an array of Behavior objects.
     * @param returnWhenInactive if <B>true</B>, the <B>start()</B> method returns when no Behavior is active.
     */
    public Arbitrator(Behavior[] behaviorList, boolean returnWhenInactive) {
        _behavior = behaviorList;
        _returnWhenInactive = returnWhenInactive;
        monitor = new Monitor();
        monitor.setDaemon(true);
    }

    /**
     * Same as Arbitrator(behaviorList, true).
     * @param behaviorList An array of Behavior objects.
     */
    public Arbitrator(Behavior[] behaviorList) {
        this(behaviorList, true);
    }

    /**
     * This method starts the arbitration of Behaviors and runs an endless loop.  <BR>
     * Note: Arbitrator does not run in a separate thread. The start()
     * method will never return unless <br>1.  no action() method is running  and
     * <br>2. no behavior  takeControl()
     * returns <B> true </B>  and  <br> 3. the <i>returnWhenInacative </i> flag is true,
     */
    public void start() {
        int lastActive =1+_behavior.length;
        monitor.start();
        while (_highestPriority == NONE) {
            Thread.yield();//wait for some behavior to take contro                    
        }
              
        while (true) 
        {
            if (_highestPriority != NONE ) 
            {
                if ( _highestPriority!= lastActive)
                {
                    _current = _highestPriority;
                    _behavior[_current].action();
                    lastActive = _current;
                    _current = NONE;  // no active behavior at the moment
                }
            } else if (_returnWhenInactive)
            {
                monitor.more = false;// shut down monitor thread
                return;
            }
            Thread.yield();
        }
    }

    /**
     * Finds the highest priority behavior that returns <B>true </B> to takeControl();
     * If this priority is higher than the current behavior, it calls current.suppress().
     */
    private class Monitor extends Thread {

        boolean more = true;
        int maxPriority = _behavior.length - 1;

        public void run() 
        {
            while (more) 
            {
                //FIND HIGHEST PRIORITY BEHAVIOR THAT WANTS CONTROL
                int wantsControl = NONE;
                for (int i = maxPriority; i >= 0; i--) 
                {
                    if (_behavior[i].takeControl()) {
                        wantsControl = i;
                        break;
                    }
                }
                _highestPriority = wantsControl;
                int current = _current;
                if (wantsControl > current && current != NONE) 
                {
                    _behavior[current].suppress();
                }
                Thread.yield();
            }
        }
    }
}
