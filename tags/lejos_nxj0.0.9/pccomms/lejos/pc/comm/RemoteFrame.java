package lejos.pc.comm;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * This class is useful for creating a PC GUI Application that connects to the NXT
 * and exchanges data with it using Java streams.
 * 
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class RemoteFrame extends JPanel implements ActionListener, MouseListener {
  private static final long serialVersionUID = 1L;

  // The name of the NXT to connect to
  protected String nxtName;

  // Data input and output streams for communicating with the NXT
  protected DataOutputStream dos;
  protected DataInputStream dis;

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
   * Create the GUI elements the map and the particle set and connect to the
   * NXT.
   */
  public RemoteFrame(String nxtName) throws IOException {
    
    addMouseListener(this);

    // Connect to NXT
    this.nxtName = nxtName;
    connect();
  }

  /**
   * Process buttons
   */
  public void actionPerformed(ActionEvent e) {
   }
 
  /**
   * Connect to the NXT
   */
  protected void connect() {
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
  
  /**
   * Find the closest particle to the mouse click
   */
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
  
  protected JButton createButton(String text) {
	JButton button = new JButton(text);
	add(button);
	button.addActionListener(this);
	return button;
  }
}

