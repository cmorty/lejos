package java.util;

/**
 * An iterator over a collection. Iterator takes the place of Enumeration in the Java collections framework
 * @author Juan Antonio Brena Moral, Sven Köhler
 */
public interface Iterator<T> 
{
	/**
	 * 
	 * @return Returns true if the iteration has more elements.
	 */
	public boolean hasNext();
	
	/**
	 * 
	 * @return Returns the next element in the interation.
	 */
	public T next();
	
	/**
	 * 
	 * Removes from the underlying collection the last element returned by the iterator (optional operation).
	 */	
	public void remove();
	
}
