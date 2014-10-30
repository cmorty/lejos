package java.util;

/**
 * A FIFO Queue of objects.
 */
public interface Queue<E> extends Collection<E> {
	
	//TODO uncomment all the other methods (needs implementation in LinkedList)

	/**
	 * Adds an element to the tail of the queue if the capacity of the queue would
	 * not been exceeded. Otherwise, it throws an Exception.
	 * 
	 * @param e the element to be added to the queue
	 * @return true
	 * @throws IllegalStateException if the capacity would be exceeded by adding the element
	 */
	boolean add(E e);

	/**
	 * Returns the element at the head of the queue. The element is not removed. 
	 * Unlike {@link #peek()}, it throws an Exception if the queue is empty. 
	 * 
	 * @return the head element
	 * @throws NoSuchElementException if the queue is empty
	 */
//	E element();

	/**
	 * Adds an element to the tail of the queue if the capacity of the queue would
	 * not been exceeded. Otherwise, it returns false.
	 * 
	 * @param e the element to be added to the queue
	 * @return true if element was added, false otherwise
	 */
//	boolean offer(E e);

	/**
	 * Returns the element at the head of the queue. The element is not removed. 
	 * If the queue is empty, null is returned. Note that null is also returned
	 * if the head element is null.
	 * 
	 * @return the head element or null if the queue is empty
	 */
//	E peek();

	/**
	 * Removes and returns the element at the head of the queue.
	 * If the queue is empty, null is returned. Note that null is also returned
	 * if the head element is null.
	 * 
	 * @return the head element or null if the queue is empty
	 */
//	E poll();

	/**
	 * Removes and returns the element at the head of the queue.
	 * Unlike {@link #poll()}, it throws an Exception if the queue is empty.
	 * 
	 * @return the head element
	 * @throws NoSuchElementException if the queue is empty
	 */
//	E remove();
}
