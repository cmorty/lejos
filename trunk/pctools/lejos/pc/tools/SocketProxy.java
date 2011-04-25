package lejos.pc.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
* Socket Proxy for NXT
* Has two main functions. The first is to connect to an existing
* server socket on the specified port. The second function is to
* create a socket server. In this case the proxy assumes that the
* NXT will then send a command to inform the proxy of the next action
* to take
*
* Currently only supports TCP connections
*
* @author Ranulf Green and Lawrie Griffiths
*/
public class SocketProxy {
	private String host;
	private int port;
	private DataInputStream inFromNXT;
	private DataOutputStream outToNXT;
	private ServerSocket serverSocket;
	private Socket sock;

	/**
	 * Run the Socket Proxy.
	 * An instance of SocketProxy will allow for transparent forwarding
	 * of messages between server and NXT using a socket connection
	 * @throws IOException 
	 */
	private int run(String[] args) throws IOException {
		SocketProxyCommandLineParser fParser = new SocketProxyCommandLineParser(SocketProxy.class, "[options]");
		CommandLine commandLine;
		
		try
		{
			commandLine = fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(System.err, e);
			return 1;
		}
		
		if (commandLine.hasOption("h"))
		{
			fParser.printHelp(System.out);
			return 0;
		}
		
		int protocols = 0;
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		NXTConnector conn = new NXTConnector();
		conn.addLogListener(new ToolsLogger());
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;
		if (protocols == 0) protocols = NXTCommFactory.ALL_PROTOCOLS;
		boolean connected = conn.connectTo(name, address, protocols);
		if (!connected) {
			System.err.println("Failed to connect to NXT");
			return 1;
		}

		inFromNXT = conn.getDataIn();
		outToNXT = conn.getDataOut();
		
		// Check to see if socket is a server or a client
		boolean isServer = inFromNXT.readBoolean();
		if (isServer) {
			newSocketServer();
		} else {
			newSocketConnection();
		}
		return 0;
	}

	/**
	 * Creates a new socket server if instructed by the NXT
	 * @throws IOException
	 */
	private void newSocketServer() throws IOException {
		int port = inFromNXT.readInt();
		System.out.println("Waiting on " + port);
		serverSocket = new ServerSocket(port);
		while (true) {
			// Wait for command from NXT
			byte command = inFromNXT.readByte();
			if(command == 1){
				waitForConnection();
			}
		}
	}

	/**
	 * Allows negotiation of the accept() method of Socket server
	 * Executes a single accept and waits until the Socket is closed
	 * 
	 * @throws IOException
	 */
	private void waitForConnection() throws IOException {
		sock = serverSocket.accept();
		//System.out.println("Accepted");

		//	inform the NXT of the new Connection
		outToNXT.writeBoolean(true);
		outToNXT.flush();

		DataInputStream inFromSocket = new DataInputStream(sock.getInputStream());
		DataOutputStream outToSocket = new DataOutputStream(sock.getOutputStream());

		// Listen for incoming data from socket
		new Forward(sock, inFromSocket, outToNXT);

		// Listen for incoming data from NXT
		new ForwardNXT(sock, inFromNXT, outToSocket);
		
		// Wait for socket to close	
		while (!sock.isClosed()) Thread.yield();
	}

	/**
	 * Allows for a connection to be made using the details supplied from the NXT
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void newSocketConnection() throws UnknownHostException, IOException 
	{
		// The first byte from the NXT contains the length of the host name in chars
		int len = inFromNXT.readByte();
		char[] hostChars = new char[len];

		// Following the first byte the host name is transmitted
		for(int i=0;i<len;i++){
			hostChars[i] = inFromNXT.readChar();
		}
		
		// Following the host name an int containing the port number of the socket to connect to
		// is transmitted
		port = inFromNXT.readInt();
		host = new String(hostChars);

		System.out.println("Host: " + host + " port: " + port);
		
		// Create a socket connection with the specified host using the specified port
		sock = new Socket(host, port);
		outToNXT.writeBoolean(true);
		outToNXT.flush();

		DataInputStream inFromSocket = new DataInputStream(sock.getInputStream());

		DataOutputStream outToSocket = new 
		DataOutputStream(sock.getOutputStream());

		// Listen for incoming data from socket
		new Forward(sock, inFromSocket, outToNXT);

		// Send data to NXT
		ForwardNXT fn = new ForwardNXT(sock, inFromNXT, outToSocket);
		
		// Wait for socket to close	and the forward to NXT thread top die
		while (!sock.isClosed() || fn.isAlive()) Thread.yield();
	}

	/**
	 * Allows for the forwarding of messages from Socket to NXT
	 * @author Ranulf Green
	 */
	private class Forward extends Thread{
		private DataOutputStream dout;
		private DataInputStream din;
		private Socket sock;

		/**
		 * Constructor.
		 * @param sock the socket with which the connection is made
		 * @param dis the input stream to read
		 * @param dos the output stream to forward to
		 */
		public Forward(Socket sock, DataInputStream dis, DataOutputStream dos){
			super();
			din=dis;
			dout=dos;
			this.sock = sock;
			start();
		}
		/**
		 * Causes a new thread to be invoked
		 */
		public void run(){
			try{
				while(true){
					int in = din.read();
					if(in<0){ 
						sock.close();
						return;
					}
					dout.writeByte(in);
					dout.flush();
				}
			} catch (IOException ioe) {}
		}
	}

	/**
	 * Class to forward messages from NXT to socket
	 * @author Ranulf Green
	 *
	 */
	private class ForwardNXT extends Thread {
		private static final byte ESCAPE = (byte) 0xFF;
		private static final byte ESCAPE_CLOSE = 1;
		
		private DataOutputStream dout;
		private DataInputStream din;

		private Socket sock;

		/**
		 * Constructor.
		 * @param sock
		 * @param dis input stream from NXT
		 * @param dos output stream to socket
		 */
		public ForwardNXT(Socket sock, DataInputStream dis, DataOutputStream dos){
			super();
			din=dis;
			dout=dos;
			this.sock = sock;
			start();
		}

		/**
		 * Causes a new thread to be invoked
		 */
		public void run() {
			try {
				while(true) {
					int in = din.read();
					if (in < 0) {
						sock.close();
						return;
					}
					// Process ESCAPE sequence
					if ((byte) in == ESCAPE) {
						in = din.read();
						if ((byte) in == ESCAPE_CLOSE) {
							sock.close();
							return;							
						} else in = ESCAPE;
					}
					dout.writeByte(in);
					dout.flush();
				}
			} catch (IOException ioe) {ioe.printStackTrace();}
		}
	}

	public static void main(String[] args) {
		int r;
		try
		{
			r = new SocketProxy().run(args);
		}
		catch (Exception t)
		{
			t.printStackTrace(System.err);
			r = 1;
		}
		System.exit(r);
	}
}



