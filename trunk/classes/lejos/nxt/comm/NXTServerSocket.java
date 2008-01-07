package lejos.nxt.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.NXTSocket;
import lejos.nxt.comm.USBConnection;

/**
 * 
 * Communicates with a ServerProxy on the PC to provide a
 * ServerSocket interface applications on the NXT.
 *
 */
public class NXTServerSocket {

	private int port;
	private BTConnection btc;
	private USBConnection usbc;
	private boolean isBluetooth;
	private final boolean isServer = true;
	
	
	
	/**
	 * Constructor. Creates a new Server Socket over an open bluetooth connection
	 * @param port The port to listen on
	 * @param btc The bluetooth connection to open
	 * @throws IOException 
	 */
	public NXTServerSocket(int port, BTConnection btc) throws IOException{
		this.port = port;
		this.btc = btc;
		isBluetooth = true;
		negotiateConnection();
	}
	
	/**
	 * Constructor. Creates a new Server Socket over an open usb connection
	 * @param port The port to listen on
	 * @param usbc The usb connection to open
	 * @throws IOException 
	 */
	public NXTServerSocket(int port, USBConnection usbc) throws IOException{
		this.port = port;
		this.usbc = usbc;
		isBluetooth = false;
		negotiateConnection();
	}
	
	private void negotiateConnection() throws IOException{
		DataOutputStream dos = openDataOutputStream();
		dos.writeBoolean(isServer);
		dos.writeInt(port);
		dos.flush();
		dos.close();
		
	}
	
	private DataOutputStream openDataOutputStream() throws IOException{
		DataOutputStream dos;
		if(isBluetooth){dos = new DataOutputStream(btc.openOutputStream());}
		else{dos = new DataOutputStream(usbc.openOutputStream());}
		return dos;
	}
	
	private DataInputStream openDataInputStream() throws IOException{
		DataInputStream dis;
		if(isBluetooth){dis = new DataInputStream(btc.openInputStream());}
		else{dis = new DataInputStream(usbc.openInputStream());}
		return dis;
	}
	
	/**
	 * Waits untill there is a socket connection available. When this becomes true
	 * a new NXTSocket is returned
	 * @return NXTSocket
	 * @throws IOException 
	 */
	public NXTSocket accept() throws IOException{
		DataOutputStream dos = openDataOutputStream();
		DataInputStream dis = openDataInputStream();
		// inform the proxy of the command
		dos.writeByte(1);
		dos.flush();
		dis.readBoolean();
		dos.close();
		dis.close();
		if(isBluetooth){return new NXTSocket(btc);}
		else{return new NXTSocket(usbc);}
		
	}

}
