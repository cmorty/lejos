package lejos.pc.remote;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;;

public class NavigationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected float xOffset = 10, yOffset = 10, pixelsPerUnit = 2;
	protected PCNavigationModel model = new PCNavigationModel();
	protected MapPanel mapPanel = new MapPanel(model, new Dimension(600,700));
	protected JPanel formPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	
	public NavigationPanel() {
		formPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
		mapPanel.setBorder(BorderFactory.createTitledBorder("Map"));
		add(connectPanel);
		add(formPanel);
		add(mapPanel);
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
	
	public void setMapSize(Dimension size) {
		mapPanel.setSize(size);
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
  
	public void log(String message) {
		System.out.println(message);
	}
}
