package lejos.addon.icp;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>
 * <b>ConnectorAdapter</b> provides a foundation implementation of the
 * IcpConnector interface, including several convenience constructors and
 * reasonable defaults for the get...() methods.
 * </p>
 * 
 * <p>
 * Other implementations of the IcpConnector interface may wish to extend
 * this class and decorate only the necessary methods (connect() and reconnect()).
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 *
 * @see IcpConnector#connect()
 * @see IcpConnector#reconnect()
 */
public abstract class IcpConnectorAdapter implements IcpConnector {

    /** Target name/address of the iControlPad this connector is bound to */
    protected String target;
    
    /** PIN to use when pairing with the given iControlPad */
    protected byte[] pin;
    
    /** InputStream from the iControlPad (once connected) */
    protected InputStream is;

    /** OutputStream from the iControlPad (once connected) */
    protected OutputStream os;

    
    /**
     * <p>
     * Creates a connector bound to the given target and using
     * the given PIN for pairing.  This only sets the parameters;
     * actual pairing is done in the connect() and reconnect()
     * methods (and may be handled by a subclass).
     * </p>
     * 
     * @param target The name/address to pair with
     * @param pin The PIN to use when pairing
     */
    protected IcpConnectorAdapter(String target, byte[] pin) {
	if (null == target) {
	    throw new IllegalArgumentException("Connector target cannot be null");
	}
	this.target = target;
	this.pin = pin;
    }
    

    /**
     * <p>
     * Convenience constructor allowing a PIN specified as
     * a char array.
     * </p>
     * 
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, byte[])
     */
    protected IcpConnectorAdapter(String target, char[] pin) {
	this(target, new byte[pin.length]);
	for (int i = 0; i < pin.length; i++) {
	    this.pin[i] = (byte)pin[i];
	}
    }


    /**
     * <p>
     * Convenience constructor allowing a PIN specified as
     * a String.
     * </p>
     * 
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, byte[])
     */
    protected IcpConnectorAdapter(String target, String pin) {
	this(target, pin.getBytes());
    }


    /**
     * <p>
     * Convenience constructor that uses a default PIN.
     * </p>
     * 
     * @see IcpConnector#DEFAULT_PIN
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, byte[])
     */
    protected IcpConnectorAdapter(String target) {
	this(target, DEFAULT_PIN);
    }


    /**
     * <p>
     * Default implementation of the reconnect() method.
     * Sets input and output streams to null, but does not
     * actually attempt reconnection.  By default, this will
     * cause an Icp object using this connector to throw an
     * exception if any reconnection is attempted.  Classes
     * extending this one should override this method and
     * provide appropriate reconnection logic.
     * </p>
     * 
     * @see IcpConnector#reconnect()
     */
    public void reconnect() {
	is = null;
	os = null;
    }

    
    /**
     * <p>
     * Returns the target bound to this connector during construction.
     * </p>
     * 
     * @return String The target name/address
     * 
     * @see IcpConnector#getTarget()
     */
    public String getTarget() {
	return target;
    }
    
    
    /**
     * @see IcpConnector#getInputStream()
     */
    public InputStream getInputStream() {
	return is;
    }
    

    /**
     * @see IcpConnector#getOutputStream()
     */
    public OutputStream getOutputStream() {
	return os;
    }

}
