package lejos.robotics.mapping;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.mapping.NavigationModel.NavEvent;

/**
 * NavigationPanel is a JPanel that displays navigation data from PCNavigationModel,
 * and allows the user to interact with it.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NavigationPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	protected float pixelsPerUnit = 2f;
	protected PCNavigationModel model = new PCNavigationModel(this);
	protected MapPanel mapPanel; 
	protected JPanel commandPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel mousePanel = new JPanel();
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(4);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(4);
	protected JPanel controlPanel = new JPanel();
	protected JLabel zoomLabel = new JLabel("Zoom:");
	protected JSlider slider = new JSlider(50,200,100);
	protected JLabel gridLabel = new JLabel("Grid:");
	protected JCheckBox gridCheck = new JCheckBox();
	protected JLabel meshLabel = new JLabel("Mesh:");
	protected JCheckBox meshCheck = new JCheckBox();
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	protected boolean showConnectPanel = true, showMousePanel = true, 
	                  showControlPanel = true, showCommandPanel = true,
	                  showReadingsPanel = true, showLastMovePanel = true,
	                  showParticlePanel = true, showMoves = false,
	                  showMesh = true;
	protected JPanel readingsPanel = new JPanel();
	protected JTextField readingsField = new JTextField(12);
	protected JPanel lastMovePanel = new JPanel();
	protected JTextField lastMoveField = new JTextField(20);
	protected JPanel particlePanel = new JPanel();
	protected JTextField particleField = new JTextField(20);
	protected int mapPanelWidth = 300;
	protected int mapPanelHeight = 300;
	protected Dimension mapPaneSize = new Dimension(600,600);
	protected JScrollPane mapPane;
	
	/**
	 * Build the various panels if they are required.
	 */
	protected void buildGUI() {
		mapPanel = new MapPanel(model, mapPaneSize, this);
		
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
			
			controlPanel.setBorder(BorderFactory.createTitledBorder("GUI Controls"));
			add(controlPanel);
					
			gridCheck.setSelected(true);

			slider.setMajorTickSpacing(25);
			slider.setPaintTicks(true);
			
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					pixelsPerUnit = source.getValue() / 100f;
					Dimension newSize = new Dimension((int) ((10 + mapPanelWidth) * pixelsPerUnit), (int) ((30 + mapPanelHeight) * pixelsPerUnit));
					//System.out.println("Setting size to " + newSize);
					mapPanel.setPreferredSize(newSize);
					mapPanel.revalidate();
					repaint();
				}
			});
			
			gridCheck.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {;
					mapPanel.repaint();
				}
			});
			
			if (showMesh) {		
				controlPanel.add(meshLabel);
				controlPanel.add(meshCheck);
				
				meshCheck.setSelected(false);
			
				meshCheck.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {;
						mapPanel.repaint();
					}
				});
			}
		}
		
		if (showMousePanel) {
			mousePanel.add(xLabel);
			mousePanel.add(xField);
			mousePanel.add(yLabel);
			mousePanel.add(yField);
			mousePanel.setBorder(BorderFactory.createTitledBorder("Mouse"));
			add(mousePanel);
		}
		
		if (showCommandPanel) {
			commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
			add(commandPanel);
		}
		
		if (showReadingsPanel) {
			readingsPanel.setBorder(BorderFactory.createTitledBorder("Last Readings"));
			readingsPanel.add(readingsField);
			add(readingsPanel);
		}

		if (showLastMovePanel) {
			lastMovePanel.setBorder(BorderFactory.createTitledBorder("Last Move"));
			lastMovePanel.add(lastMoveField);
			add(lastMovePanel);
		}
		
		if (showParticlePanel) {
			particlePanel.setBorder(BorderFactory.createTitledBorder("Selected Particle"));
			particlePanel.add(particleField);
			add(particlePanel);
		}
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		mapPane = new JScrollPane(mapPanel);
		add(mapPane);
		mapPane.setPreferredSize(mapPaneSize);
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
				s += Math.round(r.getRange()) + " ";
			}
			readingsField.setText(s);
		}
		
		if (showLastMovePanel) {
			lastMoveField.setText(model.getLastMove().toString());
		}
		
		if (showParticlePanel) {
			String s = "";
			for(RangeReading r:model.particleReadings) {
				s += Math.round(r.getRange()) + " ";
			}
			if (s.length() > 0) s += " weight = " + model.weight;
			
			particleField.setText(s);
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
	
	/**
	 * Override this method to specify actions to do after connection to the NXT
	 */
	protected void whenConnected() {	
	}
	
	/**
	 * Override this method to perform some action (other than repaining) when an event is received
	 * 
	 * @param navEvent the event
	 */
	protected void eventReceived(NavEvent navEvent) {	
	}
}
