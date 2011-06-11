package lejos.pc.remote;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

/**
 * This class is useful for creating a PC GUI Application that connects to the NXT
 * and exchanges data with it using Java streams.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class RemotePanel extends JPanel implements ActionListener, MouseListener {
  private static final long serialVersionUID = 1L;
  
  private ArrayList<CommandButton> buttons = new ArrayList<CommandButton>();
  private JPanel buttonPanel = new JPanel();

  // Data input and output streams for communicating with the NXT
  protected DataOutputStream dos;
  protected DataInputStream dis;
  
  public RemotePanel() {
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
  protected void error(String msg) {
    System.err.println(msg);
    System.exit(1);
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
	  sendCommand(((CommandButton) e.getSource()).getCommand());
  }
  
  /**
   * Send a command to the NXT
   */
  private void sendCommand(Command command) {
    try {
      dos.writeByte(command.getValue());
      dos.flush();
    } catch (IOException ioe) {
      error("IO Exception");
    }
  }
}

