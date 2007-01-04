package lejos.util;

/**
 * Represents a recyclable object.
 * @see lejos.util.Recycler
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
	 * @see lejos.util.Recyclable#setNextRecyclable
	 */
    public Recyclable getNextRecyclable();

	/**
	 * Stores a Recyclable object.
	 * @see lejos.util.Recyclable#getNextRecyclable
	 */
    public void setNextRecyclable (Recyclable r);	
}
