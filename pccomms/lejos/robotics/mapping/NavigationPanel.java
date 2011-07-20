package lejos.robotics.mapping;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
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
public class NavigationPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	protected int minZoom = 50;
	protected int maxZoom = 200;
	protected int zoomIncrement = 50;
	protected int zoomInitialValue = 150;
	protected boolean showZoomLabels = false;
	protected int zoomMajorTick = 25;
	
	protected float pixelsPerUnit = 1.5f;
	protected PCNavigationModel model = new PCNavigationModel(this);
	protected MapPanel mapPanel; 
	protected JPanel commandPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel mousePanel = new JPanel();
	protected JPanel logPanel = new JPanel();
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(4);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(4);
	protected JPanel controlPanel = new JPanel();
	protected JLabel zoomLabel = new JLabel("Zoom:");
	protected JSlider zoomSlider;
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	protected boolean showConnectPanel = true, showMousePanel = true, 
	                  showControlPanel = true, showCommandPanel = true,
	                  showReadingsPanel = true, showLastMovePanel = true,
	                  showParticlePanel = true, showMoves = false,
	                  showMesh = false, showGrid = true, showLog = false;
	protected JPanel readingsPanel = new JPanel();
	protected JTextField readingsField = new JTextField(12);
	protected JPanel lastMovePanel = new JPanel();
	protected JTextField lastMoveField = new JTextField(20);
	protected JPanel particlePanel = new JPanel();
	protected JTextField particleField = new JTextField(20);
	protected Dimension mapPaneSize = new Dimension(700,600);
	protected Point startDrag;
	protected Point initialViewStart = new Point(0,0);
	protected String title;
	protected String description = "";
	
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu fileMenu, aboutMenu, mapMenu, viewMenu;
	protected JMenuItem exit, about, clear, repaint, reset, open, save;
	protected JCheckBoxMenuItem viewGrid, viewMousePosition, viewControls,
	                          viewConnect, viewCommands, viewMesh, viewLog,
	                          viewLastMove, viewParticle;
	protected JFileChooser chooser = new JFileChooser();
	
	/**
	 * Build the various panels if they are required.
	 */
	protected void buildGUI() {
		createMousePanel();
		createConnectPanel();
		createControlPanel();
		createCommandPanel();
		createReadingsPanel();
		createMovePanel();
		createParticlePanel();
		createMapPanel();
	}
	
	/**
	 * Create the map panel
	 */
	protected void createMapPanel() {
		mapPanel = new MapPanel(model, mapPaneSize, this);
		add(mapPanel);
		
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		mapPanel.viewStart = new Point(initialViewStart);
		mapPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
	}
	
	/**
	 * Create the Connect panel to allow connection to a NXT brick
	 */
	protected void createConnectPanel() {
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
	}
	
	/**
	 * Create the control panel, which controls the GUI
	 */
	protected void createControlPanel() {
		if (showControlPanel) {
			controlPanel.setBorder(BorderFactory.createTitledBorder("GUI Controls"));
			add(controlPanel);
			
			controlPanel.add(zoomLabel);
			
			zoomSlider = new JSlider(SwingConstants.HORIZONTAL,minZoom,maxZoom,minZoom);
			zoomSlider.setValue(zoomInitialValue);
			
			zoomSlider.setMajorTickSpacing(zoomMajorTick);
			zoomSlider.setPaintTicks(true);
			
			if (showZoomLabels) {
				zoomSlider.setPaintLabels(true);
			
				//Create the label table
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for(int i=zoomSlider.getMinimum();i<=zoomSlider.getMaximum();i+=zoomIncrement) {
					labelTable.put( new Integer(i ), new JLabel(i + "%") );
				}
				zoomSlider.setLabelTable( labelTable );		
			}
			
			zoomSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					pixelsPerUnit = source.getValue() / 100f;
					repaint();
				}
			});
			
			controlPanel.add(zoomSlider);		
		}
	}
	
	/**
	 * Create the map panel which shows the mouse position in map coordinates
	 */
	protected void createMousePanel() {
		if (showMousePanel) {
			mousePanel.add(xLabel);
			mousePanel.add(xField);
			mousePanel.add(yLabel);
			mousePanel.add(yField);
			//mousePanel.setBorder(BorderFactory.createTitledBorder("Mouse"));
			add(mousePanel);
		}
	}
	
	/**
	 * Create the command panel - this is added to by overriding classes
	 */
	protected void createCommandPanel() {
		if (showCommandPanel) {
			commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
			add(commandPanel);
		}
	}
	
	/**
	 * Create the readings panel which shows the last range readings
	 */
	protected void createReadingsPanel() {
		if (showReadingsPanel) {
			readingsPanel.setBorder(BorderFactory.createTitledBorder("Last Readings"));
			readingsPanel.add(readingsField);
			add(readingsPanel);
		}
	}
	
	/**
	 * Create the move panel, which shows the last move made by the robot
	 */
	protected void createMovePanel() {
		if (showLastMovePanel) {
			lastMovePanel.setBorder(BorderFactory.createTitledBorder("Last Move"));
			lastMovePanel.add(lastMoveField);
			add(lastMovePanel);
		}
	}
	
	/**
	 * Create the particle panel which shows range readings for a specific particle
	 */
	protected void createParticlePanel() {
		if (showParticlePanel) {
			particlePanel.setBorder(BorderFactory.createTitledBorder("Selected Particle"));
			particlePanel.add(particleField);
			add(particlePanel);
		}
	}
	
	/**
	 * Create the menu
	 */
	protected void createMenu() {
		createFileMenu();
		createViewMenu();
		createMapMenu();
		createAboutMenu();
		//createHelpMenu();
	}
	
	/**
	 * Create a File menu
	 */
	protected void createFileMenu() {
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		open = new JMenuItem("Open ...");
		fileMenu.add(open);
		open.addActionListener(this);
		save = new JMenuItem("Save ...");
		fileMenu.add(save);
		save.addActionListener(this);
		exit = new JMenuItem("Exit");
		fileMenu.add(exit);
		exit.addActionListener(this);
	}
	
	/**
	 * Create a View menu
	 */
	protected void createViewMenu() {
		viewMenu = new JMenu("View");
		menuBar.add(viewMenu);
		viewGrid = new JCheckBoxMenuItem("Grid");
		viewGrid.setSelected(showGrid);
		viewGrid.addActionListener(this);
		viewMenu.add(viewGrid);;
		viewMesh = new JCheckBoxMenuItem("Mesh");
		viewMesh.setSelected(showMesh);
		viewMesh.addActionListener(this);
		viewMenu.add(viewMesh);
		viewLog = new JCheckBoxMenuItem("Log");
		viewLog.setSelected(showLog);
		viewLog.addActionListener(this);
		viewMenu.add(viewLog);
		viewMousePosition = new JCheckBoxMenuItem("Mouse Position");
		viewMousePosition.setSelected(showMousePanel);
		viewMousePosition.addActionListener(this);
		viewMenu.add(viewMousePosition);
		viewControls = new JCheckBoxMenuItem("GUI Controls");
		viewControls.setSelected(showControlPanel);
		viewControls.addActionListener(this);
		viewMenu.add(viewControls);
		viewConnect = new JCheckBoxMenuItem("Connect");
		viewConnect.setSelected(showConnectPanel);
		viewConnect.addActionListener(this);
		viewMenu.add(viewConnect);
		viewCommands = new JCheckBoxMenuItem("Commands");
		viewCommands.setSelected(showCommandPanel);
		viewCommands.addActionListener(this);
		viewMenu.add(viewCommands);
	}
	
	/**
	 * Create a Map menu
	 */
	protected void createMapMenu() {
		mapMenu = new JMenu("Map");
		menuBar.add(mapMenu);
		clear = new JMenuItem("Clear");
		clear.addActionListener(this);
		mapMenu.add(clear);
		repaint = new JMenuItem("Repaint");
		repaint.addActionListener(this);
		mapMenu.add(repaint);
		reset = new JMenuItem("Reset");
		reset.addActionListener(this);
		mapMenu.add(reset);
	}
	
	/**
	 * Create an About menu
	 */
	protected void createAboutMenu() {
		aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		about  = new JMenuItem("About " + title + " ...");
		aboutMenu.add(about);
		about.addActionListener(this);
	}
	
	/**
	 * Create a help menu
	 */
	protected void createHelpMenu() {
	    menuBar.add(Box.createHorizontalGlue());
	    menuBar.add(new JMenu("Help"));
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
	public static JFrame openInJFrame(NavigationPanel content, int width, int height,
                                    String title, Color bgColor, JMenuBar menuBar) {
		JFrame frame = new JFrame(title);
		frame.setBackground(bgColor);
		content.setBackground(bgColor);
		frame.setSize(width, height);
		frame.setContentPane(content);
		content.title = title;
		
    	frame.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent event) {
    			System.exit(0);
    		}
    	});
    	if (menuBar != null) frame.setJMenuBar(menuBar);
    	frame.setVisible(true);
    	return (frame);
	}
  
	/**
	 * Log a message
	 * 
	 * @param message
	 */
	public void log(String message) {
		System.out.println(message);
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseDragged(MouseEvent e) {
	    Point p = e.getPoint();
	    mapPanel.viewStart.x -= (p.x - startDrag.x)/pixelsPerUnit;
	    mapPanel.viewStart.y += (p.y - startDrag.y)/pixelsPerUnit;
	    startDrag = p;
	    //System.out.println("viewStart = " + mapPanel.viewStart);
	    mapPanel.repaint();
	}

	/**
	 * Optionally overridden by subclasses
	 */
	public void mouseMoved(MouseEvent e) {
		// Display the mouse co-ordinates when they change
		xField.setText("" + (int) ((e.getX()/ pixelsPerUnit + mapPanel.viewStart.x)));
		yField.setText("" +  ((int) ((mapPanel.getHeight() - e.getY())/ pixelsPerUnit + mapPanel.viewStart.y)));
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
	    startDrag = e.getPoint();
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
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    boolean inside = model.getMap().inside(new lejos.geom.Point((me.getX() / pixelsPerUnit + mapPanel.viewStart.x) , (mapPanel.getHeight() - me.getY())/ pixelsPerUnit + mapPanel.viewStart.y));  
	    if (!inside) return;
	    
	    JPopupMenu menu = new JPopupMenu(); 
	    popupMenuItems(me.getPoint(),menu);
	    
	    menu.show(this, pt.x, pt.y);
	}
	
	/**
	 * Used by subclasses to add popup menu items
	 * 
	 * @param p the point at which the menu was popped up
	 * @param menu the popup menu
	 */
	protected void popupMenuItems(Point p, JPopupMenu menu) {
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
	
	/**
	 * Provides a pop up about dialog which can be tailored or overridden
	 */
	protected void about() {
		JOptionPane.showMessageDialog(this,
			    description,
			    "About " + title,
			    JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Called when a menu item is selected
	 */
	public void actionPerformed(ActionEvent e) {	
		if (e.getSource() == about) {
			about();
		} else if (e.getSource() == exit) {
			System.exit(0);
		} else if (e.getSource() == save) {
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = chooser.getSelectedFile();
				VolatileImage image = mapPanel.createVolatileImage(mapPanel.getWidth(), mapPanel.getHeight()); 
				Graphics g = image.getGraphics();
				mapPanel.paint(g);
				g.dispose();
				try {
					ImageIO.write(image.getSnapshot(), "png", file);
				} catch (IOException ioe) {
					log("IOException in save");
				}
	        } else {
	            log("Open command cancelled by user.");
	        }
		} else if (e.getSource() == open) {
			int returnVal = chooser.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = chooser.getSelectedFile();
	            log("Opening: " + file.getName());
	            model.loadMap(file.getPath());
	            repaint();
	        } else {
	            log("Open command cancelled by user.");
	        }
		} else if (e.getSource() == clear) {
			model.clear();
			repaint();
		} else if (e.getSource() == repaint) {
			repaint();
		} else if (e.getSource() == reset) {
			mapPanel.viewStart = new Point(initialViewStart);
			//zoomSlider.setValue(zoomInitialValue);
			mapPanel.repaint();
		} else if (e.getSource() == viewGrid) {
			showGrid = viewGrid.isSelected();
			repaint();
		}  else if (e.getSource() == viewMesh) {
			showMesh = viewMesh.isSelected();
			repaint();
		} else if (e.getSource() == viewLog) {
			logPanel.setVisible(viewLog.isSelected());
			repaint();
		} else if (e.getSource() == viewMousePosition) {
			mousePanel.setVisible(viewMousePosition.isSelected());
			repaint();
		} else if (e.getSource() == viewControls) {
			controlPanel.setVisible(viewControls.isSelected());
		} else if (e.getSource() == viewCommands) {
			commandPanel.setVisible(viewCommands.isSelected());
		}  else if (e.getSource() == viewConnect) {
			connectPanel.setVisible(viewConnect.isSelected());
		}
	}
}
