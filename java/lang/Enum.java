package java.lang;

/**
 * 
 * @author Sven Köhler
 */
public abstract class Enum<E extends Enum<E>> implements Comparable<E>
{
	//MISSING implements Serializable
	//MISSING public final Class<E> getDeclaringClass()
	
	private int ordinal;
	private String name;
	
	protected Enum(String name, int ordinal)
	{
		this.name = name;
		this.ordinal = ordinal;
	}
	
	protected final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	public final int compareTo(E o)
	{
		//TODO enable this check that moment that we have working java.lang.Class
		//if (this.getDeclaringClass() != o.getDeclaringClass())
		//	throw new ClassCastException();
		
		if (this.ordinal == o.ordinal)
			return 0;
		
		return (this.ordinal > o.ordinal) ? 1 : -1;
	}
	
	public final boolean equals(Object o)
	{
		return this==o;
	}
	
	protected final void finalize()
	{
		//nothing
	}
	
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
	
	public String toString()
	{
		return this.name;
	}
	
	/**
	 * @deprecated not implemented in leJOS 
	 */
	public static<T extends Enum<T>> T valueOf(Class<T> enumclas, String name)
	{
		throw new UnsupportedOperationException();	
	}
}
