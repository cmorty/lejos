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
public class NavigationPanel extends JPanel implements MapApplicationUI, MouseListener, MouseMotionListener, ActionListener {
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
	protected JPanel statusPanel = new JPanel();
	protected JPanel logPanel = new JPanel();
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(4);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(4);
	protected JPanel controlPanel = new JPanel();
	protected JSlider zoomSlider;
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	protected boolean showConnectPanel = true, showStatusPanel = true, 
	                  showControlPanel = true, showCommandPanel = true,
	                  showReadingsPanel = true, showLastMovePanel = true,
	                  showParticlePanel = true, showMoves = false,
	                  showMesh = false, showGrid = true, showLog = false,
	                  showParticles = false;
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
	protected JMenu fileMenu, aboutMenu, mapMenu, viewMenu, colorMenu, commandsMenu;
	protected JMenuItem exit, about, clear, repaint, reset, open, save, connect, gridColor, robotColor,
						mapColor, particleColor, meshColor, targetColor, waypointColor,
						pathColor, moveColor, featureColor, backgroundColor, estimateColor, closestColor,
						getPose, randomMove, localize, stop, calculatePath, followPath;
	protected JCheckBoxMenuItem viewGrid, viewMousePosition, viewControls,
	                          viewConnect, viewCommands, viewMesh, viewLog,
	                          viewLastMove, viewParticlePanel, viewParticles;
	protected JFileChooser chooser = new JFileChooser();
	protected EventPanel eventPanel = new EventPanel(model, this,  null);
	protected JColorChooser colorChooser = new JColorChooser();
	protected JFrame frame;
	
	/**
	 * Build the various panels if they are required.
	 */
	protected void buildGUI() {
		if (showStatusPanel) {
			createStatusPanel();
			add(statusPanel);
		}
		
		if (showConnectPanel) {
			createConnectPanel();
			add(connectPanel);
		}
		
		if (showControlPanel) {
			createControlPanel();
			add(controlPanel);
		}
		
		if (showCommandPanel) {
			createCommandPanel();
			add(commandPanel);
		}
		
		if (showReadingsPanel) {
			createReadingsPanel();
			add(readingsPanel);
		}
		
		if (showLastMovePanel) {
			createMovePanel();
			add(lastMovePanel);
		}
		
		if (showParticlePanel) {
			createParticlePanel();
			add(particlePanel);
		}
		
		createMapPanel();
		add(mapPanel);
	}
	
	/**
	 * Create the map panel
	 */
	protected void createMapPanel() {
		mapPanel = new MapPanel(model, mapPaneSize, this);
		
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		mapPanel.viewStart = new Point(initialViewStart);
		mapPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
	}
	
	/**
	 * Create the Connect panel to allow connection to a NXT brick
	 */
	protected void createConnectPanel() {
		connectPanel.add(nxtLabel);
		connectPanel.add(nxtName);
		connectPanel.add(connectButton);
		connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
		
		connectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				model.connect(nxtName.getText());
			}
		});
	}
	
	/**
	 * Create the control panel, which controls the GUI
	 */
	protected void createControlPanel() {
		controlPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		
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
	
	/**
	 * Create the map panel which shows the mouse position in map coordinates
	 */
	protected void createStatusPanel() {
		statusPanel.add(xLabel);
		statusPanel.add(xField);
		statusPanel.add(yLabel);
		statusPanel.add(yField);
	}
	
	/**
	 * Create the command panel - this is added to by overriding classes
	 */
	protected void createCommandPanel() {
		commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
	}
	
	/**
	 * Create the readings panel which shows the last range readings
	 */
	protected void createReadingsPanel() {
		readingsPanel.setBorder(BorderFactory.createTitledBorder("Last Readings"));
		readingsPanel.add(readingsField);
	}
	
	/**
	 * Create the move panel, which shows the last move made by the robot
	 */
	protected void createMovePanel() {
		lastMovePanel.setBorder(BorderFactory.createTitledBorder("Last Move"));
		lastMovePanel.add(lastMoveField);
	}
	
	/**
	 * Create the particle panel which shows range readings for a specific particle
	 */
	protected void createParticlePanel() {
		particlePanel.setBorder(BorderFactory.createTitledBorder("Selected Particle"));
		particlePanel.add(particleField);
	}
	
	/**
	 * Create the menu
	 */
	protected void createMenu() {
		createFileMenu();
		createViewMenu();
		createMapMenu();
		createCommandMenu();
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
		fileMenu.addSeparator();
		save.addActionListener(this);
		connect = new JMenuItem("Connect ...");
		fileMenu.add(connect);
		connect.addActionListener(this);
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
		//viewLog = new JCheckBoxMenuItem("Log");
		//viewLog.setSelected(showLog);
		//viewLog.addActionListener(this);
		//viewMenu.add(viewLog);
		viewMousePosition = new JCheckBoxMenuItem("Status bar");
		viewMousePosition.setSelected(showStatusPanel);
		viewMousePosition.addActionListener(this);
		viewMenu.add(viewMousePosition);
		viewControls = new JCheckBoxMenuItem("Zoom");
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
		//viewParticles = new JCheckBoxMenuItem("Particles");
		//viewParticles.setSelected(showParticles);
		//viewParticles.addActionListener(this);
		//viewMenu.add(viewParticles);
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
		colorMenu = new JMenu("Colors");
		mapMenu.add(colorMenu);
		gridColor = new JMenuItem("Grid");
		gridColor.addActionListener(this);
		colorMenu.add(gridColor);
		robotColor = new JMenuItem("Robot");
		robotColor.addActionListener(this);
		colorMenu.add(robotColor);
		mapColor = new JMenuItem("Map");
		mapColor.addActionListener(this);
		colorMenu.add(mapColor);
		particleColor = new JMenuItem("Particle");
		particleColor.addActionListener(this);
		colorMenu.add(particleColor);
		meshColor = new JMenuItem("Mesh");
		meshColor.addActionListener(this);
		colorMenu.add(meshColor);
		targetColor = new JMenuItem("Target");
		targetColor.addActionListener(this);
		colorMenu.add(targetColor);
		waypointColor = new JMenuItem("Waypoint");
		waypointColor.addActionListener(this);
		colorMenu.add(waypointColor);
		pathColor = new JMenuItem("Path");
		pathColor.addActionListener(this);
		colorMenu.add(pathColor);
		moveColor = new JMenuItem("Move");
		moveColor.addActionListener(this);
		colorMenu.add(moveColor);
		featureColor = new JMenuItem("Feature");
		featureColor.addActionListener(this);
		colorMenu.add(featureColor);
		backgroundColor = new JMenuItem("Background");
		backgroundColor.addActionListener(this);
		colorMenu.add(backgroundColor);
		closestColor = new JMenuItem("Closest");
		closestColor.addActionListener(this);
		colorMenu.add(closestColor);
		estimateColor = new JMenuItem("Estimate");
		estimateColor.addActionListener(this);
		colorMenu.add(estimateColor);
	}
	
	/**
	 * Create the Commands menu
	 */
	protected void createCommandMenu() {
		commandsMenu = new JMenu("Commands");
		menuBar.add(commandsMenu);
		getPose = new JMenuItem("Get Pose");
		commandsMenu.add(getPose);
		getPose.addActionListener(this);
		randomMove = new JMenuItem("Random Move");
		commandsMenu.add(randomMove);
		randomMove.addActionListener(this);
		localize = new JMenuItem("Localize");
		commandsMenu.add(localize);
		localize.addActionListener(this);
		stop = new JMenuItem("Stop");
		commandsMenu.add(stop);
		stop.addActionListener(this);
		calculatePath = new JMenuItem("Calculate Path");
		commandsMenu.add(calculatePath);
		calculatePath.addActionListener(this);
		followPath = new JMenuItem("Follow Path");
		commandsMenu.add(followPath);
		followPath.addActionListener(this);
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
		frame.getContentPane().add(content,BorderLayout.CENTER);
		content.title = title;
		content.frame = frame;
		
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
	    if (model.getMap() != null) {
	    	boolean inside = model.getMap().inside(new lejos.geom.Point((me.getX() / pixelsPerUnit + mapPanel.viewStart.x) , (mapPanel.getHeight() - me.getY())/ pixelsPerUnit + mapPanel.viewStart.y));  
	    	if (!inside) return;
	    }
	    
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
	public void whenConnected() {	
	}
	
	/**
	 * Override this method to perform some action (other than repaining) when an event is received
	 * 
	 * @param navEvent the event
	 */
	public void eventReceived(NavEvent navEvent) {	
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
		} else if (e.getSource() == connect) {
			String nxtName = (String)JOptionPane.showInputDialog(
                    this,
                    "Name:",
                    "Connect to NXT", 
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
			if (nxtName != null) model.connect(nxtName);
		} else if (e.getSource() == gridColor) {
			chooseColor("Grid", MapPanel.GRID_COLOR_INDEX);
		} else if (e.getSource() == robotColor) {
			chooseColor("Robot", MapPanel.ROBOT_COLOR_INDEX);
		} else if (e.getSource() == mapColor) {
			chooseColor("Map", MapPanel.MAP_COLOR_INDEX);
		} else if (e.getSource() == featureColor) {
			chooseColor("Feature", MapPanel.FEATURE_COLOR_INDEX);
		} else if (e.getSource() == pathColor) {
			chooseColor("Path", MapPanel.PATH_COLOR_INDEX);
		} else if (e.getSource() == moveColor) {
			chooseColor("Move", MapPanel.MOVE_COLOR_INDEX);
		} else if (e.getSource() == targetColor) {
			chooseColor("Target", MapPanel.TARGET_COLOR_INDEX);
		} else if (e.getSource() == particleColor) {
			chooseColor("Particle", MapPanel.PARTICLE_COLOR_INDEX);
		} else if (e.getSource() == waypointColor) {
			chooseColor("Waypoint", MapPanel.WAYPOINT_COLOR_INDEX);
		} else if (e.getSource() == backgroundColor) {
			chooseColor("Background", MapPanel.BACKGROUND_COLOR_INDEX);
			mapPanel.setBackground(mapPanel.colors[MapPanel.BACKGROUND_COLOR_INDEX]);
		} else if (e.getSource() == closestColor) {
			chooseColor("Closest", MapPanel.CLOSEST_COLOR_INDEX);
		} else if (e.getSource() == estimateColor) {
			chooseColor("Estimate", MapPanel.ESTIMATE_COLOR_INDEX);
		} else if (e.getSource() == meshColor) {
			chooseColor("Mesh", MapPanel.MESH_COLOR_INDEX);
			mapPanel.colors[MapPanel.NEIGHBOR_COLOR_INDEX] = mapPanel.colors[MapPanel.MESH_COLOR_INDEX];
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
			statusPanel.setVisible(viewMousePosition.isSelected());
			repaint();
		} else if (e.getSource() == viewControls) {
			controlPanel.setVisible(viewControls.isSelected());
		} else if (e.getSource() == viewCommands) {
			commandPanel.setVisible(viewCommands.isSelected());
		}  else if (e.getSource() == viewConnect) {
			connectPanel.setVisible(viewConnect.isSelected());
		} else if (e.getSource() == getPose) {
			model.getRobotPose();
		} else if (e.getSource() == randomMove) {
			model.randomMove();
		} else if (e.getSource() == localize) {
			model.localize();
		} else if (e.getSource() == stop) {
			model.stop();
		} else if (e.getSource() == calculatePath) {
			model.calculatePath();
			repaint();
		} else if (e.getSource() == followPath) {
			model.followPath();
		}
	}
	
	private void chooseColor(String name, int index) {
		Color newColor = JColorChooser.showDialog(
                this,
                "Choose " + name + " Color",
                mapPanel.colors[index]);
		mapPanel.colors[index] = newColor;
		repaint();
	}
}
