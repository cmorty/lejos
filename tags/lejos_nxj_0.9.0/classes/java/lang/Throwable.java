package java.lang;

import java.io.PrintStream;
import lejos.nxt.VM;

/**
 * All exceptions and errors extend this class.
 */
public class Throwable
{
    private int[] _stackTrace;
	private String _message;
	
	//MISSING Throwable getCause()
	//MISSING Throwable initCause(Throwable cause)
	//MISSING void printStackTrace(PrintWriter pw)

    /**
     * Create a Throwable object. Call fillInStackTrace to create a trace of
     * stack when the Throwable was created.
     */
	public Throwable() 
	{
		//nothing
        fillInStackTrace();
	}

    /**
     * Create a Throwable object. Call fillInStackTrace to create a trace of
     * stack when the Throwable was created. Set the message to the provided
     * string.
     * @param message Message providing details of the error/exception.
     */
	public Throwable(String message)
	{
        fillInStackTrace();
		_message = message;
	}
	
	/**
	 * Can be overridden, to return localized messages.
	 * The default implementation returns the same as {@link #getMessage()}.
	 * @return Localized message string or null if there is no message
	 */
	public String getLocalizedMessage()
	{
		return this.getMessage();
	}

    /**
     * Return the message associated with this Throwable object.
     * @return Message string or null if there is no message.
     */
	public String getMessage()
	{
		return _message;
	}

    /**
     * Return a string version of the Throwable. This will consist of details of
     * the actual class and the detail message if set.
     * @return A string representation.
     */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().toString());
        if (_message != null)
        {
            sb.append(": ");
            sb.append(this.getLocalizedMessage());
        }
        return sb.toString();
	}

    /**
     * Capture a stack trace. Note that the frames containing this (and other
     * calls directly relating to the Throwable object). will be omitted.
     * @return The Throwable object.
     */
    public Throwable fillInStackTrace()
    {
        _stackTrace = VM.createStackTrace(Thread.currentThread(), this);
        return this;
    }

    /**
     * Print details of the exception/error to the provided stream. The details
     * will contain the throwable class, the text of the detail message (if any)
     * and a series of lines providing a stack trace at the time the Throwable
     * was created.
     * @param s The print stream on which to output the trace.
     */
    public void printStackTrace(PrintStream s)
    {
        s.println(toString());
        if (_stackTrace != null)
        {
            for(int i : _stackTrace)
                s.println(" at: " + (i >> 16) + "(" + (i & 0xffff) + ")");
        }
    }

    /**
     * Print details of the exception/error on the system error stream. See
     */
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }
}
