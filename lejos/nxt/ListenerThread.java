package lejos.nxt;

/**
 * Utility class for dispatching events to button, sensor and serial listeners.
 *
 * @author Paul Andrews
 */
class ListenerThread extends Thread
{
  static ListenerThread singleton = new ListenerThread();
  private static final int MAX_LISTENER_CALLERS = 7;
  private static int [] masks;
  private static ListenerCaller [] listenerCallers;
  private static int numLC = 0;

  int mask;
  Poll poller = new Poll();   

  static ListenerThread get()
  {
      synchronized (singleton)
      {
          if (!singleton.isAlive())
          {
            masks = new int[MAX_LISTENER_CALLERS];
            listenerCallers = new ListenerCaller[MAX_LISTENER_CALLERS];
            singleton.setDaemon(true);
            singleton.setPriority(Thread.MAX_PRIORITY);
            singleton.start();
          }
      }
      
      return singleton;
  }

  void addToMask(int mask, ListenerCaller lc)
  {
     int i;
     this.mask |= mask;

     for(i=0;i<numLC;i++) if (listenerCallers[i] == lc) break;
     if (i == numLC) {
       masks[numLC] = mask;
       listenerCallers[numLC++] = lc;
     }
       
     // Interrupt the polling thread, not the current one!
     interrupt();
  }

  void addButtonToMask(int id, ListenerCaller lc) {
      addToMask(id << Poll.BUTTON_MASK_SHIFT, lc);
  }
  
  void addSensorToMask(int id, ListenerCaller lc) {
      addToMask(1 << id, lc);
  }

  void addSerialToMask(ListenerCaller lc) {
      addToMask(1 << Poll.SERIAL_SHIFT, lc);
  }
  
  public void run()
  {
      for (;;) {
    	  singleton.setPriority(Thread.MAX_PRIORITY);
          try  {
              int changed = poller.poll(mask, 0);
              
              // Run events at normal priority so they can use Thread.yield()
              singleton.setPriority(Thread.NORM_PRIORITY);

              for(int i=0;i<numLC;i++) 
                if ((changed & masks[i]) != 0) 
                  listenerCallers[i].callListeners();

          } catch (InterruptedException ie) {
          }
      }
  }
}
