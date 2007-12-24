package lejos.subsumption;
    

/**
* Arbitrator2 controls which behavior should currently be active in 
* a behavior control system.  This implementation is simpler than the original, but the behaviors it uses must implement 
* the Behavior2 interface. In this architecture, when a behavior wants control, it must call  arbitrator.wantControl();
* @author Roger Glassey  (after Brian Bagnall) 
* @version 1  27-sept 2007
*/
public class Arbitrator2 {
 /**
  * the array of behaviors, in order of increasing priority
  */  
   private Behavior2 [] behavior;
   /**
    * The currently active behavior
    */
   private int active = -1;
   /**
    * the maximum priority number in this system. 
    */
   private int maxPriority = 0;

   
   /**
   * Allocates an Arbitrator2 object and initializes it with an array of
   * Behavior2 objects.  
   *  The largest  index in the Behavior array will have the
   * highest priority level, and hence will suppress all lower priority
   * behaviors if it calls  wantContorol on this. 
   * The new arbitrator  calls setArbitrator() on each Behavior to 
   * establish the call back link.  The Behaviors in an Arbitrator can not
   * be changed once the arbitrator is initialized.<br>
   * <b>NOTE:</b> Once the Arbitrator is initialized, the method go() must be
   * called to begin the arbitration.
   * @param behaviors An array of Behavior objects. The behavior of largest index has the highest proiroty.
   */
   public Arbitrator2(Behavior2 [] behaviors)
   {
      this.behavior = behaviors;
      for(int i = 0; i<behavior.length; i++) behavior[i].setArbitrator(this);    
   }
    
   /**
   * This method starts the arbitration of Behaviors.<br>
   * It iterates over the array of behaviors, highest priority first. and calls the takeControl() method. If  true  is 
   * returned, it calls action() on that behavior. When  the action() method returns (either because it was completed or it was suppressed) the search starts again.
   *If no behavior returns true, this method exits. 
   */
   public void start() 
   {
      //establish call back path for all behaviors       
      maxPriority = behavior.length - 1;
      boolean more = true;
      while(more)
      {
         int indx = -1;
         for(int i = maxPriority; i >= 0; i--) // find highest priority behavior that wants control
         {
            if( behavior[i].takeControl())
            {
               indx = i;
               break;
            }
         }  
         if(indx >= 0)
         {
            active = indx;
            behavior[active].action();  
            active = -1;
         }
         else more = false;
      }
   }
   
 /**
  * If the behavior that sent this message has higher priority than the active behavior,
  *  the active behavior is suppressed. 
  * @param theBehavior
  */  
  public void wantControl(Behavior2 theBehavior)
  { 
     int indx = 0;   // find index of theBehavior
     for(indx = 0; indx <  behavior.length ; indx++)
     {
        if(theBehavior == behavior[indx]) break;
     } 
     if( indx > active && indx < behavior.length && active >= 0)
     {
        behavior[active].suppress(); 
     }
  }

}

