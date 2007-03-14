package lejos.subsumption;


/**
 * An activity that operates in coordination with other activities.
 * Only one activity can run at a time.
 * When an activity of a higher priority wants to run any activities
 * of a lower priority will be suppressed. Furthermore, if there is
 * already an activity of the same or lower priority running, that
 * activity will be stopped.
 * <P>
 * This is essentially an alternative  way of implementing a Behavior or
 * Subsumption architecture. It is totally distinct from the Behavior
 * and Arbitrator classes and does not use either.
 * <P>
 * It works like this:
 * <OL>
 * <LI>This class should be sub-classed and the action() method should
 * be implemented to perform the desired action. That may be to respond
 * to a change in a sensor, or it may be to cause the robot to wander
 * around (or anything else).
 * This method should call one of the pause() methods occasionally. In
 * particular if it wants to sleep or yield control it should call pause()
 * instead of sleep() or yield() or wait().
 * <LI>An application should create instances of the sub-classes as needed,
 * set their priorities by calling setPriority() and then start them
 * by calling start().
 * <LI>If an activity wants to run it should call activity.iWantToRun(), or
 * another class could call it.
 * <LI>If there is no higher priority activity
 * running when an activity  become runnable, any activities of the same or
 * lower priority will be suppressed. In addition, if an activity was not
 * already running the action() method will be called in its own thread.
 * <LI>If an activity is already running when it is run again, pause() throws
 * an InterruptedException. It can do what it wants with that. An activity that
 * is run when a sensor is activated might want to restart action() from the
 * beginning for example. A background activity might just ignore it.
 * <LI>If a higher priority activity gets to run, pause() on this activity will
 * throw a StopException. This should not be caught so that the action()
 * method exits with that exception.
 * </OL>
 * Note. A background activity should ensure that something makes it runnable
 * if it is stopped. The easiest way to do that is to override resetRunnable()
 * to call iWantToRun().
 * <P>
 * Example:
 * <pre>
 * &#47;**
 *  * When sensor one is pressed, run the motors in some pattern.
 *  *&#47;
 * class ControlMotors extends Activity implements PortListener
 * {
 * 	public ControlMotors()
 * 	{
 * 		Port.S1.addPortListener(this);
 * 	}
 * 
 * 	&#47;**
 * 	 * Called when the sensor state changes (in some thread other than this one).
 * 	 *&#47;	
 * 	public void stateChanged(Port s, int old, int nu)
 * 	{
 * 		if (old &gt; nu)
 * 			return;
 * 
 * 		iWantToRun();
 * 	}
 *     
 * 	&#47;**
 * 	 * Encapsulates the actual activity we want to perform.
 * 	 *
 * 	 * @exception StopException if we are forcibly stopped.
 * 	 *&#47;
 * 	protected void action() throws StopException
 * 	{
 * 		boolean finished = false;
 * 		
 * 		&#47;&#47; Sit here until we are finished or we are forcibly halted.					
 * 		while (!finished)
 * 		{
 * 			try
 * 			{
 * 				&#47;&#47; spin
 * 				Motor.C.forward();
 * 				Motor.A.backward();
 * 				
 * 				&#47;&#47; Wait for 0.25 secs, may throw InterruptedException
 * 				pause(250);
 * 	        		
 * 				&#47;&#47; Forward
 * 				Motor.A.forward();
 * 
 * 				&#47;&#47; We are finished
 * 				finished = true;
 * 			} catch (InterruptedException ie)
 * 			{
 * 				&#47;&#47; pause() was interrupted. Re-start from the beginning
 * 			}
 *		}
 *	}
 * }
 * 
 * </pre>
 * @author Paul Andrews
 */
public abstract class Activity extends ActivityBase
{
	private static final StopException stopException = new StopException();
	private static Activity runnable;

/******* Methods that CAN be overridden in a derived class *******/
	
	/**
	 * Encapsulates the actual activity we want to perform.
	 * Returns when complete or when stopped.
	 *
	 * @exception StopException when some other activity has stopped this one.
	 */
	protected abstract void action() throws StopException;

	/**
	 * Reset the runnable activity. Normal activities should not
	 * override this. Background activities can in order to request that
	 * they become runnable.
	 */
	protected void resetRunnable()
	{
		if (runnable == this)
			runnable = null;
	}
	
/******* Methods that CAN'T be overridden in a derived class *******/

	/**
	 * Call this if you want this activity to run.
	 */	
	protected final void iWantToRun()
	{
		synchronized(monitor)
		{
			// Only one activity can actually run.
			if (runnable == null || this.getPriority() >= runnable.getPriority())
			{
				// Wake up the thread represented by 'this'.
				// That is not necessarily the thread that is actually
				// executing iWantToRun();
				runnable = this;
				this.interrupt();
				
				// Tell everyone else to reset.
				monitor.notifyAll();
			}
			
			// Otherwise, that's just too bad.
		}
	}
	
	/**
	 * Thread entry point. Never returns.
	 */	
	public final void run()
	{
		synchronized (monitor)
		{
			while (true)
			{
				try
				{
					// Call this here so that background activities can make
					// themselves runnable.
					resetRunnable();		    

					// Wait until we become runnable
					try { pause(); } catch (InterruptedException ie) {}
					
					// Perform action.
					action();
					
					// Execution complete. Could be other things waiting to go,
					// so reset them. They won't get to run until we release
					// the monitor in pause();	
				    monitor.notifyAll();
			    } catch (StopException e)
			    {
			     	// Execution stopped forcibly
			    }
		    }
	    }
	}
	
	/**
	 * Wait at most 'time' milliseconds. Returning
	 * without an exception means continue the activity where it left off.
	 *
	 * @exception InterruptedException if we were made runnable whilst
	 * we were running. This might indicate, for example, that a sensor
	 * was pressed while we were still reacting to an earlier press.
	 * Typically, an activity would want to restart execution of the
	 * action() method from the beginning if that happened.
	 * @exception StopException if we should stop executing altogether.
	 */		
	protected final void pause(long time)
		throws InterruptedException, StopException
	{
		monitor.wait(time);
		if (runnable != this)
			throw stopException;
	}

	/**
	 * Wait until we've either been made runnable or someone else has.
	 *
	 * @exception InterruptedException if we should restart execution of the
	 * action() method from the beginning.
	 * @exception StopException if we should stop executing altogether.
	 */		
	protected final void pause()
		throws InterruptedException, StopException
	{
		monitor.wait();
		if (runnable != this)
			throw stopException;
	}
}

