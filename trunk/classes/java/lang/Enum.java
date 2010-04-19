package java.lang;

/**
 * 
 * @author Sven Köhler
 */
public abstract class Enum<E extends Enum<E>> implements Comparable<E>
{
	//MISSING implements Serializable
	
	private int ordinal;
	private String name;
	
	protected Enum(String name, int ordinal)
	{
		this.name = name;
		this.ordinal = ordinal;
	}
	
	@Override
	protected final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	public final int compareTo(E o)
	{
		if (this.getDeclaringClass() != o.getDeclaringClass())
			throw new ClassCastException();
		
		if (this.ordinal == o.ordinal)
			return 0;
		
		return (this.ordinal > o.ordinal) ? 1 : -1;
	}
	
	@Override
	public final boolean equals(Object o)
	{
		return this==o;
	}
	
	@Override
	protected final void finalize()
	{
		//nothing
	}
	
	@SuppressWarnings("unchecked")
	public final Class<E> getDeclaringClass()
	{
		Class<?> c1 = this.getClass();
		Class<?> c2 = c1.getSuperclass();
		return (Class<E>)((c2 == Enum.class) ? c1 : c2);
	}
	
	@Override
	public final int hashCode()
	{
		return super.hashCode();
	}
	
	public final String name()
	{
		return this.name;
	}
	
	public final int ordinal()
	{
		return this.ordinal;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	/**
	 * @deprecated not implemented in leJOS 
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public static<T extends Enum<T>> T valueOf(Class<T> enumclas, String name)
	{
		throw new UnsupportedOperationException();	
	}
}
