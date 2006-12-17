package josx.util;

/**
 * Represents a recyclable object.
 * @see josx.util.Recycler
 */
public abstract class AbstractRecyclable implements Recyclable {
	private Recyclable nextRecyclable;
	
	/**
	 * Initializes the Recyclable. 
	 */
	public abstract void init();
	
	/**
	 * Called by users when this Recyclable is no longer needed.
	 */
	public abstract void release();

	/**
	 * Must return Recyclable most recently set with
	 * <code>setNextRecyclable</code>.
	 * @see josx.util.Recyclable#setNextRecyclable
	 */
	public Recyclable getNextRecyclable() {
	    return this.nextRecyclable;
	}

	/**
	 * Stores a Recyclable object.
	 * @see josx.util.Recyclable#getNextRecyclable
	 */
	 public void setNextRecyclable (Recyclable r) {
		this.nextRecyclable = r; 
	 }
	
}
