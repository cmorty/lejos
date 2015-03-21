package javax.net;

import java.io.IOException;
import java.net.Socket;

/**
 *  <p>This class creates sockets. It may be subclassed by other factories, which create particular subclasses of sockets 
 *  and thus provide a general framework for the addition of public socket-level functionality.</p>
 *  <p>Socket factories are a simple way to capture a variety of policies related to the sockets being constructed, 
 *  producing such sockets in a way which does not require special configuration of the code which asks for the sockets:</p>
 *  <li>Due to polymorphism of both factories and sockets, different kinds of sockets can be used by the same application 
 *  code just by passing it different kinds of factories.
 *  <li>Factories can themselves be customized with parameters used in socket construction. So for example, factories 
 *  could be customized to return sockets with different networking timeouts or security parameters already configured.
 *  <li>The sockets returned to the application can be subclasses of java.net.Socket, so that they can directly expose 
 *  new APIs for features such as compression, security, record marking, statistics collection, or firewall tunneling.
 *  <p>Factory classes are specified by environment-specific configuration mechanisms. For example, the getDefault 
 *  method could return a factory that was appropriate for a particular user or applet, and a framework could use a 
 *  factory customized to its own purposes.</p>
 * @author BB
 *
 */
public abstract class SocketFactory {

	/**
	 * Creates a SocketFactory. 
	 */
	protected SocketFactory() {
		// TODO:
	}

	/**
	 * Creates an unconnected socket.
	 * @return the unconnected socket
	 * @throws IOException if the socket cannot be created
	 */
	public Socket createSocket() throws IOException {
		return null; // TODO:
	}

	/**
	 * Creates a socket and connects it to the specified remote host at the specified remote port. This socket is 
	 * configured using the socket options established for this factory.
	 * @param host the server host
	 * @param port the server port
	 * @return the Socket
	 * @throws IOException if an I/O error occurs when creating the socket 
	 * @throws UnknownHostException if the host is not known
	 */
	public abstract Socket createSocket(String host, int port) throws IOException; //, UnknownHostException;

	/**
	 * Creates a socket and connects it to the specified remote host on the specified remote port. The socket will also be bound to the local address and port supplied. This socket is configured using the socket options established for this factory.
	 * 
	 * @param host
	 * @param port
	 * @param localHost the local address the socket is bound to
	 * @param localPort the local port the socket is bound to
	 * @return
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	//public abstract Socket createSocket(String host, int port, InetAddress localHost, int localPort)
	//		throws IOException, UnknownHostException;

	/**
	 * Creates a socket and connects it to the specified port number at the specified address. This socket is configured 
	 * using the socket options established for this factory.
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException
	 */
	//public abstract Socket createSocket(InetAddress host, int port) throws IOException;

	/**
	 * Creates a socket and connect it to the specified remote address on the specified remote port. The socket will also 
	 * be bound to the local address and port suplied. The socket is configured using the socket options established for 
	 * this factory.
	 * @param address the server network address
	 * @param port the server port
	 * @param localAddress
	 * @param localPort
	 * @return
	 * @throws IOException
	 */
	//public abstract Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
	//		throws IOException;

}
