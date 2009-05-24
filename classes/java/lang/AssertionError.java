package java.lang;

public class AssertionError extends Error
{
	public AssertionError()
	{
		//FIXME generate proper messages
		super("assertion failed");
	}

	public AssertionError(boolean message)
	{
		this();
	}

	public AssertionError(char message)
	{
		this();
	}

	public AssertionError(double message)
	{
		this();
	}

	public AssertionError(float message)
	{
		this();
	}

	public AssertionError(int message)
	{
		this();
	}

	public AssertionError(long message)
	{
		this();
	}

	public AssertionError(Object message)
	{
		this();
	}
}
