package java.lang;

public class IllegalArgumentException extends RuntimeException{

    /**
	* Constructs an <code>IllegalArgumentException</code> with no
	* detail message.
	*/
    public IllegalArgumentException() {
        super();
    }

    /**
	 * Constructs an <code>IllegalArgumentException</code> with the
	 * specified detail message.
	*
	* @param   s   the detail message.
	*/
    public IllegalArgumentException(String s) {
        super(s);
    }

    /**
	 * Constructs an <code>IllegalArgumentException</code> with the
	 * specified detail message and cause.
	 *
     * @param message The detail message
     * @param cause  The cause
     */
    public IllegalArgumentException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
	 * Constructs an <code>IllegalArgumentException</code> with the
	 * specified cause.
	 *
     * @param cause  The cause
     */
    public IllegalArgumentException(Throwable cause)
    {
        super(cause);
    }

}
