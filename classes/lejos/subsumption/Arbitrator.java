package lejos.subsumption;


/**
* Arbitrator controls which behavior should currently be active in 
* a behavior control system. Make sure to call start() after the 
* Arbitrator is instantiated.
* @see Behavior
* @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
* @version 0.1  27-July-2001
*/
public class Arbitrator {
   
   private Behavior [] behavior;
   private final int NONE = 99;
   private int currentBehavior;
   private BehaviorAction actionThread;
   
   /**
   * Allocates an Arbitrator object and initializes it with an array of
   * Behavior objects. The highest index in the Behavior array will have the
   * highest order behavior level, and hence will suppress all lower level
   * behaviors if it becomes active. The Behaviors in an Arbitrator can not
   * be changed once the arbitrator is initialized.<BR>
   * <B>NOTE:</B> Once the Arbitrator is initialized, the method start() must be
   * called to begin the arbitration.
   * @param behaviors An array of Behavior objects.
   */
   public Arbitrator(Behavior [] behaviors) {
      this.behavior = behaviors;
      currentBehavior = NONE;
      actionThread = new BehaviorAction();
      actionThread.start();      
   }
   
   /**
   * This method starts the arbitration of Behaviors.
   * Modifying the start() method is not recomended. <BR>
   * Note: Arbitrator does not run in a seperate thread, and hence the start()
   * method will never return.
   */
   public void start() {
      int totalBehaviors = behavior.length - 1;

      while(true) {
         // Check through all behavior.takeControl() starting at highest level behavior
         for(int i = totalBehaviors;i>=0;--i) {
            if(behavior[i].takeControl()) {
               // As soon as takeControl() is true, execute the currentBehavior.suppress()
               //if(behavior[i] != currentBehavior) {
               if(i != currentBehavior) { // Prevents program from running same action over and over again
                  if (currentBehavior != NONE) {
                     if(currentBehavior >= i) // If higher level thread, wait to complete..
                        while(!actionThread.done) {Thread.yield();}
                     behavior[currentBehavior].suppress();
                  }
                  // Make currentBehavior this one
                  currentBehavior = i;

                  // Run the currentBehavior.behaviorAction()
                  actionThread.execute(i);
                  Thread.yield();
               }  
               break; // Breaks out of for() loop
            }
         }
      }
   }

   /**
   * This class handles the action() methods of the Behaviors.
   */
   private class BehaviorAction extends Thread {
      public boolean done = true;
      int current = NONE;
      
      public void run() {
         while(true) {
         	synchronized(this)
         	{
            	if(current != NONE) {
               		done = false;
               		behavior[current].action();
               		current = NONE;
               		done = true;
            	}
            }
            Thread.yield();
         }
      }
      
      public synchronized void execute(int index) {
         current = index;
      }
   }
}

