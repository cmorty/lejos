package java.util;

/**
 * A task that can be scheduled for one-time or repeated execution by a Timer. 
 * 
 * @see Timer
 * 
 */
public abstract class TimerTask implements Runnable
{
  /**
   * If positive the next time this task should be run.
   * If negative this TimerTask is canceled or executed for the last time.
   */
  long scheduled;

  /**
   * If positive the last time this task was run.
   * If negative this TimerTask has not yet been scheduled.
   */
  long lastExecutionTime;

  /**
   * If positive the number of milliseconds between runs of this task.
   * If -1 this task doesn't have to be run more then once.
   */
  long period;

  /**
   * If true the next time this task should be run is relative to
   * the last scheduled time, otherwise it can drift in time.
   */
  boolean fixed;

  /**
   * Creates a TimerTask and marks it as not yet scheduled.
   */
  protected TimerTask()
  {
    this.scheduled = 0;
    this.lastExecutionTime = -1;
  }

  /**
   * Marks the task as canceled and prevents any further execution.
   * Returns true if the task was scheduled for any execution in the future
   * and this cancel operation prevents that execution from happening.
   * <p>
   * A task that has been canceled can never be scheduled again.
   * <p>
   * In this implementation the TimerTask it is possible that the Timer does
   * keep a reference to the TimerTask until the first time the TimerTask
   * is actually scheduled. But the reference will disappear immediatly when
   * cancel is called from within the TimerTask run method.
   */
  public boolean cancel()
  {
    boolean prevented_execution = (this.scheduled >= 0);
    this.scheduled = -1;
    return prevented_execution;
  }

    /**
     * The TimerTask must implement a run method that will be called by the
	 * Timer when the task is scheduled for execution. The task can check when
	 * it should have been scheduled and cancel itself when no longer needed.
	 * <p>
	 * Example:
	 * <pre>
	 *  Timer timer = new Timer();
	 *  TimerTask task = new TimerTask() {
	 *      public void run() {
	 *      if (this.scheduledExecutionTime() &lt; System.currentTimeMillis() + 500)
	 *          // Do something
	 *      else
	 *          // Complain: We are more then half a second late!
	 *      if (someStopCondition)
	 *          this.cancel(); // This was our last execution
	 *  };
	 *  timer.scheduleAtFixedRate(task, 1000, 1000); // schedule every second
	 * </pre>
	 * <p>
	 * Note that a TimerTask object is a one shot object and can only given once
	 * to a Timer. (The Timer will use the TimerTask object for bookkeeping,
	 * in this implementation).
	 * <p>
	 * This class also implements <code>Runnable</code> to make it possible to
	 * give a TimerTask directly as a target to a <code>Thread</code>.
	 *
     */
    public abstract void run();

  /**
   * Returns the last time this task was scheduled or (when called by the
   * task from the run method) the time the current execution of the task
   * was scheduled. When the task has not yet run the return value is
   * undefined.
   * <p>
   * Can be used (when the task is scheduled at fixed rate) to see the
   * difference between the requested schedule time and the actual time
   * that can be found with <code>System.currentTimeMillis()</code>.
   */
  public long scheduledExecutionTime()
  {
    return lastExecutionTime;
  }
}
