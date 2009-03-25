package java.lang;

import java.util.Iterator;

/**
 * Interface needed by Java foreach loops. It just provides an Iterator.
 * 
 * @author Sven Köhler
 * @param <T> type of the elements
 */
public interface Iterable<T>
{
	Iterator<T> iterator();
}
