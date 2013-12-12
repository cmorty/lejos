package lejos.pc.charting;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base class for charting logger comms manager
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractConnectionManager implements ConnectionProvider {
	protected InputStream in = null;
	protected OutputStream out = null;
	/**
	 * Set this as the passed parameter in your connect() method implementation. 
	 */
	protected String connectedDeviceName = null;

	public AbstractConnectionManager() {
		super();
	}

	public abstract boolean isConnected();
	public abstract void closeConnection();
	public abstract boolean connect(String NXT);
	
	/** Return the name of the NXT last successfully connected to.
	 * @return name of the NXT
	 */
	public String getConnectedName() {
	    return this.connectedDeviceName;
	}

	/** Return the <code>InputStream</code> from the NXT.
	 * @return the <code>InputStream</code>
	 */
	public InputStream getInputStream() {
	    return this.in;
	}

	/** Return the <code>OutputStream</code> to the NXT.
	 * @return the <code>OutputStream</code>
	 */
	public OutputStream getOutputStream() {
	    return this.out;
	}

	

}