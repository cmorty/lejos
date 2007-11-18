package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;

/**
 * 
 * Represents a Bluetooth Stream Connection.
 * 
 */
public class BTConnection implements StreamConnection {
	private int handle;
	private boolean open, streamOpen;
	private BTInputStream is = new BTInputStream();
	private BTOutputStream os = new BTOutputStream();
	
	/**
	 * Create a connection for the given handle.
	 * 
	 * @param handle the handle for the connection
	 *
	 */
	BTConnection(int handle)
	{
		this.handle = handle;
		open = true;
		streamOpen = true;
	}

	/**
	 * Close the connection.
	 * 
	 */
	public void close() throws IOException {
		//Bluetooth.closeConnection((byte) handle);
		Bluetooth.btSetCmdMode(1);
		open = false;
		streamOpen = false;
	}

	/**
	 * Return the DataInputStream for this connect
	 * 
	 * @return the data input stream
	 */
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(is);
	}

	/**
	 * Return the DataOutputStream for this connection.
	 * 
	 * @return the data output stream
	 */
	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(os);
	}

	/**
	 * Return the InputStream for this connection.
	 * 
	 * @return the input stream
	 */
	public InputStream openInputStream() throws IOException {
		return is;
	}

	/**
	 * Return the OutputStream for this connection
	 * 
	 * @return the output stream
	 * 
	 */
	public OutputStream openOutputStream() throws IOException {
		return os;
	}
	
	/**
	 * Close the stream for this connection.
	 * This suspends the connection and switch the BC4 chip to command mode.
	 *
	 */
	public void closeStream() {
		streamOpen = false;
		Bluetooth.btSetCmdMode(1);
		try {Thread.sleep(100);} catch (InterruptedException ioe) {}
	}
	
	/**
	 * Open the stream for this connection.
	 * This resumes the connection and switches the BC4 chip to data mode.
	 *
	 */
	public void openStream() {
		Bluetooth.openStream((byte) handle);
		try {Thread.sleep(100);} catch (InterruptedException ioe) {}
		Bluetooth.btSetCmdMode(0);
		streamOpen = true;
	}
	
	/**
	 * Get the signal strength of this connection.
	 * This necessitates closeing and reopening the data stream.
	 *  
	 * @return a value from 0 to 255
	 */
	public int getSignalStrength() {
		closeStream();
		int strength = Bluetooth.getSignalStrength((byte) handle); 
		openStream();
		return strength;
	}
}

