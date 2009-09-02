import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * This PC sample works with the leJOS NXJ sample, SocketTest.
 * 
 * It shows you how to connect a program running on the NXT to a TCP/IP server
 * on your PC, intranet on the Internet. The host program uses the standard Java
 * SocketServer class, and the NXT program uses the NXTSocket class.
 * 
 * To run the sample, first start SocketTest on the NXT and it will wait for
 * a connection to the Socket Proxy. 
 * 
 * Then run this program and it will display a small GUI window. Change the port 
 * it listens on if 8081 is used on your PC. If you do this you will need to change
 * SocketTest as well.
 * 
 * Then run the Socket Proxy (nxjsocketproxy command) optionally specifying the name and
 * or address of the NXT you want to connect to. This will cause Socket Proxy to
 * connect to SocketTest on the NXT and SocketTest to connect to this socket server. 
 * 
 * You can then type words in the GUI and see the responses come back from the NXT (
 * on the standard output stream).
 * 
 * Type "bye" to cause SocketTest on the NXT to stop.
 * 
 *
 * @author Ranulf Green and Lawrie Griffiths
 *
 */
public class SocketServer {

	DataOutputStream outToSocket = null;
	DataInputStream inFromSocket = null;

	public SocketServer(){
		JFrame f = new JFrame();
		JPanel p = new JPanel(new FlowLayout());
		final JTextField t = new JTextField(10);
		JButton c = new JButton("Echo");

		p.add(t);
		p.add(c);

		c.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String s = t.getText();
				s+='\n';
				try {
					System.out.println("Sending " + s);
					outToSocket.writeChars(s);
					outToSocket.flush();
					if (s.equals("bye\n")) return;
					// Read the reply line
					StringBuffer sb = new StringBuffer();
					char c;
					do {
						c = inFromSocket.readChar();
						sb.append(c);
					} while (c != '\n');
					System.out.println(sb.toString());
				} catch (IOException e) {}
			}});
		
	    WindowListener listener = new WindowAdapter() {
	        public void windowClosing(WindowEvent w) {
	          System.exit(0);
	        }
	      };
	      
	    f.addWindowListener(listener);
	      
		f.add(p);
		f.pack();
		f.setVisible(true);
		new a();
	}

	private class a extends Thread{
		public a(){
			start();
		}

		public void run(){
			try {
				ServerSocket s = new ServerSocket(8081);
				while(true){
					Socket sock = s.accept();
					System.out.println("Socket Server Connected");
					outToSocket = new DataOutputStream(sock.getOutputStream());
					inFromSocket = new DataInputStream(sock.getInputStream());
				}
			} catch (IOException e) {}
		}
	}

	public static void main(String[] args) {
		new SocketServer();
	}
}


