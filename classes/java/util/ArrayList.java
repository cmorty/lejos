package java.util;

/**
 * An expandable array.
 * 
 * @author Andre Nijholt
 */
public class ArrayList {
  private static final int INITIAL_CAPACITY   = 7;

  private static final int CAPACITY_INCREMENT = 3;

  protected Object[]       elementData;

  protected int            capacityIncrement;

  protected int            elementCount;

  /**
   * Create an array list.
   * 
   * @param initialCapacity The initial size of the array list.
   */
  public ArrayList(int initialCapacity) {
    elementData = new Object[initialCapacity < 0 ? 0 : initialCapacity];
    capacityIncrement = CAPACITY_INCREMENT;
    elementCount = 0;
  }

  /**
   * Create an array list.
   * 
   * @param elements The initial elements in the array list.
   */
  public ArrayList(Object[] elements) {
    this((elements.length * 13) / 10);
    addAll(elements);
  }

  /**
   * Create an array list.
   */
  public ArrayList() {
    this(INITIAL_CAPACITY);
  }

  /**
   * Add a element at a specific index.
   * 
   * @param index The index at which the element should be added.
   * @param element The element to add.
   */
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

  /**
   * Add a element at the end of the array list.
   * 
   * @param element The element to add.
   */
  public void add(Object element) {
    ensureCapacity(elementCount + 1);
    elementData[elementCount++] = element;
  }

  /**
   * Add all elements from the array to the array list.
   * 
   * @param elements The array of elements to add.
   */
  public void addAll(Object[] elements) {
    if (elements == null) return;
    ensureCapacity(elementCount + elements.length);

    for (int i = 0; i < elements.length; i++) {
      elementData[elementCount++] = elements[i];
    }
  }

  /**
   * Add all elements from the array to the array list at a specific index.
   * 
   * @param index The index to start adding elements.
   * @param elements The array of elements to add.
   */
  public void addAll(int index, Object[] elements) {
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

  /**
   * Clear the array list.
   */
  public void clear() {
    for (int i = 0; i < elementCount; i++) {
      elementData[i] = null;
    }
  }

  /**
   * Check if a specific element is contained in the array list.
   * 
   * @param element The element in question.
   * @return true, if the element is contained in the array list.
   */
  public boolean contains(Object element) {
    for (int i = 0; i < elementCount; i++) {
      if (elementData[i].equals(element)) return true;
    }

    return false;
  }

  /**
   * Get a specific element.
   * 
   * @param index The index of the wanted element.
   * @return The wanted element.
   */
  public Object get(int index) {
    if (index > elementCount) {
      throw new ArrayIndexOutOfBoundsException();
    }

    return elementData[index];
  }

  /**
   * Get the first index of a specific element.
   * 
   * @param element The wanted element.
   * @return The index of the wanted element, or -1 if not found.
   */
  public int indexOf(Object element) {
    for (int i = 0; i < elementCount; i++) {
      if (elementData[i].equals(element)) return i;
    }

    return -1;
  }

  /**
   * Get the last index of a specific element.
   * 
   * @param element The wanted element.
   * @return The index of the wanted element, or -1 if not found.
   */
  public int lastIndexOf(Object element) {
    for (int i = elementCount - 1; i >= 0; i--) {
      if (elementData[i].equals(element)) return i;
    }

    return -1;
  }

  /**
   * Check if array list is empty.
   * 
   * @return true if the array list is empty.
   */
  public boolean isEmpty() {
    return (elementCount == 0);
  }

  /**
   * Remove a element at a specific index.
   *
   * @param index The index of the element to remove.
   * @return the removed element.
   */
  public Object remove(int index) {

    if (index > elementCount) {
      throw new ArrayIndexOutOfBoundsException();
    }

    Object element = elementData[index];
    for (int i = index; i < elementCount - 1; i++) {
      elementData[i] = elementData[i + 1];
    }
    elementData[--elementCount] = null;

    return element;
  }

  /**
   * Replace an element at a specific index with a new element.
   * 
   * @param index The index of the element to set.
   * @param element The new element.
   * @return the old element.
   */
  public Object set(int index, Object element) {
    if (index > elementCount) {
      throw new ArrayIndexOutOfBoundsException();
    }

    Object o = elementData[index];
    elementData[index] = element;
    return o;
  }

  /**
   * Get the number of elements in this array list.
   * 
   * @return the number of elements.
   */
  public int size() {
    return elementCount;
  }

  /**
   * Ensure that we have suffiecient capacity in the array to store
   * the requested number of elements. Expand the array if required.
   * @param minCapacity
   */
  private void ensureCapacity(int minCapacity) {
    if (elementData.length < minCapacity) {
      int newCapacity = (capacityIncrement > 0) ? (elementData.length + capacityIncrement) : (elementData.length * 2);
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }

      Object oldData[] = elementData;
      elementData = new Object[newCapacity];
      System.arraycopy(oldData, 0, elementData, 0, elementCount);
    }
  }
}
