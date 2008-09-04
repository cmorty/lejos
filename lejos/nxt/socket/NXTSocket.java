package lejos.nxt.socket;

import java.io.*;
import lejos.nxt.comm.*;

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
	private USBConnection usbc;
	private String host;
	private int port;
	private boolean isBluetooth;
	private boolean isServer = false;

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
		isBluetooth = true;
		negotiateConnection();
		outToProxy.close();
		inFromProxy.close();
		
	}
	
	/**
	 * Constructor: Pass an open USB connection and socket details.
	 * @param host The name of the host with which the socket will be opened
	 * @param port The port to connect to
	 * @param usbc The USB connection
	 * @throws IOException If the bluetooth does not respond or the proxy is
	 * 	not running
	 */
	public NXTSocket(String host, int port, USBConnection usbc) throws IOException{
		this.host = host;
		this.port = port;
		this.usbc = usbc;
		inFromProxy = new DataInputStream(btc.openInputStream());
		outToProxy = new DataOutputStream(btc.openOutputStream());
		isBluetooth = false;
		negotiateConnection();
		outToProxy.close();
		inFromProxy.close();
	}
	
	/**
	 * Constructor. Use if the socket is intended not to connect to a host
	 * @param btc the connection the socket is made over
	 */
	public NXTSocket(BTConnection btc){
		this.btc = btc;
		isBluetooth = true;
	}
	
	/**
	 * Constructor for usb connnection. Does not connect to a host
	 * @param usbc The USB connection to use;
	 */
	public NXTSocket(USBConnection usbc){
		this.usbc = usbc;
		isBluetooth = false;
	}
	
	/**
	 * 
	 * Negotiates a connection between NXT and socket proxy
	 * @throws IOException if host name is invalid or connection fails
	 */
	private void negotiateConnection() throws IOException{
		if(host.length()==0) throw new IOException ();
		else{
			outToProxy = new DataOutputStream(btc.openOutputStream());
			outToProxy.writeBoolean(isServer);
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
		if(isBluetooth){return new DataInputStream(btc.openInputStream());}
		else{return new DataInputStream(usbc.openInputStream());}
	}

	/**
	 * Returns the data output stream of the socket
	 * @return The data output stream of the socket
	 * @throws IOException
	 */
	public DataOutputStream getDataOutputStream() throws IOException{
		if(isBluetooth){return new DataOutputStream(btc.openOutputStream());}
		else{return new DataOutputStream(usbc.openOutputStream());}
	}
	
	/**
	 * Returns the output stream associated with this socket
	 * @return The output stream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException{
		if(isBluetooth){return btc.openOutputStream();}
		else {return usbc.openOutputStream();}
	}
	
	/**
	 * Returns the input stream associated with this socket
	 * @return The input stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException{
		if(isBluetooth){return btc.openInputStream();}
		else{return usbc.openInputStream();}
	}
	
}


