package lejos.addon.icp;


/**
 * <p>
 * An <b>IcpTimeoutException</b> is thrown when communication with the
 * iControlPad is lost.  The code automatically attempts to reconnect
 * when no response is received from the device, but after a certain
 * number of attempts it gives up and throws this exception.  Code
 * using the methods in the Icp class that query the iControlPad should
 * catch this exception to properly handle the case when no data is
 * available from the device.
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 */
public class IcpTimeoutException extends Exception {

    
    /**
     * @see Exception#Exception()
     */
    public IcpTimeoutException() {
	this("I/O timeout communicating with iCP");
    }


    /**
     * @see Exception#Exception(String)
     */
    public IcpTimeoutException(String message) {
	super(message);
    }

    
    /**
     * @see Exception#Exception(Throwable)
     */
    public IcpTimeoutException(Throwable cause) {
	super(cause);
    }


    /**
     * @see Exception#Exception(String, Throwable)
     */
    public IcpTimeoutException(String message, Throwable cause) {
	super(message, cause);
    }

}
