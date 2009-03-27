package java.util;

/**
 * This is non-public because it's not compatible with the JDK, yet.
 * 
 * @author Sven Köhler
 * @param <E> type of the elements
 */
abstract class AbstractList<E> extends AbstractCollection<E> implements List<E>
{
	public ListIterator<E> listIterator()
	{
		return this.listIterator(0);
	}

	public boolean contains(Object o)
	{
		return this.indexOf(o) >= 0;
	}

	public Iterator<E> iterator()
	{
		return this.listIterator();
	}
}
