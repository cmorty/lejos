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

	public DataInputStream openDataInputStream() {
		return new DataInputStream(is);
	}

	public DataOutputStream openDataOutputStream() {
		return new DataOutputStream(os);
	}

	public InputStream openInputStream() {
		return is;
	}

	public OutputStream openOutputStream() {
		return os;
	}

}

