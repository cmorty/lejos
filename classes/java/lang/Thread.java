package java.lang;

/**
 * A thread of execution (or task).<p>
 * 
 * Thread objects represent the unit of execution within leJOS. Each thread
 * object when started will execute concurrently with other threads. The system
 * automatically creates the initial thread and calls the programs entry point.
 * <p>
 * In leJOS thread scheduling is purely priority based. Lower priority threads will
 * not run at all if there is a higher priority thread that is runnable. Threads of
 * equal priority will be time sliced on a round robin basis, the current time slice
 * used by leJOS is 2ms. <br>
 * <b>Note:</b> This means that if a thread calls yield and there are no threads of 
 * equal priority available to run then the original thread will continue to
 * execute (but with a new time slice). yield can not be used to allow lower
 * priority threads to execute. 
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

  /**
   * Returns true if the thread has been started and has not yet terminated.
   * @return true if the thread is alive, false if not
   */
  public final boolean isAlive()
  {
    return _TVM_state > 1;
  }    

  /**
   * Initialise a new thread object
   * @param name string name of the thread
   * @param target the method to be called when the thread starts
   */
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

  /**
   * Create a new Thread. The thread will inherit the priority and daemon state 
   * of the creating thread.
   * Execution is started by calling the start method.
   */
  public Thread()
  {
    init("", null);
  }

  /**
   * Create a new Thread. The thread will inherit the priority and daemon state 
   * of the creating thread.
   * Execution is started by calling the start method. The thread will have the specified
   * name.
   * @param name The name to attach to this thread object
   */
  public Thread (String name)
  {
    init(name, null);
  }

  /**
   * Create a new Thread. The thread will inherit the priority and daemon state of the creating thread.
   * Execution is started by calling the start method. If target is not null then the run method
   * of target will be called, when the thread starts. If target is null then the run method of
   * the thread object will be called.
   * @param target The object whose run method is called
   */
  public Thread(Runnable target)
  {
      init("", target);
  }
 
  /**
   * Create a new Thread. The thread will inherit the priority and daemon state of the creating thread.
   * Execution is started by calling the start method. If target is not null then the run method
   * of target will be called, when the thread starts. If target is null then the run method of
   * the thread object will be called.
   * @param target The object whose run method is called
   * @param name The name to be used for the thread
   */
  public Thread(Runnable target, String name)
  {
      init(name, target);
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    // If the thead was created with a runnable it will be stored in waitingOn
    // Note it is not safe to use this field again after this point.
    if (_TVM_waitingOn != null)
      ((Runnable)_TVM_waitingOn).run();
  }
  
  /**
   * Called to start the execution of the new thread. A thread can only be started once.
   */
  public final native void start();
  
  /**
   * Temporally suspends execution of this thread to allow other threads to run.
   */
  public static native void yield();
  
  /**
   * Suspends execution of the thread for the specified amount of time.
   * @param aMilliseconds Number of milliseconds to sleep for
   * @throws InterruptedException
   */
  public static native void sleep (long aMilliseconds) throws InterruptedException;
  
  /**
   * Return a reference to the currently executing thread.
   * @return The current thread
   */
  public static native Thread currentThread();
  
  /**
   * Returns the priority of this thread.
   * @return the thread priority
   */
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
   * scheduler will time-slice them. In order for lower priority threads
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
  
  /**
   * Test to see if the current thread is in an interrupted state, if so return true,
   * otherwise return false. After this call the thread will no longer be in an interrupted state.
   * 
   * @return true if the thread is in an interrupted state, false otherwise
   */
  public static native boolean interrupted();
  
  /**
   * Tests to see if the thread is in an interrupted state, but does not clear this state.
   * @return true if the thread is in an interrupted state, false otherwise
   */
  public final native boolean isInterrupted();
  
  /**
   * Set the daemon flag. If a thread is a daemon thread its existence will
   * not prevent a JVM from exiting.
   */
  public final native boolean isDaemon();
  
  /**
   * Sets the daemon state of the thread. If a thread is a daemon thread its existence will
   * not prevent a JVM from exiting.
   * @param on set to true to mark the thread as daemon, false otherwise
   */
  public final native void setDaemon(boolean on);
  

  /**
   * Waits for the thread to terminate, or for the operation to be interrupted.
   * @throws InterruptedException
   */
  public final native void join() throws InterruptedException;
  
  /**
   * Waits for the thread to terminate, or for the specified amount of time, or for the current thread to
   * be interrupted.
   * @param timeout The maximum time to wait in milliseconds
   * @throws InterruptedException
   */
  public final native void join(long timeout) throws InterruptedException;
}



