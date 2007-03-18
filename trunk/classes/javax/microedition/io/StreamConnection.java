package javax.microedition.io;
import java.io.*;

/**
 * 
 * This interface defines the capabilities that a stream connection must have.
 * 
 * StreamConnections have one underlying InputStream and one OutputStream. 
 * 
 * Opening a DataInputStream counts as opening an InputStream and opening a DataOutputStream counts as opening an OutputStream. 
 * 
 * Trying to open another InputStream or OutputStream causes an IOException. 
 * 
 * Trying to open the InputStream or OutputStream after they have been closed causes an IOException
 *
 */
public interface StreamConnection {
	
	/**
	 * Close the stream connection
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Open and return an InputStream - not yet implemented.
	 *
	 */
	public InputStream openInputStream() throws IOException;
	
	/**
	 * Open and return a DataInputStream - not yet implemented.
	 */
	public DataInputStream openDataInputStream() throws IOException;
	
	/**
	 * Open and return an OutputStream - not yet implemented.
	 */
	public OutputStream openOutputStream() throws IOException;
	
	/**
	 * Open and return a DataOutputStream - not yet implemented.
	 */
	public DataOutputStream openDataOutputStream() throws IOException;
}
