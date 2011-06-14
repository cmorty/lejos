package lejos.pc.remote;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.navigation.Pose;

/**
 * This class is useful for creating a PC GUI Application that connects to the NXT
 * and exchanges data with it using Java streams.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class RemotePanel extends JPanel implements ActionListener, MouseListener {
  private static final long serialVersionUID = 1L;
  
  protected static final int CANVAS_OFFSET = 50;
  
  protected ArrayList<CommandButton> buttons = new ArrayList<CommandButton>();
  protected JPanel buttonPanel = new JPanel();

  // Data input and output streams for communicating with the NXT
  protected DataOutputStream dos;
  protected DataInputStream dis;
  
  public RemotePanel() {
	  add(buttonPanel);
  }

  /**
   * Create a frame to display the panel in
   */
  public static JFrame openInJFrame(Container content, int width, int height,
                                    String title, Color bgColor) {
    JFrame frame = new JFrame(title);
    frame.setBackground(bgColor);
    content.setBackground(bgColor);
    frame.setSize(width, height);
    frame.setContentPane(content);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent event) {
        System.exit(0);
      }
    });
    frame.setVisible(true);
    return (frame);
  }
 
  /**
   * Connect to the NXT
   */
  public void connect(String nxtName) {
    NXTConnector conn = new NXTConnector();

    if (!conn.connectTo(nxtName, null, NXTCommFactory.BLUETOOTH)) {
      error("NO NXT found");
    }
  
    System.out.println("Connected to " + nxtName);
  
    dis = conn.getDataIn();
    dos = conn.getDataOut();
  }
  
  /**
   * Print the error message and exit
   */
  protected void fatal(String msg) {
    System.err.println(msg);
    System.exit(1);
  }
  
  /**
   * Show a pop-up message
   */
  public void error(String msg) {
	JOptionPane.showMessageDialog(this, msg);
  }
  
  public void mouseClicked(MouseEvent me) {
  }

  public void mouseExited(MouseEvent me) {
  }
  
  public void mouseReleased(MouseEvent me) {
  }
  
  public void mouseEntered(MouseEvent me) {
  }
  
  public void mousePressed(MouseEvent me) {
  }
  
  public void createButton(CommandButton button) {
	buttons.add(button);
    button.addActionListener(this);
	buttonPanel.add(button);
  }
  
  public JButton createButton(String name) {
    JButton button = new JButton(name);
	button.addActionListener(this);
	return button;
  }
 
  /**
   * Process buttons
   */
  public void actionPerformed(ActionEvent e) {
	if (dos == null) return;
	try {
	  Command c = ((CommandButton) e.getSource()).getCommand();
	  sendCommand(c, null);
	  HashMap<String,Number> reply = readReply(c);
	  updateData(c.getReply(),reply);
	} catch (IOException ioe) {
	  error("Command failed with I/O error");
	}
  }
  
  /**
   * Send a command to the NXT
   */
  private void sendCommand(Command command, HashMap<String,Number> data) throws IOException {
	  if (dos == null) {
		  error("Not connected");
		  return;
	  }
      dos.writeByte(command.getValue());
      dos.flush();
      Message request = command.getRequest();
      ArrayList<MessageElement> c = request.getMessageElements();
      for(MessageElement e : c ) {
    	  switch (e.getType()) {
    		  case BYTE: dos.writeByte((Byte) data.get(e.getName())); break;
    		  case SHORT: dos.writeShort((Short) data.get(e.getName())); break;
    		  case INT: dos.writeInt((Integer) data.get(e.getName())); break;
    		  case FLOAT: dos.writeFloat((Float) data.get(e.getName())); break;
    	  }
      }
  }
  
  private HashMap<String,Number> readReply(Command command) throws IOException {
	  if (dis == null) {
		  error("Not connected");
		  return null;
	  }
	  HashMap<String,Number> data = new HashMap<String,Number>();
      Message reply = command.getReply();
      ArrayList<MessageElement> c = reply.getMessageElements();
      for(MessageElement e : c ) {
    	  switch (e.getType()) {
    		  case BYTE: data.put(e.getName(), (Byte) dis.readByte()); break;
    		  case SHORT: data.put(e.getName(), (Short) dis.readShort()); break;
    		  case INT: data.put(e.getName(), (Integer) dis.readInt()); break;
    		  case FLOAT: data.put(e.getName(), (Float) dis.readFloat()); break;
    	  }
      }
	  return data;
  }
  
  protected void updateData(Message reply, HashMap<String,Number> data) {
	  
  }

}

