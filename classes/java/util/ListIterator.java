package java.util;

public interface ListIterator<E> extends Iterator<E>
{
	int nextIndex();
	int previousIndex();
	
	boolean hasPrevious();
	E previous();
	
	void add(E e);
	void set(E e);
}
