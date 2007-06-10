package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;

/**
 * 
 * Represents a USB Stream Connection.
 *
 */
public class USBConnection implements StreamConnection {
	boolean open;
	USBInputStream is = new USBInputStream();
	USBOutputStream os = new USBOutputStream();
	
	public USBConnection()
	{
		open = true;
	}

	public void close() throws IOException {
		open = false;
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(is);
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(os);
	}

	public InputStream openInputStream() throws IOException {
		return is;
	}

	public OutputStream openOutputStream() throws IOException {
		return os;
	}

}

