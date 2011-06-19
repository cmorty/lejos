package lejos.pc.remote;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class NavigationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected static final int CANVAS_OFFSET = 50;
	protected float xOffset = 10, yOffset = 10, pixelsPerUnit = 2;
	protected PCNavigationModel model = new PCNavigationModel();
	protected int gridSpacing = 1;
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(6);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(6);
	protected JLabel headingLabel = new JLabel("Heading:");
	protected JTextField headingField = new JTextField(6);
	protected JButton gotoButton = new JButton("Go to");
	
	public NavigationPanel() {
		add(xLabel);
		add(xField);
		add(yLabel);
		add(yField);
		add(headingLabel);
		add(headingField);
		add(gotoButton);
		
		gotoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					model.goTo(new Pose(Float.parseFloat(xField.getText()), 
						Float.parseFloat(yField.getText()), Float.parseFloat(headingField.getText())));
				} catch (NumberFormatException e) {}
			}
		});
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
  
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintMap((Graphics2D) g);
		paintRobot((Graphics2D) g);
	}
	
	/**
	 * Draw the map using Line2D objects
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintMap(Graphics2D g2d) {
		LineMap map = model.getMap();
		if (map == null) return;
		g2d.setColor(Color.black);
		Line[] lines = map.getLines();
		for (int i = 0; i < lines.length; i++) {
			Line2D line = new Line2D.Float(
    		  xOffset + lines[i].x1 * pixelsPerUnit, 
    		  CANVAS_OFFSET + yOffset + lines[i].y1 * pixelsPerUnit, 
    		  xOffset + lines[i].x2 * pixelsPerUnit, 
    		  CANVAS_OFFSET + yOffset + lines[i].y2 * pixelsPerUnit);
			g2d.draw(line);
		}
	}
	
	protected void paintRobot(Graphics2D g2d) {
		// TODO
	}
	
	public void log(String message) {
		System.out.println(message);
	}
}
