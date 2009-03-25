package java.lang;

/**
 * All classes extend this one, implicitly.
 */
public class Object
{
	protected Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException("LeJOS doesn't support cloning");
	}
	
	public boolean equals (Object aOther)
	{
		return this == aOther;
	}
	
	/**
	 * @deprecated not implemented in leJOS 
	 */
	protected void finalize()
	{
		//nothing
	}

	/**
	 *Returns <code>null</code>. It's here to satisfy javac.
	 */
	public final Class<?> getClass()
	{
		throw new UnsupportedOperationException();
	}

	public int hashCode()
	{
		return System.identityHashCode(this);
	}

	/**
	 * Wake up one thread blocked on a wait(). Must be synchronized on
	 * this object otherwise an IllegalMonitorStateException will be thrown.
	 * <P>
	 * If multiple threads are waiting, higher priority threads will be woken
	 * in preference, otherwise the thread that gets woken is essentially
	 * random. 
	 */
	public final native void notify();
	
	/**
	 * Wake up all threads blocked on a wait(). Must be synchronized on
	 * this object otherwise an IllegalMonitorStateException will be thrown.
	 */
	public final native void notifyAll();
	
	public String toString()
	{
		int hash = System.identityHashCode(this);
		return "Object@"+Integer.toHexString(hash);
	}
	
	/**
	 * This is the same as calling wait(0).
	 * TODO make this a Java method that calls wait(0) since native methods are expensive?
	 */
	public final native void wait() throws InterruptedException;
	
	/**
	 * Wait until notified. Must be synchronized on this object otherwise
	 * an IllegalMonitorStateException will be thrown. The wait can
	 * terminate if one of the following things occurs:
	 * <ol>
	 * <li>notify() or notifyAll() is called.
	 * <li>The calling thread is interrupted.
	 * <li>The timeout expires.
	 * </ol>
	 * @param timeout maximum time in milliseconds to wait. Zero means forever.
	 */
	public final native void wait(long timeout) throws InterruptedException;
	
	public final void wait(long timeout, int nanos) throws InterruptedException
	{
		//rounding up
		if (nanos > 0)
			timeout++;
		
		this.wait(timeout);
	}
}







