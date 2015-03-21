// TODO: This should be an inner class of the Dexter WiFi Sensor class when done.

package javax.net;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *  <p>This class creates server sockets. It may be subclassed by other factories, which create particular types of server 
 *  sockets. This provides a general framework for the addition of public socket-level functionality. It is the server 
 *  side analogue of a socket factory, and similarly provides a way to capture a variety of policies related to the 
 *  sockets being constructed.</p>
 *  
 *  <p>Like socket factories, server Socket factory instances have methods used to create sockets. There is also an 
 *  environment specific default server socket factory; frameworks will often use their own customized factory.</p>
 *  
 * @author BB
 *
 */
public abstract class ServerSocketFactory {

	/**
	 * 
	 */
	protected 	ServerSocketFactory() {
		 
	}
     
	/**
	 * Returns an unbound server socket.
	 * @return the unbound socket
	 * @throws IOException the unbound socket
	 */
	public ServerSocket createServerSocket() throws IOException {
		return null;  // TODO:
	}
     
	/**
	 * Returns a server socket bound to the specified port.
	 * @param port
	 * @return
	 */
	public abstract  ServerSocket 	createServerSocket(int port);
     
	/**
	 * Returns a server socket bound to the specified port, with a specified listen backlog and local IP.
	 * @param port
	 * @param backlog
	 * @param ifAddress
	 * @return
	 */
	//public abstract  ServerSocket 	createServerSocket(int port, int backlog, InetAddress ifAddress);
	
    /**
     * Returns a copy of the environment's default socket factory.
     * @return
     */
	public static ServerSocketFactory 	getDefault() {
		return null; // TODO:
	}
}
