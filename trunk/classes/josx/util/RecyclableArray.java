package josx.util;

/**
 * A recyclable array. It should be
 * allocated using an instance of <code>ArrayRecycler</code>.
 * @see josx.util.ArrayRecycler
 */
public class RecyclableArray extends AbstractRecyclable {
	private static final RuntimeException INDEX_EXCEPTION = new ArrayIndexOutOfBoundsException();
	private final Object[] buffer;
	private int length;
	
	RecyclableArray (int capacity) {
	    buffer = new Object[capacity];	
	}
	
    public final void init() {
	}
	
	final void init (int length) {
		this.length = length;
		Object[] arr = this.buffer;
		for (int i = 0; i < length; i++) {
			arr[i] = null;
		}
	}
	
	public final void release() {
		// Nothing to do
	}	
	
	final int getCapacity() {
	    return this.buffer.length;	
	}
	
	public final int getLength() {
	    return this.length;	
	}
	
	public final Object get (int index) {
		if (index >= this.length)
			throw INDEX_EXCEPTION;
	    return this.buffer[index];	
	}
	
	public final void put (int index, Object o) {
		if (index >= this.length)
			throw INDEX_EXCEPTION;
        this.buffer[index] = o;	    
	}
}
