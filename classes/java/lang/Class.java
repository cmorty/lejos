package java.lang;

/**
 * Not functional. It's here to satisfy javac and jikes.
 */
public class Class<T>
{
	/**
	 * @exception ClassNotFoundException Thrown always in TinyVM.
	 */
	@SuppressWarnings("unused")
	public static Class<?> forName (String aName)
		throws ClassNotFoundException
	{
		throw new ClassNotFoundException();
	}
	
	/**
	 * Always return false.
	 * @return false
	 */
	public boolean desiredAssertionStatus()
	{
		return false;
	}
}
