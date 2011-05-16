package lejos.pc.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class NXJSocketProxy
{
	/* TODO fix design problem
	 * There are several problem with this proxy. First of all, a socket
	 * consists of two streams, which can be closed separately. However,
	 * currently the proxy does not support to close the streams independently.
	 * Also, if the peer closes the InputStream, it is not possible to signal
	 * EOF to the NXT. Escape sequences are only allowed in one direction, which
	 * is one of the main causes of this problem. Also, there does not seem to be
	 * any way of reporting errors back to NXT in case connecting or writing fails.
	 */
	

	/**
	 * Run the Socket Proxy.
	 * An instance of SocketProxy will allow for transparent forwarding
	 * of messages between server and NXT using a socket connection
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private int run(String[] args) throws IOException, InterruptedException {
		SocketProxyCommandLineParser fParser = new SocketProxyCommandLineParser(NXJSocketProxy.class, "[options]");
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
		String name = AbstractCommandLineParser.getLastOptVal(commandLine, "n");
		String address = AbstractCommandLineParser.getLastOptVal(commandLine, "d");
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

		DataInputStream inFromNXT = conn.getDataIn();
		DataOutputStream outToNXT = conn.getDataOut();
		
		// Check to see if socket is a server or a client
		boolean isServer = inFromNXT.readBoolean();
		if (isServer) {
			newSocketServer(inFromNXT, outToNXT);
		} else {
			newSocketConnection(inFromNXT, outToNXT);
		}
		return 0;
	}

	/**
	 * Creates a new socket server if instructed by the NXT
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void newSocketServer(DataInputStream inFromNXT, DataOutputStream outToNXT) throws IOException, InterruptedException {
		int port = inFromNXT.readInt();
		System.out.println("Waiting on " + port);
		ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			// Wait for command from NXT
			byte command = inFromNXT.readByte();
			if(command == 1){
				Socket sock = serverSocket.accept();
				//System.out.println("Accepted");

				handleNewConnection(sock, inFromNXT, outToNXT);
			}
		}
	}

	/**
	 * Allows negotiation of the accept() method of Socket server
	 * Executes a single accept and waits until the Socket is closed
	 * @param outToNXT 
	 * @param inFromNXT 
	 * @param serverSocket 
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void handleNewConnection(Socket sock, DataInputStream inFromNXT, DataOutputStream outToNXT) throws IOException, InterruptedException {
		//	inform the NXT of the new Connection
		outToNXT.writeBoolean(true);
		outToNXT.flush();

		pipeData(sock, inFromNXT, outToNXT);		
		sock.close();
	}

	private void pipeData(Socket sock, DataInputStream inFromNXT, DataOutputStream outToNXT)
		throws IOException, InterruptedException
	{
		InputStream inFromSocket = sock.getInputStream();
		OutputStream outToSocket = sock.getOutputStream();

		// Listen for incoming data from socket
		Thread t1 = new ForwardSocketToNXT(sock, inFromSocket, outToNXT);
		// Listen for incoming data from NXT
		Thread t2 = new ForwardNXTToSocket(sock, inFromNXT, outToSocket);
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	/**
	 * Allows for a connection to be made using the details supplied from the NXT
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void newSocketConnection(DataInputStream inFromNXT, DataOutputStream outToNXT) throws UnknownHostException, IOException, InterruptedException 
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
		int port = inFromNXT.readInt();
		String host = new String(hostChars);

		System.out.println("Host: " + host + " port: " + port);
		
		// Create a socket connection with the specified host using the specified port
		Socket sock = new Socket(host, port);
		outToNXT.writeBoolean(true);
		outToNXT.flush();

		pipeData(sock, inFromNXT, outToNXT);
		sock.close();
	}

	/**
	 * Allows for the forwarding of messages from Socket to NXT
	 * @author Ranulf Green
	 */
	private static class ForwardSocketToNXT extends Thread{
		private OutputStream dout;
		private InputStream din;
		private Socket sock;

		/**
		 * Constructor.
		 * @param sock the socket with which the connection is made
		 * @param dis the input stream to read
		 * @param dos the output stream to forward to
		 */
		public ForwardSocketToNXT(Socket sock, InputStream dis, OutputStream dos){
			super();
			this.din=dis;
			this.dout=dos;
			this.sock = sock;
		}
		/**
		 * Causes a new thread to be invoked
		 */
		public void run(){
			try{
				byte[] buffer = new byte[4096];
				while(true){
					int len = din.read(buffer);
					if(len < 0){
						// TODO don't close socket and signal EOF to NXT somehow
						// dout.close(); is not possible since stream might be reused.
						this.sock.close();
						break;
					}
					dout.write(buffer, 0, len);
					dout.flush();
				}
			} catch (IOException ioe) {
				//TODO print error message
			}
		}
	}

	/**
	 * Class to forward messages from NXT to socket
	 * @author Ranulf Green
	 *
	 */
	private static class ForwardNXTToSocket extends Thread {
		private static final int ESCAPE = 0xFF;
		private static final int ESCAPE_CLOSE = 1;
		
		private OutputStream dout;
		private InputStream din;
		private Socket sock;

		/**
		 * Constructor.
		 * @param sock
		 * @param dis input stream from NXT
		 * @param dos output stream to socket
		 */
		public ForwardNXTToSocket(Socket sock, InputStream dis, OutputStream dos){
			super();
			this.din=dis;
			this.dout=dos;
			this.sock = sock;
		}

		/**
		 * Causes a new thread to be invoked
		 */
		public void run() {
			try {
				//TODO make this more efficient by reading and writing large chunks of data
				while(true) {
					int in = din.read();
					if (in < 0) {
						this.sock.close();
						break;
					}
					// Process ESCAPE sequence
					if (in == ESCAPE) {
						in = din.read();
						if (in == ESCAPE_CLOSE) {
							//TODO don't close socket, and signal EOF to peer and exit thread
							// this.sock.shutdownOutput();
							sock.close();
							return;							
						}
						
						//TODO what to do when EOF after ESCAPE (in < 0)?
						in = ESCAPE;
					}
					dout.write(in);
					dout.flush();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		ToolStarter.startTool(NXJSocketProxy.class, args);
	}

	public static int start(String[] args) throws Exception
	{
		return new NXJSocketProxy().run(args);
	}
}
