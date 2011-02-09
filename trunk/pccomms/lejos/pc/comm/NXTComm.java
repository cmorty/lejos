package lejos.pc.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.remote.NXTCommRequest;

/**
 * 
 * Interface that all NXTComm implementation classes must implement for low-level communication
 * with the NXT.
 *
 */
public interface NXTComm extends NXTCommRequest {
    public static final int PACKET = 0;
    public static final int LCP = 1;
    public static final int RAW = 2;
            
	/**
	 * Search for NXTs over USB, Bluetooth or both
	 * @param name name of the NXT or null
	 * @param protocol bitwise combination of NXTCommFactory.BLUETOOTH and NXTCommFactory.USB
	 * @return a NXTInfo object describing the NXt found and the connection to it
	 * @throws NXTCommException
	 */
	public NXTInfo[] search(String name, int protocol) throws NXTCommException;

	/**
	 * Connect to a NXT found by a search or created from mname and address.
	 * 
	 * @param nxt the NXTInfo object for the NXT
     * @param mode the mode for the connection
	 * @return true iff the open succeeded
	 * @throws NXTCommException
	 */
	public boolean open(NXTInfo nxt, int mode) throws NXTCommException;

	/**
	 * Connect to a NXT found by a search or created from mname and address.
	 * 
	 * @param nxt the NXTInfo object for the NXT
	 * @return true if the open succeeded
	 * @throws NXTCommException
	 */
	public boolean open(NXTInfo nxt) throws NXTCommException;
		
	/**
	 * Read data from a NXT that has an open connection.
	 * Used for stream connections.
	 * 
	 * @return the data
	 * @throws IOException
	 */
	public byte[] read() throws IOException;
	
	/**
	 * Request the number of bytes available to read.
	 * 
	 * @return the number of bytes available
	 * @throws IOException
	 */
	public int available() throws IOException;
	
	/**
	 * Write data to a NXT that has an open connection.
	 * 
	 * @param data the data to be written.
	 * Used for stream connections.
	 * 
	 * @throws IOException
	 */
	public void write(byte [] data) throws IOException;
	
	/**
	 * Return an OutputStream for writing a stream of data to the NXT over this connection.
	 * 
	 * @return the OutputStream object
	 */
	public OutputStream getOutputStream();
	
	/**
	 * Return an InputStream for reading a stream of data from the NXT over this connection.
	 * 
	 * @return the InputStream object
	 */
	public InputStream getInputStream();
}
