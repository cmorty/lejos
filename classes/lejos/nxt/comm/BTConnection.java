package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;


public class BTConnection implements StreamConnection {
	int handle;
	boolean open;
	
	public BTConnection(int handle)
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

