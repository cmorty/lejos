package lejos.subsumption;

/**
 * Arbitrator controls which behavior should currently be active in 
 * a behavior control system. Make sure to call start() after the 
 * Arbitrator is instantiated.
*  This class has two major responsibilities: <br> 
 * 1. Suppress the currently active behavior when  a behavior of higher priority returns  true  to takeControl()<br>
 * 2. When the active behavior  exits its action() method, activate  the behavior of highest priority that returns 
 *  true  to takeControl().
 * <br> Requirement for a Behavior:  When suppress() is called, terminate  action() immediately. 
 * @see Behavior
 * @author Roger
 */
public class Arbitrator
{

   private final int NONE = -1;
   private Behavior[] _behavior;
   private int _highestPriority = NONE; // highest priority behavior that wants control
   private int _current = NONE; // currently active behavior
   private boolean _returnWhenInactive;
   /**
    * Monitor is an inner class.  It polls the behavior array to find the behavior of hightst
    * priority.  If higher than the current active behavior, it calls current.suppress()
    */
   private Monitor monitor;

   /**
    * Allocates an Arbitrator object and initializes it with an array of
    * Behavior objects. The highest index in the Behavior array will have the
    * highest order behavior level, and hence will suppress all lower level
    * behaviors if it becomes active. The Behaviors in an Arbitrator can not
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
      Monitor monitor = new Monitor();
      monitor.start();        
      while(_highestPriority == NONE);//wait for some behavior to take control
   }

   /**
    * Same as Arbitrator4(behaviorList, true).
    * @param behaviorList An array of Behavior objects.
    */  
   public Arbitrator(Behavior[] behaviorList)
   {
      this(behaviorList, true);
   }
   /**
    * This method starts the arbitration of Behaviors. <BR>
    * Note: Arbitrator does not run in a separate thread, and hence the start()
    * method will never return unless returnWhileInacative is true and no behavior wants to take control.
    */
   public void start() 
   {
      while(true)
      {   
         if(_highestPriority != NONE)
         {
            _current = _highestPriority;
            _behavior[_current].action();
            _current = NONE;  // no active behavior at the moment
         }
         else
         {
            if (_returnWhenInactive)
            {
               monitor.more = false;
               return;
            }
         }
         Thread.yield();
      }
   }
   /**
    * Finds the highest priority behavior that wants control
    * If this priority is higher than the current behavior, it calls current.suppress().
    */
   private class Monitor extends Thread 
   {
      boolean more = true;
      int maxPriority = _behavior.length - 1;
      public void run()
      {
         while(more)
         {
            //FIND HIGHEST PRIORITY BEHAVIOR THAT WANTS CONTROL
            for(int i = maxPriority; i >= 0; i--)
            {
               if(_behavior[i].takeControl())
               {
                  _highestPriority = i;
                  break;
               }
            }
            if(_highestPriority > _current && _current != NONE)
            {
               _behavior[_current].suppress();
            }
            Thread.yield();
         }
      }
   }
}
