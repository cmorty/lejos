package lejos.pc.tools;

import java.io.*;
import java.net.*;
import lejos.pc.comm.*;

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
* @author Ranulf Green
* @version 1.0
*/
public class SocketProxy {

	private String host;
	private int port;
	private DataInputStream inFromNXT;
	private DataOutputStream outToNXT;
	private ServerSocket serverSocket;
	private Socket sock;

	/**
	 * Constructor
	 * An instance of Socket proxy will allow for transparent forwarding
	 * of messages between server and NXT using a socket connection
	 * @param NXTName The name of the NXT to connect to
	 * @param NXTaddress The physical address of the NXT
	 */
	public SocketProxy(String NXTName, String NXTaddress){
		try {
			//  create a Bluetooth connection with the NXT
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			NXTInfo[] nxtInfo = new NXTInfo[1];

			nxtInfo[0] = new NXTInfo(NXTName,NXTaddress);

			System.out.println("Connecting to " + nxtInfo[0].btResourceString);

			// check to see if NXT really exists, if not exit
			if (!nxtComm.open(nxtInfo[0])) {
				System.out.println("Failed to open " + nxtInfo[0].name);
				System.exit(1);
			}

			inFromNXT = new DataInputStream(nxtComm.getInputStream());
			outToNXT = new DataOutputStream(nxtComm.getOutputStream());

		    newSocketConnection();
		}
		catch (UnknownHostException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Creates a new socket server if instructed by the NXT
	 * @throws IOException
	 */
	private void newSocketServer() throws IOException{
		int port = inFromNXT.readInt();
		serverSocket = new ServerSocket(port);
		while(true){
			// wait for command from NXT
			byte command = inFromNXT.readByte();
			if(command == 1){
				waitForConnection();
			}
			// TODO support for other socket server functions
		}
	}

	/**
	 * Allows negotiation of the accept() method of Socket server
	 * @throws IOException
	 */
	private void waitForConnection()throws IOException{
		while(true){
			sock = serverSocket.accept();

			//	inform the NXT of the new Connection
			outToNXT.writeBoolean(true);

			DataInputStream inFromSocket = new DataInputStream(sock.getInputStream());
			DataOutputStream outToSocket = new DataOutputStream(sock.getOutputStream());

			// listen for incoming data from socket
			new forward(sock, inFromSocket, outToNXT);

			// listen for incoming data from NXT
			new forward(sock, inFromNXT, outToSocket);
		}
	}

	/**
	 * Allows for a connection to be made using the details supplied from the NXT
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void newSocketConnection() throws UnknownHostException, IOException 
	{
		// the first byte from the NXT contains the length of the host name in chars
		int len = inFromNXT.readByte();
		char[] hostChars = new char[len];

		// following the first byte the host name is transmitted
		for(int i=0;i<len;i++){
			hostChars[i] = inFromNXT.readChar();
		}
		// following the host name an int containing the port number of the socket to connect to
		// is transmitted
		port = inFromNXT.readInt();
		host = new String(hostChars);

		System.out.println("Host: " + host + " port: " + port);
		// create a socket connection with the specified host using the specified port

		sock = new Socket(host, port);
		outToNXT.writeBoolean(true);
		outToNXT.flush();

		DataInputStream inFromSocket = new DataInputStream(sock.getInputStream());

		DataOutputStream outToSocket = new 
		DataOutputStream(sock.getOutputStream());

		// listen for incoming data from socket
		new forward(sock, inFromSocket, outToNXT);

		// listen for incoming data from NXT
		new forwardNXT(sock, inFromNXT, outToSocket);
	}

	/**
	 * Allows for the forwarding of messages from Socket to NXT
	 * @author Ranulf Green
	 */
	private class forward extends Thread{
		private DataOutputStream dout;
		private DataInputStream din;

		private Socket sock;

		/**
		 * Constructor.
		 * @param sock the socket with which the connection is made
		 * @param dis the input stream to read
		 * @param dos the output stream to forward to
		 */
		public forward(Socket sock, DataInputStream dis, DataOutputStream dos){
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
				boolean flushed = true;
				while(true){
					int a = din.available();
					if(a>0){
						//System.out.println("Reading!" + a);
						flushed = false;
						int in = din.readUnsignedByte();
						if(in<0){
							//System.out.println("In Thread: Socket closed:" + in);
							sock.close();
							return;
						}
						//System.out.println("Sending " + in);
						dout.writeByte(in);
					}else if(!flushed){
						//System.out.println("Flushing");
						dout.flush();
						flushed = true;
					}
					try {
						Thread.sleep(100);
						Thread.yield();
					} catch (InterruptedException e) {}
				}
			}catch(IOException ioe){ioe.printStackTrace();};
		}
	}

	/**
	 * Class to forward messages from NXT to socket
	 * @author Ranulf Green
	 *
	 */
	private class forwardNXT extends Thread{
		private DataOutputStream dout;
		private DataInputStream din;

		private Socket sock;

		/**
		 * Constructor.
		 * @param sock
		 * @param dis input stream from NXT
		 * @param dos output stream to socket
		 */
		public forwardNXT(Socket sock, DataInputStream dis, DataOutputStream dos){
			super();
			din=dis;
			dout=dos;
			this.sock = sock;
			start();
		}

		/**
		 * causes a new thread to be invoked
		 */
		public void run(){
			try{
				while(true){
					int in = din.readUnsignedByte();
					if(in<0){
						System.out.println("In Thread: Socket closed:" + in);
						sock.close();
						return;
					}
					if(in!=0){
						dout.writeByte(in);
						dout.flush();
					}
					try {
						Thread.sleep(10);
						Thread.yield();
					} catch (InterruptedException e) {}
				}
			}catch(IOException ioe){ioe.printStackTrace();};
		}
	}

	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("USAGE: java SocketProxy <NXTName> <NXTAddress>");
			System.exit(0);
		}
		new SocketProxy(args[0],args[1]);
	}
}



