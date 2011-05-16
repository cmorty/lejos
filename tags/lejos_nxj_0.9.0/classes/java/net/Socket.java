package java.net;

import java.io.*;
import lejos.nxt.comm.*;

/**
* Socket. Allows a NXT to establish a connection with a remote Socket
* server via a proxy server.
* 
* Version 1.1 uses NXTConnection and does not need to distinguish between USB and Bluetooth.
*
* @author Ranulf Green & Lawrie Griffiths
* @version 1.1
*/
public class Socket{
	private DataOutputStream outToProxy;
	private DataInputStream inFromProxy;
	private NXTConnection nxtc;
	private String host;
	private int port;
	private boolean isServer = false;
	private NXTSocketOutputStream os;
	
	private static final int BUFFER_SIZE = 64;
	
	/**
	 * Constructor: Pass an open NXT connection and socket details.
	 * @param host The name of the host with which the socket will be opened
	 * @param port The port to connect to
	 * @param nxtc The NXT connection
	 * @throws IOException If the host does not respond or the proxy is
	 * 	not running
	 */
	public Socket(String host, int port, NXTConnection nxtc) throws IOException {
		this.host = host;
		this.port = port;
		this.nxtc = nxtc;
		inFromProxy = new DataInputStream(nxtc.openInputStream());
		outToProxy = new DataOutputStream(nxtc.openOutputStream());
		negotiateConnection();
		outToProxy.close();
		inFromProxy.close();
	}
	
	/**
	 * Constructor. Use if the socket is intended not to connect to a host
	 * @param nxtc the connection the socket is made over
	 */
	public Socket(NXTConnection nxtc) {
		this.nxtc = nxtc;
	}
	
	/**
	 * 
	 * Negotiates a connection between NXT and socket proxy
	 * @throws IOException if host name is invalid or connection fails
	 */
	private void negotiateConnection() throws IOException {
		if (host.length()==0) throw new IOException ();
		outToProxy = new DataOutputStream(nxtc.openOutputStream());
		outToProxy.writeBoolean(isServer);
		outToProxy.writeByte(host.length());
		outToProxy.writeChars(host);
		outToProxy.writeInt(port);
		outToProxy.flush();
		if (!inFromProxy.readBoolean()) {
			throw new IOException();
		}
	}

	/**
	 * Returns the output stream associated with this socket
	 * @return The output stream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		os =  new NXTSocketOutputStream(nxtc, BUFFER_SIZE);
		return os;
	}
	
	/**
	 * Returns the input stream associated with this socket
	 * @return The input stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return nxtc.openInputStream();
	}
	
	/**
	 * Write Escape sequence to indicate end of file
	 */
	public void close() {
		try {
			os.writeClose();
		} catch (IOException e) {
			// Ignore exception
		}
	}	
}


