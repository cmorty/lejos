
/**
 * An <code>AssertionFailedError</code> will be thrown 
 * when an assertion failed.
 * 
 * <p>
 * lejosunit changes:
 * <ul>
 *   <li>
 *     support of a message attribute, as lejos does NOT yet support
 *     constructor with a message.  
 *   </li>
 * </ul>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AssertionFailedError extends Error {

    /** 
     * The assertion message given with the constructor.
     */
// Commented out, to reduce size
//    private String message = null;

    /**
     * Default constructor
     */
    public AssertionFailedError() {
        super ();
    }

    /**
     * Constructor with an given explanation message.
     * 
     * @param aMessage the given explanation message
     */
    public AssertionFailedError(String aMessage) {
        super();
// Commented out, to reduce size
//        this.message = aMessage;
    }

    /**
     * Gets the message of the assertion failure.
     * 
     * @return the message of the assertion
     */
// Commented out, to reduce size
//    public String getMessage() {
//        return message;
//    }
}
