package lejos.pc.remote;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;

/**
 * NavigationPanel is a JPanel that displays navigation data from PCNavigationModel,
 * and allows the user to interact with it.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NavigationPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	protected float xOffset = 0f, yOffset = 0f, pixelsPerUnit = 2f;
	protected PCNavigationModel model = new PCNavigationModel(this);
	protected MapPanel mapPanel = new MapPanel(model, new Dimension(600,700), this);
	protected JPanel commandPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel statusPanel = new JPanel();
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(5);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(5);
	protected JPanel controlPanel = new JPanel();
	protected JLabel zoomLabel = new JLabel("Zoom:");
	protected JSlider slider = new JSlider(100,500,200);
	protected JLabel gridLabel = new JLabel("Grid:");
	protected JCheckBox gridCheck = new JCheckBox();
	protected JLabel meshLabel = new JLabel("Mesh:");
	protected JCheckBox meshCheck = new JCheckBox();
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	protected boolean showConnectPanel = true, showStatusPanel = true, 
	                  showControlPanel = true, showCommandPanel = true,
	                  showReadingsPanel = true, showLastMovePanel = true;
	protected JPanel readingsPanel = new JPanel();
	protected JTextField readingsField = new JTextField(12);
	protected JPanel lastMovePanel = new JPanel();
	protected JTextField lastMoveField = new JTextField(20);

	/**
	 * Build the various panels if they are required.
	 */
	protected void buildGUI() {
		if (showConnectPanel) {
			connectPanel.add(nxtLabel);
			connectPanel.add(nxtName);
			connectPanel.add(connectButton);
			connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
			add(connectPanel);
			
			connectButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					model.connect(nxtName.getText());
				}
			});
		}
		
		if (showControlPanel) {
			controlPanel.add(zoomLabel);
			controlPanel.add(slider);
			controlPanel.add(gridLabel);
			controlPanel.add(gridCheck);
			controlPanel.add(meshLabel);
			controlPanel.add(meshCheck);
			controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
			add(controlPanel);
					
			gridCheck.setSelected(true);
			meshCheck.setSelected(false);
			
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					pixelsPerUnit = source.getValue() / 100f;
					mapPanel.repaint();
				}
			});
			
			gridCheck.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {;
					mapPanel.repaint();
				}
			});
			
			meshCheck.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {;
					mapPanel.repaint();
				}
			});
		}
		
		if (showStatusPanel) {
			statusPanel.add(xLabel);
			statusPanel.add(xField);
			statusPanel.add(yLabel);
			statusPanel.add(yField);
			statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
			add(statusPanel);
		}
		
		if (showCommandPanel) {
			commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
			add(commandPanel);
		}
		
		if (showReadingsPanel) {
			readingsPanel.setBorder(BorderFactory.createTitledBorder("Readings"));
			readingsPanel.add(readingsField);
			add(readingsPanel);
		}

		if (showLastMovePanel) {
			lastMovePanel.setBorder(BorderFactory.createTitledBorder("Last Move"));
			lastMovePanel.add(lastMoveField);
			add(lastMovePanel);
		}
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		add(mapPanel);		
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
	
	/**
	 * Set the size of the map panel
	 * 
	 * @param size the preferred panel dimensions
	 */
	public void setMapSize(Dimension size) {
		mapPanel.setSize(size);
	}
	
	@Override
	/**
	 * Update active subpanels with latest data from the navigation model
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (showReadingsPanel) {
			RangeReadings readings = model.getReadings();
			
			String s = "";
			for(RangeReading r:readings) {
				s += r.getRange() + " ";
			}
			readingsField.setText(s);
		}
		
		if (showLastMovePanel) {
			lastMoveField.setText(model.getLastMove().toString());
		}
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
	 * Log a message
	 * @param message
	 */
	public void log(String message) {
		System.out.println(message);
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseMoved(MouseEvent e) {
		// Display the mouse co-ordinates when they change
		xField.setText("" + Math.round(e.getX() / pixelsPerUnit));
		yField.setText("" + Math.round(e.getY() / pixelsPerUnit));
	}
	
	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseClicked(MouseEvent e) {
		popupMenu(e);
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseEntered(MouseEvent e) {		
	}

	/**
	 * Called when the mouse exits the map panel. 
	 */
	public void mouseExited(MouseEvent e) {
		// Set the x,y co-ordinates blank when not in the map panel
		xField.setText("");
		yField.setText("");
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mousePressed(MouseEvent e) {		
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseReleased(MouseEvent e) {	
	}
	
	/**
	 * Display a context menu at the specified point in the map panel.
	 * Overridden by subclasses.
	 * 
	 * @param me the mouse event
	 */
	protected void popupMenu(MouseEvent me) {
	}
}
