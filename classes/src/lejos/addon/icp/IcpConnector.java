package lejos.addon.icp;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>
 * The <b>IcpConnector</b> interface defines methods required for any
 * class that establishes a connection to an iControlPad device on
 * behalf of an Icp object.
 * </p>
 * 
 * <p>
 * Implementors of this interface should consider extending the
 * ConnectorAdapter class, which provides sane defaults for many of
 * the instance variables and methods required by this interface.
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 *
 * @see IcpConnectorAdapter
 */
public interface IcpConnector {

    /** Default PIN for the iControlPad ("1234") */
    public static final byte[] DEFAULT_PIN = {(byte) '1', (byte) '2', (byte) '3', (byte) '4'};

    
    /**
     * <p>
     * Establishes a connection to an iControlPad.  The connector
     * should attempt to connect to the target specified by
     * getTarget().
     * </p>
     */
    public void connect();

    
    /**
     * <p>
     * Attempts to re-establish a connection to an iControlPad.
     * This may be the same operation as connect(), or it may perform
     * additional cleanup or retry logic.  In the simplest case, an
     * implementation may provide an empty implementation, which would
     * force the Icp object to retry (unsucessfully) forever.
     */
    public void reconnect();

    
    /**
     * <p>
     * Returns the target name or address that this connector is bound
     * to.  connect() and reconnect() should operate on this same value.
     * </p>
     * 
     * @return String The target name or address of the iControlPad
     */
    public String getTarget();

    
    /**
     * <p>
     * Gets the input stream associated with the iControlPad connection.
     * If no valid connection exists, it should return null.
     * </p>
     * 
     * @return InputStream The input stream from the iControlPad, or
     *                     null if no valid connection exists.
     */
    public InputStream getInputStream();
    

    /**
     * <p>
     * Gets the output stream associated with the iControlPad connection.
     * If no valid connection exists, it should return null.
     * </p>
     * 
     * @return OutputStream The output stream from the iControlPad, or
     *                      null if no valid connection exists.
     */
    public OutputStream getOutputStream();

}
