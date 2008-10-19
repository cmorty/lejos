import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

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


