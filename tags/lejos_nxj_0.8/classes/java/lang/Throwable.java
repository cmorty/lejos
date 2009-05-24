package java.lang;

/**
 * All exceptions and errors extend this class.
 */
public class Throwable
{
	private String _message;
	
	public Throwable() 
	{
		//nothing
	}

	public Throwable(String message)
	{
		_message = message;
	}

	public String getMessage()
	{
		return _message;
	}

	@Override
	public String toString()
	{
		return this.getMessage();
	}
}
