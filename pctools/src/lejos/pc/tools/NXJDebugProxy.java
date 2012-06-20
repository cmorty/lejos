package lejos.pc.tools;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import js.tinyvm.DebugData;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.pc.tools.debug.Connection;
import lejos.pc.tools.debug.DebugProxyTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class NXJDebugProxy implements NXTCommLogListener {

	public static void main(String[] args) {
		ToolStarter.startTool(NXJDebugProxy.class, args);
	}
	
	public static int start(String[] args) throws Exception
	{
		return new NXJDebugProxy().run(args);
	}

	public int run(String[] args) throws Exception {
		String debugFile;

		String name, address;
		int protocols = 0;

		InetSocketAddress socketAddress = null;
		boolean attach = false;
		boolean debug=false;

		NXJDebugProxyCommandLineParser parser = new NXJDebugProxyCommandLineParser(NXJDebugProxy.class, "[options]");
		try {
			CommandLine commandLine = parser.parse(args);

			debugFile = AbstractCommandLineParser.getLastOptVal(commandLine, "di");
			boolean doHelp = commandLine.hasOption("h");

			name = AbstractCommandLineParser.getLastOptVal(commandLine, "n");
			boolean blueTooth = commandLine.hasOption("b");
			boolean usb = commandLine.hasOption("u");
			address = AbstractCommandLineParser.getLastOptVal(commandLine, "d");
			debug = commandLine.hasOption("v");
			
			if (blueTooth)
				protocols |= NXTCommFactory.BLUETOOTH;
			if (usb)
				protocols |= NXTCommFactory.USB;

			if (protocols == 0)
				protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;

			if (commandLine.hasOption("c")) {
				String addr = AbstractCommandLineParser.getLastOptVal(commandLine, "c");
				if (addr == null) {
					throw new ParseException("please specify an attach location");
				}
				int sep = addr.indexOf(':');
				if (sep == -1) {
					throw new ParseException("please specify a valid attach location");
				}
				socketAddress = new InetSocketAddress(addr.substring(0, sep - 1), Integer.parseInt(addr.substring(sep)));
				
				attach = true;
			}

			if (commandLine.hasOption("l")) {
				if (attach) {
					throw new ParseException("Can't both listen and attach");
				}
				String addr = AbstractCommandLineParser.getLastOptVal(commandLine, "l");
				if (addr == null)
					socketAddress = null;
				else {
					int sep = addr.indexOf(':');
					if (sep == -1) {
						socketAddress = new InetSocketAddress(Integer.parseInt(addr));
					} else {
						socketAddress = new InetSocketAddress(addr.substring(0, sep - 1), Integer.parseInt(addr.substring(sep)));
					}
				}
			}

			if (doHelp) {
				parser.printHelp(System.out);
				return 0;
			}

			if (debugFile == null)
				throw new ParseException("no debug file specified");
		} catch (ParseException e) {
			parser.printHelp(System.err, e);
			return 1;
		}

		final Socket sConn = openConnection(attach, socketAddress);
		DebugData data;

		// if(debugFile!=null){
		data = DebugData.load(new File(debugFile));
		// }
		// else{
		// System.err.println("WARNING: we are not using a debug data file. Debugging capabilicies will be strongly restricted.");
		// }
		// Following line is very useful if using the remote console to see debug output.
		//try{Thread.sleep(10000);} catch(Exception e){/*ignore*/}
		final NXTConnector connector = new NXTConnector();

		connector.addLogListener(this);

		if (!connector.connectTo(name, address, protocols, NXTComm.PACKET)) {
			System.err.println("No NXT found - is it switched on and plugged in (for USB)?");
			return 1;
		}

		// Connection classes like java.net.Socket and lejos.pc.comm.NXTComm
		// implement Closeable only in java 7...
		//
		Closeable closeableSocket = new Closeable() {
			public void close() throws IOException {
				sConn.close();
			}
		};
		Connection debuggerConnection = new Connection("SocketConnection", closeableSocket, sConn.getInputStream(), sConn.getOutputStream());
		Closeable closeableNXTConnector = new Closeable() {
			public void close() throws IOException {
				connector.close();
			}
		};
		Connection nxtConnection = new Connection("NXTConnection", closeableNXTConnector, connector.getInputStream(),
				connector.getOutputStream());

		DebugProxyTool tool = new DebugProxyTool(data, debuggerConnection, nxtConnection);

		tool.setDebug(debug);
		tool.addLogListener(this);
		tool.start();
		tool.waitForCompletion();
		return 0;
	}

	private Socket openConnection(boolean attach, InetSocketAddress socketAddress) throws IOException {

		Socket socket;
		if (attach) {
			socket = new Socket();
			socket.connect(socketAddress);
		} else {
			ServerSocket server = new ServerSocket();
			server.bind(socketAddress);
			System.out.println("Listening for debugger at " + server.getInetAddress().getHostName() + ":" + server.getLocalPort());
			socket = server.accept();
		}
		System.out.println("Connected to debugger at " + socket.getInetAddress().getHostName() + ":" + socket.getPort());
		socket.setTcpNoDelay(true);
		return socket;
	}

	public void logEvent(String message) {
		System.out.println(message);
	}

	public void logEvent(Throwable throwable) {
		throwable.printStackTrace();
	}
}
