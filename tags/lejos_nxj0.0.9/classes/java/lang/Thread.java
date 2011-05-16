package java.lang;

/**
 * A thread of execution (or task). Now handles priorities, daemon threads
 * and interruptions.
 */
public class Thread implements Runnable
{
  /**
   * The minimum priority that a thread can have. The value is 1.
   */
  public final static int MIN_PRIORITY = 1;

 /**
  * The priority that is assigned to the primordial thread. The value is 5.
  */
  public final static int NORM_PRIORITY = 5;

  /**
   * The maximum priority that a thread can have. The value is 10.
   */
  public final static int MAX_PRIORITY = 10;

  // Note 1: This class cannot have a static initializer.

  // Note 2: The following fields are used by the VM.
  // Their sizes and location can only be changed
  // if classes.h is changed accordingly. Needless
  // to say, they are read-only.

  private Thread _TVM_nextThread;
  private Object _TVM_waitingOn;
  private int _TVM_sync;
  private int _TVM_sleepUntil;
  private Object _TVM_stackFrameArray;
  private Object _TVM_stackArray;
  private byte _TVM_stackFrameArraySize;
  private byte _TVM_monitorCount;
  private byte _TVM_threadId; 
  private byte _TVM_state; 
  private byte _TVM_priority; 
  private byte _TVM_interrupted; 
  private byte _TVM_daemon; 

  // Extra instance state follows:
  
  private String name;

  public final boolean isAlive()
  {
    return _TVM_state > 1;
  }    

  private void init(String name, Runnable target)
  {
  	Thread t = currentThread();
	if (t == null)
		setPriority(NORM_PRIORITY);
	else
    {
		setPriority(t.getPriority());
        setDaemon(t.isDaemon());
    }
    this.name = name;
    // This is a little naughty. We should not really use the internal fields
    // of the task block. However the waitingOn field can not and is not used
    // until after the thread has been started, so it is reasonably safe to use
    // it to hold the target since this will only be used when the thread
    // is first run. Also this saves haveing an extra ref. in such a basic
    // type.
    this._TVM_waitingOn = target;
  }
  
  public Thread()
  {
    init("", null);
  }

  public Thread (String name)
  {
    init(name, null);
  }

  public Thread(Runnable target)
  {
      init("", target);
  }
  
  public Thread(Runnable target, String name)
  {
      init(name, target);
  }
  public void run()
  {
    // If the thead was created with a runnable it will be stored in waitingOn
    // Note it is not safe to use this field again after this point.
    if (_TVM_waitingOn != null)
      ((Runnable)_TVM_waitingOn).run();
  }
  
  public final native void start();
  public static native void yield();
  public static native void sleep (long aMilliseconds) throws InterruptedException;
  public static native Thread currentThread();
  public final native int getPriority();

  /**
   * Returns the string name of this thread.
   * @return The name of this thread.
   */
  public String getName()
  {
      return name;
  }

  /**
   * Sets the string name associated with this thread.
   * @param name The new name of the thread.
   */
  public void setName(String name)
  {
      this.name = name;
  }
  /**
   * Set the priority of this thread. Higher number have higher priority.
   * The scheduler will always run the highest priority thread in preference
   * to any others. If more than one thread of that priority exists the
   * scheduler will time-slice them. In order for lower priority threas
   * to run a higher priority thread must cease to be runnable. i.e. it
   * must exit, sleep or wait on a monitor. It is not sufficient to just
   * yield.
   * <P>
   * Threads inherit the priority of their parent. The primordial thread
   * has priority NORM_PRIORITY.
   *
   * @param priority must be between MIN_PRIORITY and MAX_PRIORITY.
   */
  public final native void setPriority(int priority);
  
  /**
   * Set the interrupted flag. If we are asleep we will wake up
   * and an InterruptedException will be thrown.
   */
  public native void interrupt();
  public static native boolean interrupted();
  public final native boolean isInterrupted();
  
  /**
   * Set the daemon flag. If a thread is a daemon thread its existence will
   * not prevent a JVM from exiting.
   */
  public final native boolean isDaemon();
  public final native void setDaemon(boolean on);
  
  /**
   * Join not yet implemented
   */
  public final native void join() throws InterruptedException;
  public final native void join(long timeout) throws InterruptedException;
}



