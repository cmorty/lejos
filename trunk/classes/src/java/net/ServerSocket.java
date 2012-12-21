package java.net;

import java.io.*;
import lejos.nxt.comm.*;

/**
 * 
 * Communicates with a ServerProxy on the PC to provide a
 * ServerSocket interface applications on the NXT.
 *
 */
public class ServerSocket {

	private int port;
	private NXTConnection nxtc;
	private final boolean isServer = true;
	
	protected ServerSocket() {
		// This is a hack so DexterSocketServer can extend this class without starting a BT connection. 
	}
	
	/**
	 * Constructor. Creates a new Server Socket over a Bluetooth or USB connection
	 * @param port The port to listen on
	 * @throws IOException 
	 */
	public ServerSocket(int port) throws IOException {
		this.port = port;
		this.nxtc = NXTSocketUtils.getNXTConnection();
		negotiateConnection();
	}
	
	private void negotiateConnection() throws IOException {
		DataOutputStream dos = openDataOutputStream();
		dos.writeBoolean(isServer);
		dos.writeInt(port);
		dos.flush();
		dos.close();		
	}
	
	private DataOutputStream openDataOutputStream() throws IOException {
		DataOutputStream dos;
		dos = new DataOutputStream(nxtc.openOutputStream());
		return dos;
	}
	
	private DataInputStream openDataInputStream() throws IOException {
		DataInputStream dis;
		dis = new DataInputStream(nxtc.openInputStream());
		return dis;
	}
	
	/**
	 * Waits until there is a socket connection available. When this becomes true
	 * a new Socket is returned
	 * @return Socket the socket
	 * @throws IOException 
	 */
	public Socket accept() throws IOException {
		DataOutputStream dos = openDataOutputStream();
		DataInputStream dis = openDataInputStream(); 
		
		// Inform the proxy of the command
		dos.writeByte(1);
		dos.flush();
		dis.readBoolean();
		dos.close();
		dis.close();
		return new Socket();		
	}
	
	public void close() {
		// TODO: This would be a good method to implement.
	}
}
