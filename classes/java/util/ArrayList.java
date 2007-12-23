package java.util;

/**
 * 
 * @author Andre Nijholt
 */
public class ArrayList {
	private static final int INITIAL_CAPACITY 	= 7;
	private static final int CAPACITY_INCREMENT = 3;
	
	protected Object[] elementData;
	protected int capacityIncrement;
	protected int elementCount;

	public ArrayList(int initialCapacity) {
	    if (initialCapacity < 0) initialCapacity = 0;
		elementData = new Object[initialCapacity];
		capacityIncrement = CAPACITY_INCREMENT;
		elementCount = 0;
	}
	
	public ArrayList(Object[] elements) {
		// Set initial capacity to 130% (normally specified 110%)
		this((elements.length * 13) / 10);
		addAll(elements);
	}

	public ArrayList() {
		this(INITIAL_CAPACITY);
	}

	public void add(int index, Object element) {
		if (index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}

		ensureCapacity(elementCount + 1);
		for (int i = elementCount; i > index; i--) {
			elementData[i] = elementData[i - 1];
		}
		elementData[index] = element;
		elementCount++;
	}
	

	public void add(Object o) {
	    ensureCapacity(elementCount + 1);
	    elementData[elementCount++] = o;
	}
	
	public void addAll(Object [] elements) {
		if (elements == null) return;
		ensureCapacity(elementCount + elements.length);
		
		for (int i = 0; i < elements.length; i++) {
			elementData[elementCount++] = elements[i];
		}
	}
	
	public void addAll(int index, Object [] elements) {
		if (elements == null) return;
		if (index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}

		ensureCapacity(elementCount + elements.length);
		for (int i = elementCount + elements.length - 1; i > index; i--) {
			elementData[i] = elementData[i - elements.length];
		}
		for (int i = 0; i < elements.length; i++) {
			elementData[i + index] = elements[i];
			elementCount++;
		}
	}
	
	public void clear() {
		for (int i = 0; i < elementCount; i++) {
			elementData[i] = null;
		}
	}
	
	public boolean contains(Object o) {
		for (int i = 0; i < elementCount; i++) {
			if (elementData[i].equals(o)) return true;
		}
		
		return false;
	}
	
	public Object get(int index) {
		if (index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return elementData[index];
	}
	
	public int indexOf(Object o) {
		for (int i = 0; i < elementCount; i++) {
			if (elementData[i].equals(o)) return i;
		}
		
		return -1;
	}

	public int lastIndexOf(Object o) {
		for (int i = elementCount - 1; i >= 0; i--) {
			if (elementData[i].equals(o)) return i;
		}
		
		return -1;
	}
	
	public boolean isEmpty() {
		return (elementCount == 0);
	}

	public Object remove(int index) {
		if (index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}

		Object o = elementData[index];
		for (int i = index; i < elementCount; i++) {
			elementData[i] = elementData[i + 1];
		}
		elementCount--;
		return o;
	}
	
	public Object set(int index, Object element) {
		if (index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}

		Object o = elementData[index];
		elementData[index] = element;
		return o;
	}
	
	public int size() {
		return elementCount;
	}

	private void ensureCapacity(int minCapacity) {
	    if (elementData.length < minCapacity) {
	    	int newCapacity = (capacityIncrement > 0)
	    		? (elementData.length + capacityIncrement)
	    		: (elementData.length * 2);
	    	if (newCapacity < minCapacity) {
	    		newCapacity = minCapacity;
	    	}

	    	Object oldData[] = elementData;
	    	elementData = new Object[newCapacity];
	    	System.arraycopy(oldData, 0, elementData, 0, elementCount);
	    }
	}
	



}
