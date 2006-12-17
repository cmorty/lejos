package josx.util;

/**
 * Represents a recyclable object.
 * @see josx.util.Recycler
 */
public interface Recyclable {
	/**
	 * Called when the Recyclable is allocated by a Recycler.
	 */
	public void init();
	
	/**
	 * Called when this Recyclable is no longer needed.
	 * Resources should be disposed in this method, e.g. this is where
	 * any nested Recyclables would be released.
	 */
	public void release();

	/**
	 * Must return Recyclable most recently set with
	 * <code>setNextRecyclable</code>.
	 * @see josx.util.Recyclable#setNextRecyclable
	 */
    public Recyclable getNextRecyclable();

	/**
	 * Stores a Recyclable object.
	 * @see josx.util.Recyclable#getNextRecyclable
	 */
    public void setNextRecyclable (Recyclable r);	
}
