package java.util;

/**
 * An exception thrown by some stack class methods
 * to indicate that the stack is empty.
 */
public class EmptyStackException extends RuntimeException {

	/**
	 * Creates a new exception with null message string.
	 */
    public EmptyStackException() {
    	super();
    }

}