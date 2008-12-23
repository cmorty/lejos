package lejos.subsumption;
                                     

/**
 * Arbitrator controls which behavior should currently be active in 
 * a behavior control system. Make sure to call start() after the 
 * Arbitrator is instantiated.<br>
 *  This class has three major responsibilities: <br> 
 * 1. Determine the highest priority  behavior that returns <b> true </b> to takeControl()<br>   
 * 2. Suppress the currently active behavior if its prioirty is less than highest
 * priority. If no action() method is running, and some Behavior should take control,
 * suppress the last active behavior; <br>
 * 3. When the active behavior  exits its action() method, activate  the behavior of highest priority 
 * (unless it was the most recently active behavior.)     
 * <br> Requirement for a Behavior:  When suppress() is called, terminate  action() immediately. 
 * @see Behavior
 * @author Roger Glassey  and Lawrie Griffith
 */
public class Arbitrator
{

    private final int NONE = -1;
    private Behavior[] _behavior;
    // highest priority behavior that wants control
    private int _highestPriority = NONE;
    private int _current = NONE; // currently active behavior
    private int _lastActive = NONE; // last acative behavior
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
    public Arbitrator(Behavior[] behaviorList, boolean returnWhenInactive)
    {
        _behavior = behaviorList;
        _returnWhenInactive = returnWhenInactive;
        monitor = new Monitor();
        monitor.setDaemon(true);
    }

    /**
     * Same as Arbitrator(behaviorList, false) Arbitrator start never exits
     * @param behaviorList An array of Behavior objects.
     */
    public Arbitrator(Behavior[] behaviorList)
    {
        this(behaviorList, false);

    }

    /**
     * This method starts the arbitration of Behaviors and runs an endless loop.  <BR>
     * Note: Arbitrator does not run in a separate thread. The start()
     * method will never return unless <br>1.  no action() method is running  and
     * <br>2. no behavior  takeControl()
     * returns <B> true </B>  and  <br> 3. the <i>returnWhenInacative </i> flag is true,
     */
    public void start()
    {
        _lastActive = NONE;
        monitor.start();
        
        while (_highestPriority == NONE)
        {
            Thread.yield();//wait for some behavior to take control                   
        }
        
        while (true)
        {
        	synchronized (monitor) {
	            if (_highestPriority != NONE)
	            {
	                if (_highestPriority != _lastActive) // no repetition of action()
	                {
	                     _current = _highestPriority;
	             		_lastActive = _current;
	                }
	            } else if (_returnWhenInactive)
	            {// no behavior wants to run
	                monitor.more = false;// shut down monitor thread
	                return;
	            }
        	}
        	// monitor released, so _current could be suppressed before run
        	if (_current != NONE) {
        		_behavior[_current].action(); // Run the action
        		_current = NONE;
        	}
            Thread.yield();
        }
    }

    /**
     * Finds the highest priority behavior that returns <B>true </B> to takeControl();
     * If this priority is higher than the current behavior, it calls current.suppress().
     * If there is no current behavior, calls suppress() on the most recently active behavior.
     */
    private class Monitor extends Thread
    {

        boolean more = true;
        int maxPriority = _behavior.length - 1;

        public void run()
        {
            while (more)
            {
                //FIND HIGHEST PRIORITY BEHAVIOR THAT WANTS CONTROL
                synchronized (this)
                {
                    _highestPriority = NONE;
                    for (int i = maxPriority; i >= 0; i--)
                    {
                        if (_behavior[i].takeControl())
                        {
                            _highestPriority = i;
                            break;
                        }
                    }
                    if (_current != NONE)
                    {
                        if (_highestPriority > _current)
                        {
                            _behavior[_current].suppress();
                            _current = NONE;
                            _lastActive = NONE;
                        }
                    } else // current == NONE
                    {
                    	// When current is finished, 
                    	// suppress the last active unless it still should run
                        if (_lastActive != NONE  &&  _highestPriority != _lastActive )
                        {
                            _behavior[_lastActive].suppress();
                            _lastActive = NONE;
                        }
                    }
                }// end sync
                Thread.yield();
            }
        }
    }
}
  
