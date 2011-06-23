package lejos.pc.remote;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import lejos.robotics.NavigationModel;

public class NavigationPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	protected float xOffset = 10, yOffset = 10, pixelsPerUnit = 2;
	protected PCNavigationModel model = new PCNavigationModel();
	protected MapPanel mapPanel = new MapPanel(model, new Dimension(600,700), xOffset, yOffset, pixelsPerUnit);
	protected JPanel formPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel statusPanel = new JPanel();
	protected JLabel xLabel = new JLabel("Mouse X:");
	protected JTextField xField = new JTextField(5);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(5);
	
	public NavigationPanel() {
		buildGUI();
	}
	
	protected void buildGUI() {
		formPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
		add(connectPanel);
		add(statusPanel);
		add(formPanel);
		add(mapPanel);
		statusPanel.add(xLabel);
		statusPanel.add(xField);
		statusPanel.add(yLabel);
		statusPanel.add(yField);
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
	}
	/**
	 * Print the error message and exit
	 */
	public void fatal(String msg) {
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

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		xField.setText("" + e.getX());
		yField.setText("" + e.getY());
	}
	
	public void mouseClicked(MouseEvent e) {
		popupMenu(e);
	}

	public void mouseEntered(MouseEvent e) {
		//System.out.println("Mouse entered at " + e);		
	}

	public void mouseExited(MouseEvent e) {
		//System.out.println("Mouse exited at " + e);
	}

	public void mousePressed(MouseEvent e) {
		//System.out.println("Mouse pressed at " + e);		
	}

	public void mouseReleased(MouseEvent e) {
		//System.out.println("Mouse released at " + e);	
	}
	
	protected void popupMenu(MouseEvent me) {
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    
	    JPopupMenu menu = new JPopupMenu(); 
	    menu.add(new MenuAction(NavigationModel.NavEvent.FIND_CLOSEST, "Find Closest", pt)); 
	    menu.add(new MenuAction(NavigationModel.NavEvent.ADD_WAYPOINT, "Add Way Point",pt));
	    menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To",pt));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Set pose",pt));
	
	    menu.show(this, pt.x, pt.y);
	}
}
