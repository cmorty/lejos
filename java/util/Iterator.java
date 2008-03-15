package java.util;
/**
 * An iterator over a collection. Iterator takes the place of Enumeration in the Java collections framework
 * @author Juan Antonio Breña Moral
 *
 */
public interface Iterator {

	/**
	 * 
	 * @return Returns true if the iteration has more elements.
	 */
	public boolean hasNext();
	
	/**
	 * 
	 * @return Returns the next element in the interation.
	 */
	public Object next();
	
	/**
	 * 
	 * @return Removes from the underlying collection the last element returned by the iterator (optional operation).
	 */	
	public void remove();
	
}
