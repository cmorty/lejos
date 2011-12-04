package java.lang;

public class AssertionError extends Error
{
	public AssertionError()
	{
		super();
	}
	
	public AssertionError(String message)
	{
		super(message);
	}

	public AssertionError(boolean message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(char message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(double message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(float message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(int message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(long message)
	{
		this(String.valueOf(message));
	}

	public AssertionError(Object message)
	{
		this(String.valueOf(message));
		
		if (message instanceof Throwable)
		{
			//TODO init cause
			//this.initCause((Throwable) message);
		}
	}
}
