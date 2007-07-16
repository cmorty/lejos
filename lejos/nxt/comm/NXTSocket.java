package lejos.nxt.comm;

import java.io.*;

/**
* NXTSocket. Allows a NXT to establish a connection with a remote Socket
* server via a proxy server.
*
* @author Ranulf Green
* @version 1.0
*/

public class NXTSocket{

	private DataOutputStream outToProxy;
	private DataInputStream inFromProxy;
	private BTConnection btc;
	private String host;
	private int port;

	/**
	 * Constructor: Pass an open bluetooth connection and socket details.
	 * @param host The name of the host with which the socket will be opened
	 * @param port The port to connect to
	 * @param btc The bluetooth connection
	 * @throws IOException If the bluetooth does not respond or the proxy is
	 * 	not running
	 */
	public NXTSocket(String host, int port, BTConnection btc) throws IOException{
		this.host = host;
		this.port = port;
		this.btc = btc;
		inFromProxy = new DataInputStream(btc.openInputStream());
		outToProxy = new DataOutputStream(btc.openOutputStream());
		negotiateConnection();
		outToProxy.close();
		inFromProxy.close();
	}

	/**
	 * Negotiates a connection between NXT and socket proxy
	 * @throws IOException if host name is invalid or connection fails
	 */
	private void negotiateConnection() throws IOException{
		if(host.length()==0) throw new IOException ();
		else{
			outToProxy = new DataOutputStream(btc.openOutputStream());
			outToProxy.writeByte(host.length());
			outToProxy.writeChars(host);
			outToProxy.writeInt(port);
			outToProxy.flush();
		}
		if(!inFromProxy.readBoolean()){
			throw new IOException();
		}
	}

	/**
	 * Returns the data input stream of the socket
	 * @return The data input stream of the socket
	 * @throws IOException
	 */
	public DataInputStream getDataInputStream() throws IOException{
		return new DataInputStream(btc.openInputStream());
	}

	/**
	 * Returns the data output stream of the socket
	 * @return The data output stream of the socket
	 * @throws IOException
	 */
	public DataOutputStream getDataOutputStream() throws IOException{
		return new DataOutputStream(btc.openOutputStream());
	}
}


