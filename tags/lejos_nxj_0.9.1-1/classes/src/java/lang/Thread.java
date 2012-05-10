package java.lang;
import lejos.nxt.VM;

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
  private UncaughtExceptionHandler uncaughtExceptionHandler;
  private static UncaughtExceptionHandler defaultUncaughtExceptionHandler;

  public static interface UncaughtExceptionHandler
  {
      void uncaughtException(Thread t, Throwable e);
  }
  
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
    // If the thread was created with a runnable it will be stored in waitingOn
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
  
  /**
   * Tests the interrupted state of the current thread. If it is interrupted it
   * will true and clear the interrupted state. Otherwise it will return false.
   * @return true if the current thread has been interrupted otherwise false
   */
  public static native boolean interrupted();
  
  /**
   * Tests to see if the current thread has been interrupted but leaves the
   * interrupted state unchanged.
   * @return true if the current thread has been interrupted otherwise false
   */
  public final native boolean isInterrupted();
  
  /**
   * Set the daemon flag. If a thread is a daemon thread its existence will
   * not prevent a JVM from exiting.
   * @return true if this thread has the daemon flag set otherwise false
   */
  public final native boolean isDaemon();
  
  /**
   * Sets the state of the threads daemon flag. If this flag is set then the
   * system will not wait for it to exit when all other none daemon threads
   * have exited. 
   * @param on the new state of the daemon flag
   */
  public final native void setDaemon(boolean on);
  
  /**
   * Waits for this thread to die.
   * @throws InterruptedException 
   */
  public final native void join() throws InterruptedException;

  /**
   * Waits for up to timeout mS for this thread to die.
   * @param timeout The period in ms to wait for this thread to die
   * @throws InterruptedException 
   */  
  public final native void join(long timeout) throws InterruptedException;

  /**
   * Set the default exception handler. This will be called for any uncaught
   * exceptions thrown by threads which do not have an uncaught exception
   * handler set.
   * @param handler The new exception handler
   */
  public static void setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler)
  {
      defaultUncaughtExceptionHandler = handler;
  }

  /**
   * returns the current default exception handler if set, or null if none is set.
   * @return the current exception handler
   */
  public static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()
  {
      return defaultUncaughtExceptionHandler;
  }

  /**
   * Sets the uncaught exception handler for this thread. This handler will be
   * called for any uncaught exceptions thrown bu this thread.
   * @param handler The new handler
   */
  public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler)
  {
      uncaughtExceptionHandler = handler;
  }

  /**
   * returns the current uncaught exception handler for this thread, if one has
   * has been set or null if none is set.
   * @return the current handler
   */
  public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler()
  {
      return uncaughtExceptionHandler;
  }

  /**
   * This is a special entry point called by the firmware to allow Java code
   * to handle uncaught exceptions. Note great care is needed here to avoid
   * loops due to exceptions being thrown while trying to handle the exception.
   * In particular if the system has run out of memory it is likely that the
   * Java code will end up throwing an out of memory exception. If this happens
   * we catch the exception and call the VM to display the original exception
   * information.
   * @param exception
   * @param method
   * @param pc 
   */  
  private static void systemUncaughtExceptionHandler(Throwable exception, int method, int pc)
  {
      try {
          // try the various possible handlers in turn
          Thread curThread = Thread.currentThread();
          if (curThread.getUncaughtExceptionHandler() != null)
              curThread.getUncaughtExceptionHandler().uncaughtException(curThread, exception);
          else if (defaultUncaughtExceptionHandler != null)
              defaultUncaughtExceptionHandler.uncaughtException(curThread, exception);
          else
              exception.uncaughtException(method, pc);
      } catch (Throwable e)
      {
          // If we get any exceptions let the system deal with it...
          VM.firmwareExceptionHandler(exception, method, pc);
      }
  }

  /**
   * This native call is used by the VM to end the current thread. It is
   * called when the initial method in the thread returns.
   */
  private final static native void exitThread();
}



