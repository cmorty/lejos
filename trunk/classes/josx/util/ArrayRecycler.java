package josx.util;

/**
 * An array recycler for Object arrays. To use this utility, create
 * a global instance of this class, and then invoke
 * <code>allocate(int)</code> to create recyclable arrays.
 * As usual, invoke <code>recycle(...)</code> to release the array. 
 * <p>
 * Note that the caller is expected to provide
 * thread safety for instances of this class.
 * 
 * @see josx.util.RecyclableArray
 */
public final class ArrayRecycler extends Recycler {
	private int requestedLength;
	
    /**
     * Constructs a recycler.
     */
    public ArrayRecycler() {
    }
    
    /**
     * Attempts to obtain a free RecyclableArray.
     * @return A RecyclableArray reference.
     * @throws java.lang.StackOverflowError May be thrown due to the recursive implementation of the method.
     */
    public final RecyclableArray allocate (int length) {
		RecyclableArray array1;
		this.requestedLength = length;
        array1 = (RecyclableArray) allocate();
		if (array1.getCapacity() >= length) {
			array1.init (length);
			return array1;
		}
		try {
		   return allocate (length);
		} finally {
		   // Must not recycle before calling allocate (length).
		   recycle (array1);
		}
	}	    

	protected final Recyclable createInstance() {
		return new RecyclableArray (this.requestedLength);
	}
}
