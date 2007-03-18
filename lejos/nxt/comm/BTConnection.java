package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;

/**
 * 
 * Represents a Bluetooth Stream Connection.
 * 
 * Currently only used to close a Bluetooth connection,
 * but will be used in a later release to implement input
 * and output streams.
 *
 */
public class BTConnection implements StreamConnection {
	int handle;
	boolean open;
	
	BTConnection(int handle)
	{
		this.handle = handle;
		open = true;
	}

	public void close() throws IOException {
		Bluetooth.btSetCmdMode(1);
		open = false;
	}

	public DataInputStream openDataInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream openInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public OutputStream openOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

