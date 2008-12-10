package lejos.subsumption;

/**
 * ArbitratorS controls which behavior should currently be active in 
 * a behavior control system. Make sure to call start() after the 
 * ArbitratorS is instantiated.<br>
 *  This class has three major responsibilities: <br> 
 * 1. Determine the highest priority  behavior that returns <b> true </b> to takeControl()<br>   
 * 2. Suppress the currently active behavior if its prioirty is less than highest
 * priority. <br>   
 * 3. When the action() method exits, it calls action() on the Behavior of highest priority.
 * this class ia a  simplified version of Arbitrator. The differences are:
 * <br> It assumes that a Behavior is no longer active when action() exits.
 * <br> Therefore it will only call suppress() on the Behavior whose action() method is running.
 * <br> It can make consecutives calls of action() on the same Behavior.
 * <br> Requirements for a Behavior:
 * <br>When suppress() is called, terminate  action() immediately.
 * @see Behavior
 * @author Roger Glassey
 */
public class ArbitratorS
{

    private final int NONE = -1;
    private Behavior[] _behavior;
    // highest priority behavior that wants control
    private int _highestPriority = NONE;
    private int _current = NONE; // currently active behavior
//    private int _lastActive = NONE; // last acative behavior
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
    public ArbitratorS(Behavior[] behaviorList, boolean returnWhenInactive)
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
    public ArbitratorS(Behavior[] behaviorList)
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
        monitor.start();
//        RConsole.open();
        while (_highestPriority == NONE)
        {
            Thread.yield();//wait for some behavior to take contro                    
        }
        while (true)
        {
            if (_highestPriority != NONE)
            {
                synchronized (monitor)
                {
                    _current = _highestPriority;
                }
                _behavior[_current].action();
                synchronized (monitor)
                {
                    _current = NONE;  // no active behavior at the moment
                }

            } else if (_returnWhenInactive)
            {// no behavior wants to run
                monitor.more = false;// shut down monitor thread
                return;
            }
            Thread.yield();
        }
    }

    /**
     * Finds the highest priority behavior that returns <B>true </B> to takeControl();
     * If this priority is higher than the current behavior, it calls current.suppress().
     * If there is no current behavior, calls suppress() on the most recently acrive behavior.
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
                int wantsControl = NONE;
                synchronized (this)
                {
                    for (int i = maxPriority; i >= 0; i--)
                    {
                        if (_behavior[i].takeControl())
                        {
                            wantsControl = i;
                            break;
                        }
                    }
                    int current = _current;
                    if (current != NONE)
                    {
                        if (wantsControl > current)
                        {
                            _behavior[current].suppress();
                            _current = NONE;
                        }
                        
                    }
                     _highestPriority = wantsControl;
                }// end sync
                Thread.yield();
            }
        }
    }
}
  
