package lejos.robotics.mapping;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lejos.internal.config.ConfigManager;
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
public abstract class NavigationPanel extends JPanel implements MapApplicationUI, MouseListener, MouseMotionListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	protected final static String KEY_DEFAULT_NXT = "DEFAULT_NXT";
	protected final static String KEY_DEFAULT_MAP = "DEFAULT_MAP";
	protected final static String KEY_DIFF_PILOT_WHEEL_DIAMETER = "DIFFERENTIAL_PILOT_WHEEL_DIAMETER";
	protected final static String KEY_DIFF_PILOT_TRACK_WIDTH = "DIFFERENTIAL_PILOT_TRACK_WIDTH";
	protected final static String KEY_DIFF_PILOT_LEFT_MOTOR = "DIFFERENTIAL_PILOT_LEFT_MOTOR";
	protected final static String KEY_DIFF_PILOT_RIGHT_MOTOR = "DIFFERENTIAL_PILOT_RIGHT_MOTOR";
	protected final static String KEY_DIFF_PILOT_REVERSE = "DIFFERENTIAL_PILOT_REVERSE";
	protected final static String KEY_STEER_PILOT_WHEEL_DIAMETER = "STEERING_PILOT_WHEEL_DIAMETER";
	protected final static String KEY_STEER_PILOT_DRIVE_MOTOR = "STEERING_PILOT_DRIVE_MOTOR";
	protected final static String KEY_STEER_PILOT_DRIVE_MOTOR_REVERSE = "STEERING_PILOT_DRIVE_MOTOR_REVERSE";
	protected final static String KEY_STEER_PILOT_STEERING_MOTOR = "STEERING_PILOT_STEERING_MOTOR";
	protected final static String KEY_STEER_PILOT_LEFT_TACHO = "STEERING_PILOT_LEFT_TACHO_COUNT";
	protected final static String KEY_STEER_PILOT_RIGHT_TACHO = "STEERING_PILOT_RIGHT_TACHO_COUNT";
	protected final static String KEY_MESH_GRID_SIZE = "MESH_GRID_SIZE";
	protected final static String KEY_MESH_CLEARANCE = "MESH_CLEARANCE";
	protected final static String KEY_DETECTOR_DELAY = "RANGE_FEATURE_DETECTOR_DELAY";
	protected final static String KEY_DETECTOR_MAX_DISTANCE = "RANGE_FEATURE_DETECTOR_MAX_DISTANCE";
	protected final static String KEY_RANGE_SCANNER_GEAR_RATIO = "ROTATING_RANGE_SCANNER_GEAR_RATIO";
	protected final static String KEY_RANGE_SCANNER_HEAD_MOTOR = "ROTATING_RANGE_SCANNER_HEAD_MOTOR";
	protected final static String KEY_RANDOM_MOVE_MAX_DISTANCE = "RANDOM_MOVE_MAX_DISTANCE";
	protected final static String KEY_RANDOM_MOVE_CLEARANCE = "RANDOM_MOVE_CLEARANCE";
	protected final static String KEY_MCL_NUM_PARTICLES = "MCL_NUMBER_OF_PARTICLES";
	protected final static String KEY_MCL_CLEARANCE = "MCL_CLEARANCE";
	protected final static String KEY_MAX_TRAVEL_SPEED = "MAXIMUM_TRAVEL_SPEED";
	protected final static String KEY_MAX_ROTATE_SPEED = "MAXIMUM_ROTATE_SPEED";
	
	// Zoom control parameters
	protected int minZoom = 50;
	protected int maxZoom = 200;
	protected int zoomIncrement = 50;
	protected int zoomInitialValue = 150;
	protected boolean showZoomLabels = false;
	protected int zoomMajorTick = 25;
	
	protected float pixelsPerUnit = 1.5f;
	protected PCNavigationModel model = new PCNavigationModel(this);
	
	// Panels
	protected MapPanel mapPanel; 
	protected JPanel commandPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel statusPanel = new JPanel();
	protected JPanel xyPanel = new JPanel();
	protected JPanel logPanel = new JPanel();
	
	// Status panel
	protected JLabel xLabel = new JLabel("X:");
	protected JTextField xField = new JTextField(4);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(4);
	protected JLabel connectedLabel = new JLabel("Not Connected");
	protected JLabel mapLabel = new JLabel("No map");
	
	// Zoom Panel
	protected JPanel controlPanel = new JPanel();
	protected JSlider zoomSlider;
	
	// Connect Panel
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	
	// Control of features displayed
	protected boolean showConnectPanel = true, showStatusPanel = true, 
	                  showControlPanel = true, showCommandPanel = true,
	                  showReadingsPanel = true, showLastMovePanel = true,
	                  showParticlePanel = true, showMoves = false,
	                  showMesh = false, showGrid = true, showLog = false,
	                  showParticles = false, showLoadMapPanel = true,
	                  showEventPanel = true;
	
	// Last Readings panel
	protected JPanel readingsPanel = new JPanel();
	protected JTextField readingsField = new JTextField(12);
	
	// Last Move Panel
	protected JPanel lastMovePanel = new JPanel();
	protected JTextField lastMoveField = new JTextField(20);
	
	// Particle panel
	protected JPanel particlePanel = new JPanel();
	protected JTextField particleField = new JTextField(20);
	
	// Various data items
	protected Dimension mapPaneSize = new Dimension(700,600);
	protected Point startDrag;
	protected Point initialViewStart = new Point(0,0);
	protected String title;
	protected String description = "";
	protected String program = "../samples/MapTest.nxj";
	
	//Menu
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu fileMenu, aboutMenu, mapMenu, viewMenu, colorMenu, commandsMenu, configureMenu;
	protected JMenuItem exit, about, clear, repaint, reset, open, save, connect, gridColor, robotColor,
						mapColor, particleColor, meshColor, targetColor, waypointColor,
						pathColor, moveColor, featureColor, backgroundColor, estimateColor, closestColor,
						getPose, randomMove, localize, stop, calculatePath, followPath, pilot, scanner,
						finder, detector, random, mcl, defaultColors;
	protected JCheckBoxMenuItem viewGrid, viewMousePosition, viewControls,
	                          viewConnect, viewCommands, viewMesh, viewLog,
	                          viewLastMove, viewParticlePanel, viewParticles,
	                          viewLoadMap, viewEventForm;
	protected JFileChooser chooser = new JFileChooser();
	protected EventPanel eventPanel = new EventPanel(model, this,  null);
	protected JColorChooser colorChooser = new JColorChooser();
	protected JFrame frame;
	protected JPanel loadPanel = new JPanel();
	protected JLabel mapFileLabel = new JLabel("Map file");
	protected JTextField mapFileField = new JTextField(10);
	protected JButton loadMapButton = new JButton("Load");
	protected JCheckBox uploadBox = new JCheckBox("Upload NXT Program?");
	
	// Configure Pilot
	protected JDialog configurePilot;
	protected JPanel differentialPanel = new JPanel();
	protected JPanel pilotForm = new JPanel();
	protected JLabel pilotTypeLabel = new JLabel("Pilot type:");
	protected String[] pilotTypes = {"Differential", "Steering", "Segway", "Omnidirectional"};
	protected JComboBox pilotTypeBox = new JComboBox(pilotTypes);
	protected JLabel wheelDiameterLabel = new JLabel("Wheel Diameter:");
	protected JTextField wheelDiameterField = new JTextField(6);
	protected JLabel trackWidthLabel = new JLabel("Track Width:");
	protected JTextField trackWidthField = new JTextField(6);
	protected JButton pilotOKButton = new JButton("OK");
	protected String[] motors = {"A","B","C"};
	protected JLabel leftMotorLabel = new JLabel("Left Motor:");
	protected JComboBox leftMotorBox = new JComboBox(motors);
	protected JLabel rightMotorLabel = new JLabel("Right Motor:");
	protected JComboBox rightMotorBox = new JComboBox(motors);
	protected JLabel reverseLabel = new JLabel("Reverse?");
	protected JCheckBox reverseBox = new JCheckBox();
	protected JPanel steeringPanel = new JPanel();
	protected JLabel steerWheelDiameterLabel = new JLabel("Wheel Diameter:");
	protected JTextField steerWheelDiameterField = new JTextField(4);
	protected JLabel driveMotorLabel = new JLabel("Drive Motor:");
	protected JComboBox driveMotorBox = new JComboBox(motors);
	protected JLabel driveReverseLabel = new JLabel("Drive reverse?");
	protected JCheckBox driveReverseBox = new JCheckBox();
	protected JLabel steeringMotorLabel = new JLabel("Steering Motor:");
	protected JComboBox steeringMotorBox = new JComboBox(motors);
	protected JLabel leftTachoLabel = new JLabel("Left Motor Tacho Count");
	protected JTextField leftTachoField = new JTextField(4);
	protected JLabel rightTachoLabel = new JLabel("Right Motor Tacho Count");
	protected JTextField rightTachoField = new JTextField(4);	
	
	// Configure 4-way Mesh path finder
	protected JPanel finderPanel = new JPanel();
	protected JDialog configureMesh;
	protected JPanel meshPanel = new JPanel();
	protected JPanel finderForm = new JPanel();
	protected JLabel pfLabel = new JLabel("Path Finder:");
	protected String[] pathFinders = {"4-way Mesh", "Random","Shortest"};
	protected JComboBox pfBox = new JComboBox(pathFinders);
	protected JLabel gridSizeLabel = new JLabel("Grid Size:");
	protected JTextField gridSizeField = new JTextField(4);
	protected JLabel clearanceLabel = new JLabel("Clearance:");
	protected JTextField clearanceField = new JTextField(4);
	protected JButton finderOKButton = new JButton("OK");
	
	// Configure Range Feature Detector
	protected JLabel delayLabel = new JLabel("Detector Delay:");
	protected JTextField delayField = new JTextField(4);
	protected JLabel maxDistanceLabel = new JLabel("Maximum distance:");
	protected JTextField maxDistanceField = new JTextField(4);
	protected JPanel detectorPanel = new JPanel();
	protected JPanel detectorForm = new JPanel();
	protected JDialog configureDetector;
	protected JButton detectorOKButton = new JButton("OK");
	
	// Configure Rotating Range Scanner
	protected JLabel gearRatioLabel = new JLabel("Gear ratio:");
	protected JTextField gearRatioField = new JTextField(4);
	protected JLabel headMotorLabel = new JLabel("Head motor");
	protected JComboBox headMotorBox = new JComboBox(motors);
	protected JPanel scannerPanel = new JPanel();
	protected JPanel scannerForm = new JPanel();
	protected JDialog configureScanner;
	protected JButton scannerOKButton = new JButton("OK");
	
	// Configure Random move
	protected JLabel maxDistLabel = new JLabel("Maximum distance:");
	protected JTextField maxDistField = new JTextField(4);
	protected JLabel clearLabel = new JLabel("Clearance:");
	protected JTextField clearField = new JTextField(4);
	protected JPanel randomPanel = new JPanel();
	protected JPanel randomForm = new JPanel();
	protected JButton randomOKButton = new JButton("OK");
	protected JDialog configureRandom;
	
	// Configure MCL
	protected JLabel numParticlesLabel = new JLabel("Number of particles:");
	protected JTextField numParticlesField = new JTextField(4);
	protected JLabel borderLabel = new JLabel("Clearance:");
	protected JTextField borderField = new JTextField(4);
	protected JPanel mclPanel = new JPanel();
	protected JPanel mclForm = new JPanel();
	protected JButton mclOKButton = new JButton("OK");
	protected JDialog configureMCL;
	
	protected Properties props = new Properties();
	
	public NavigationPanel() {	
		loadProperties();
	}
	
	protected void loadProperties() {
		try {
			ConfigManager.loadPropertiesFile(ConfigManager.CONFIG_NAVPANEL, props);
		} catch (IOException ioe) {
			log("Error loading properties file: " + ioe.getMessage());
		}
	}

	protected void saveProperties() {
		mapPanel.saveColors(props);
		try {
			ConfigManager.savePropertiesFile(ConfigManager.CONFIG_NAVPANEL, props);
		} catch (IOException ioe) {
			log("Failed to store properties");
		}
	}
	
	/**
	 * Build all the panels
	 */
	public void buildPanels() {
		createPilotPanel();
		createDifferentialPanel();
		createSteeringPanel();
		createFinderPanel();
		createMeshPanel();
		createDetectorPanel();
		createScannerPanel();
		createRandomPanel();
		createMCLPanel();
		createStatusPanel();
		createConnectPanel();
		createControlPanel();
		createCommandPanel();
		createMovePanel();
		createParticlePanel();
		createMapPanel();
		createLoadPanel();
		createReadingsPanel();
		createMenu();
		mapPanel.getColors(props);
		saveProperties();
	}
	
	/**
	 * Build the GUI- used by MCLTest but deprecated
	 */
	protected void buildGUI() {
		buildPanels();
		if (showStatusPanel) add(xyPanel);	
		if (showConnectPanel) add(connectPanel);
		if (showControlPanel) add(controlPanel);
		if (showCommandPanel) add(commandPanel);
		if (showReadingsPanel) add(readingsPanel);
		if (showLastMovePanel) add(lastMovePanel);
		if (showParticlePanel) add(particlePanel);
		
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
	 * Create load map panel
	 */
	protected void createLoadPanel() {
		loadPanel.add(mapFileLabel);
		loadPanel.add(mapFileField);
		mapFileField.setText(props.getProperty(KEY_DEFAULT_MAP,""));
		loadPanel.add(loadMapButton);
		loadPanel.setBorder(BorderFactory.createTitledBorder("Load Map"));
		loadMapButton.addActionListener(this);
	}
	
	/**
	 * Create the Pilot Configuration Form
	 */
	protected void createPilotPanel() {
		pilotForm.add(pilotTypeLabel);
		pilotForm.add(pilotTypeBox);
		pilotForm.add(differentialPanel);
		pilotForm.add(pilotOKButton);
		pilotForm.setPreferredSize(new Dimension(300,400));
		
		pilotTypeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pilotForm.remove(differentialPanel);
				pilotForm.remove(steeringPanel);
				switch (pilotTypeBox.getSelectedIndex()) {
				case 0:
					pilotForm.remove(pilotOKButton);
					pilotForm.add(differentialPanel);
					pilotForm.add(pilotOKButton);
					pilotForm.revalidate();
					pilotForm.repaint();
					break;
				case 1:
					pilotForm.remove(pilotOKButton);
					pilotForm.add(steeringPanel);
					pilotForm.add(pilotOKButton);
					pilotForm.revalidate();
					pilotForm.repaint();
					break;
				case 2:
					pilotForm.revalidate();
					pilotForm.repaint();
					break;
				case 3:
					pilotForm.revalidate();
					pilotForm.repaint();
					break;
				}
			}
		});
		
		pilotOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					switch(pilotTypeBox.getSelectedIndex()) {
					case 0:			
						model.setDifferentialPilotParams(Float.parseFloat(wheelDiameterField.getText()), 
							Float.parseFloat(trackWidthField.getText()),
							leftMotorBox.getSelectedIndex(), rightMotorBox.getSelectedIndex(), 
							reverseBox.isSelected());
						props.setProperty(KEY_DIFF_PILOT_WHEEL_DIAMETER, wheelDiameterField.getText());
						props.setProperty(KEY_DIFF_PILOT_TRACK_WIDTH, trackWidthField.getText());
						props.setProperty(KEY_DIFF_PILOT_LEFT_MOTOR, "" + leftMotorBox.getSelectedIndex());
						props.setProperty(KEY_DIFF_PILOT_RIGHT_MOTOR, "" + rightMotorBox.getSelectedIndex());
						props.setProperty(KEY_DIFF_PILOT_REVERSE, "" + reverseBox.isSelected());
						saveProperties();
						break;
					case 1:
						props.setProperty(KEY_STEER_PILOT_WHEEL_DIAMETER, steerWheelDiameterField.getText());
						props.setProperty(KEY_STEER_PILOT_DRIVE_MOTOR, "" + driveMotorBox.getSelectedIndex());
						props.setProperty(KEY_STEER_PILOT_DRIVE_MOTOR_REVERSE, "" + driveReverseBox.isSelected());
						props.setProperty(KEY_STEER_PILOT_STEERING_MOTOR, "" + steeringMotorBox.getSelectedIndex());
						props.setProperty(KEY_STEER_PILOT_LEFT_TACHO, leftTachoField.getText());
						props.setProperty(KEY_STEER_PILOT_RIGHT_TACHO, rightTachoField.getText());
						saveProperties();
						break;
					}
					configurePilot.setVisible(false);
				} catch (NumberFormatException nfe) {
					error("Inalid parameter");
				}
			}
		});
	}
	
	protected void createDifferentialPanel() {
		differentialPanel.setLayout(new SpringLayout());

		differentialPanel.add(wheelDiameterLabel);
		differentialPanel.add(wheelDiameterField);
		differentialPanel.add(trackWidthLabel);
		differentialPanel.add(trackWidthField);
		differentialPanel.add(leftMotorLabel);
		differentialPanel.add(leftMotorBox);
		differentialPanel.add(rightMotorLabel);
		differentialPanel.add(rightMotorBox);
		differentialPanel.add(reverseLabel);
		differentialPanel.add(reverseBox);
		
		wheelDiameterField.setText(props.getProperty(KEY_DIFF_PILOT_WHEEL_DIAMETER, "5.6"));
		trackWidthField.setText(props.getProperty(KEY_DIFF_PILOT_TRACK_WIDTH,"16.0"));
		leftMotorBox.setSelectedIndex(Integer.parseInt(props.getProperty(KEY_DIFF_PILOT_LEFT_MOTOR, "0")));
		rightMotorBox.setSelectedIndex(Integer.parseInt(props.getProperty(KEY_DIFF_PILOT_RIGHT_MOTOR, "2")));
		reverseBox.setSelected(Boolean.parseBoolean(props.getProperty(KEY_DIFF_PILOT_REVERSE, "false")));
		
		makeCompactGrid(differentialPanel,
                5, 2,    //rows, cols
                20, 20,  //initX, initY
                20, 20); //xPad, yPad
	}
	
	protected void createSteeringPanel() {
		steeringPanel.setLayout(new SpringLayout());

		steeringPanel.add(steerWheelDiameterLabel);
		steeringPanel.add(steerWheelDiameterField);
		steeringPanel.add(driveMotorLabel);
		steeringPanel.add(driveMotorBox);
		steeringPanel.add(driveReverseLabel);
		steeringPanel.add(driveReverseBox);
		steeringPanel.add(steeringMotorLabel);
		steeringPanel.add(steeringMotorBox);
		steeringPanel.add(leftTachoLabel);
		steeringPanel.add(leftTachoField);
		steeringPanel.add(rightTachoLabel);
		steeringPanel.add(rightTachoField);
		
		steerWheelDiameterField.setText(props.getProperty(KEY_STEER_PILOT_WHEEL_DIAMETER, "5.6"));
		driveMotorBox.setSelectedIndex(Integer.parseInt(props.getProperty(KEY_STEER_PILOT_DRIVE_MOTOR, "0")));
		driveReverseBox.setSelected(Boolean.parseBoolean(props.getProperty(KEY_STEER_PILOT_DRIVE_MOTOR_REVERSE, "false")));
		driveMotorBox.setSelectedIndex(Integer.parseInt(props.getProperty(KEY_STEER_PILOT_DRIVE_MOTOR, "0")));
		leftTachoField.setText(props.getProperty(KEY_STEER_PILOT_LEFT_TACHO, ""));
		rightTachoField.setText(props.getProperty(KEY_STEER_PILOT_RIGHT_TACHO, ""));
		
		makeCompactGrid(steeringPanel,
                6, 2,    //rows, cols
                20, 20,  //initX, initY
                20, 20); //xPad, yPad
	}

	
	/**
	 * Create the PathFinder configuration form
	 */
	protected void createFinderPanel() {
		finderPanel.setLayout(new SpringLayout());
		finderPanel.add(pfLabel);
		finderPanel.add(pfBox);
		
		makeCompactGrid(finderPanel,
                1, 2,    //rows, cols
                20, 20,  //initX, initY
                20, 20); //xPad, yPad
		
		finderForm.add(finderPanel);
		
		finderForm.add(meshPanel);
		finderForm.add(finderOKButton);
		
		finderForm.setPreferredSize(new Dimension(200,250));
		
		pfBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (pfBox.getSelectedIndex()) {
				case 0:
					finderForm.remove(finderOKButton);
					finderForm.add(meshPanel);
					finderForm.add(finderOKButton);
					finderForm.revalidate();
					finderForm.repaint();
					break;
				case 1:
					finderForm.remove(meshPanel);
					finderForm.revalidate();
					finderForm.repaint();
					break;
				case 2:
					finderForm.remove(meshPanel);
					finderForm.revalidate();
					finderForm.repaint();
					break;
				}
			}
		});
		
		finderOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.setPathFinder(pfBox.getSelectedIndex());
					switch (pfBox.getSelectedIndex()) {
						case 0:
							model.setMeshParams(Integer.parseInt(gridSizeField.getText()), Integer.parseInt(clearanceField.getText()));
							props.setProperty(KEY_MESH_CLEARANCE, clearanceField.getText());
							props.setProperty(KEY_MESH_GRID_SIZE, gridSizeField.getText());
							saveProperties();
							break;		
					}
					configureMesh.setVisible(false);
				} catch (NumberFormatException nfe) {
					error("Inalid parameter");
				}
			}
		});
		
	}
	
	/**
	 * Create the mesh configuration panel
	 */
	protected void createMeshPanel() {
		meshPanel.setLayout(new SpringLayout());
		meshPanel.add(gridSizeLabel);
		meshPanel.add(gridSizeField);
		meshPanel.add(clearanceLabel);
		meshPanel.add(clearanceField);
		
		gridSizeField.setText(props.getProperty(KEY_MESH_CLEARANCE, ""));
		clearanceField.setText(props.getProperty(KEY_MESH_GRID_SIZE, ""));
		
		makeCompactGrid(meshPanel,
                2, 2, //rows, cols
                20, 20,        //initX, initY
                20, 20);       //xPad, yPad	
	}
	
	/**
	 * Create the Feature Detector configuration form
	 */
	protected void createDetectorPanel() {
		detectorPanel.setLayout(new SpringLayout());
		detectorPanel.add(delayLabel);
		detectorPanel.add(delayField);
		detectorPanel.add(maxDistanceLabel);
		detectorPanel.add(maxDistanceField);
		
		delayField.setText(props.getProperty(KEY_DETECTOR_DELAY, ""));
		maxDistanceField.setText(props.getProperty(KEY_DETECTOR_MAX_DISTANCE, ""));
		
		makeCompactGrid(detectorPanel,
                2, 2, //rows, cols
                20, 20,        //initX, initY
                20, 20);       //xPad, yPad
		
		detectorForm.add(detectorPanel);
		detectorForm.add(detectorOKButton);
		
		detectorOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.setRangeFeatureParams(Float.parseFloat(maxDistanceField.getText()), Integer.parseInt(delayField.getText()));
					props.setProperty(KEY_DETECTOR_DELAY, delayField.getText());
					props.setProperty(KEY_DETECTOR_MAX_DISTANCE, maxDistanceField.getText());
					saveProperties();
					configureDetector.setVisible(false);
				} catch (NumberFormatException nfe) {
					error("Inalid parameter");
				}
			}
		});	
	}
	
	/**
	 * Create the range scanner configuration form
	 */
	protected void createScannerPanel() {
		scannerPanel.setLayout(new SpringLayout());
		scannerPanel.add(gearRatioLabel);
		scannerPanel.add(gearRatioField);
		scannerPanel.add(headMotorLabel);
		scannerPanel.add(headMotorBox);
		
		gearRatioField.setText(props.getProperty(KEY_RANGE_SCANNER_GEAR_RATIO, ""));
		headMotorBox.setSelectedIndex(Integer.parseInt(props.getProperty(KEY_RANGE_SCANNER_HEAD_MOTOR, "0")));
		
		makeCompactGrid(scannerPanel,
                2, 2, //rows, cols
                20, 20,        //initX, initY
                20, 20);       //xPad, yPad
		
		scannerForm.add(scannerPanel);
		scannerForm.add(scannerOKButton);
		
		scannerOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.setRotatingRangeScannerParams(Integer.parseInt(gearRatioField.getText()), headMotorBox.getSelectedIndex());
					props.setProperty(KEY_RANGE_SCANNER_GEAR_RATIO, gearRatioField.getText());
					saveProperties();
					configureScanner.setVisible(false);
				} catch (NumberFormatException nfe) {
					error("Inalid parameter");
				}
			}
		});	
	}
	
	/**
	 * Create the Random Move configuration form
	 */
	protected void createRandomPanel() {
		randomPanel.setLayout(new SpringLayout());
		randomPanel.add(maxDistLabel);
		randomPanel.add(maxDistField);
		randomPanel.add(clearLabel);
		randomPanel.add(clearField);
		
		maxDistField.setText(props.getProperty(KEY_RANDOM_MOVE_MAX_DISTANCE, ""));
		clearField.setText(props.getProperty(KEY_RANDOM_MOVE_CLEARANCE,""));
		
		makeCompactGrid(randomPanel,
                2, 2, //rows, cols
                20, 20,        //initX, initY
                20, 20);       //xPad, yPad
		
		randomForm.add(randomPanel);
		randomForm.add(randomOKButton);
		
		randomOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					model.sendRandomMoveParams(Float.parseFloat(maxDistField.getText()), Float.parseFloat(clearField.getText()));
					props.setProperty(KEY_RANDOM_MOVE_MAX_DISTANCE, maxDistField.getText());
					props.setProperty(KEY_RANDOM_MOVE_CLEARANCE, clearField.getText());
					saveProperties();
					configureRandom.setVisible(false);
				} catch (NumberFormatException nfe) {
					error("Inalid parameter");
				}
			}
		});	
	}
	
	/**
	 * Create the MCL configuration form
	 */
	protected void createMCLPanel() {
		mclPanel.setLayout(new SpringLayout());
		mclPanel.add(numParticlesLabel);
		mclPanel.add(numParticlesField);
		mclPanel.add(borderLabel);
		mclPanel.add(borderField);
		
		numParticlesField.setText(props.getProperty(KEY_MCL_NUM_PARTICLES,""));
		borderField.setText(props.getProperty(KEY_MCL_CLEARANCE, ""));
		
		
		makeCompactGrid(mclPanel,
                2, 2, //rows, cols
                20, 20,        //initX, initY
                20, 20);       //xPad, yPad
		
		mclForm.add(mclPanel);
		mclForm.add(mclOKButton);
		
		mclOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//model.sendRandomMoveParams(Float.parseFloat(maxDistField.getText()), Float.parseFloat(clearField.getText()));
				props.setProperty(KEY_MCL_NUM_PARTICLES, numParticlesField.getText());
				props.setProperty(KEY_MCL_CLEARANCE, borderField.getText());
				saveProperties();
				configureMCL.setVisible(false);
			}
		});	
	}
	
	/**
	 * Create the Connect panel to allow connection to a NXT brick
	 */
	protected void createConnectPanel() {
		connectPanel.add(nxtLabel);
		connectPanel.add(nxtName);
		nxtName.setText(props.getProperty(KEY_DEFAULT_NXT,""));
		connectPanel.add(connectButton);
		connectPanel.add(uploadBox);
		uploadBox.setSelected(true);
		connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
		
		connectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (uploadBox.isSelected()) {
					try {
						model.connectAndUpload(nxtName.getText(), new File(program));
					} catch (FileNotFoundException e) {
						return;
					}
				}
				model.connect(nxtName.getText());
				props.setProperty(KEY_DEFAULT_NXT, nxtName.getText());
				saveProperties();
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
	protected void createXYPanel() {
		xyPanel.add(xLabel);
		xyPanel.add(xField);
		xyPanel.add(yLabel);
		xyPanel.add(yField);
	}
	
	/** 
	 * Create the status Panel
	 */
	protected void createStatusPanel() {
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.LINE_AXIS));
		statusPanel.add(Box.createHorizontalGlue());
		statusPanel.add(connectedLabel);
		statusPanel.add(Box.createHorizontalGlue());
		createXYPanel();
		statusPanel.add(xyPanel);
		statusPanel.add(Box.createHorizontalGlue());
		statusPanel.add(mapLabel);
		statusPanel.add(Box.createHorizontalGlue());
	}
	
	/**
	 * Create the command panel - this is added to by overriding classes
	 */
	protected void createCommandPanel() {
		commandPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
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
		createConfigureMenu();
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
		viewMenu.add(viewGrid);
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
		viewCommands = new JCheckBoxMenuItem("Controls");
		viewCommands.setSelected(showCommandPanel);
		viewCommands.addActionListener(this);
		viewMenu.add(viewCommands);
		//viewParticles = new JCheckBoxMenuItem("Particles");
		//viewParticles.setSelected(showParticles);
		//viewParticles.addActionListener(this);
		//viewMenu.add(viewParticles);
		viewLoadMap = new JCheckBoxMenuItem("Load Map");
		viewLoadMap.setSelected(showLoadMapPanel);
		viewLoadMap.addActionListener(this);
		viewMenu.add(viewLoadMap);
		viewEventForm = new JCheckBoxMenuItem("Event Form");
		viewEventForm.setSelected(showEventPanel);
		viewEventForm.addActionListener(this);
		viewMenu.add(viewEventForm);
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
		backgroundColor = new JMenuItem("Background");
		backgroundColor.addActionListener(this);
		colorMenu.add(backgroundColor);
		closestColor = new JMenuItem("Closest");
		closestColor.addActionListener(this);
		colorMenu.add(closestColor);
		estimateColor = new JMenuItem("Estimate");
		estimateColor.addActionListener(this);
		colorMenu.add(estimateColor);
		featureColor = new JMenuItem("Feature");
		featureColor.addActionListener(this);
		colorMenu.add(featureColor);
		gridColor = new JMenuItem("Grid");
		gridColor.addActionListener(this);
		colorMenu.add(gridColor);
		mapColor = new JMenuItem("Map");
		mapColor.addActionListener(this);
		colorMenu.add(mapColor);
		meshColor = new JMenuItem("Mesh");
		meshColor.addActionListener(this);
		colorMenu.add(meshColor);
		moveColor = new JMenuItem("Move");
		moveColor.addActionListener(this);
		colorMenu.add(moveColor);
		particleColor = new JMenuItem("Particle");
		particleColor.addActionListener(this);
		colorMenu.add(particleColor);
		pathColor = new JMenuItem("Path");
		pathColor.addActionListener(this);
		colorMenu.add(pathColor);
		defaultColors = new JMenuItem("Reset Defaults");
		defaultColors.addActionListener(this);
		colorMenu.add(defaultColors);
		robotColor = new JMenuItem("Robot");
		robotColor.addActionListener(this);
		colorMenu.add(robotColor);
		targetColor = new JMenuItem("Target");
		targetColor.addActionListener(this);
		colorMenu.add(targetColor);
		waypointColor = new JMenuItem("Waypoint");
		waypointColor.addActionListener(this);
		colorMenu.add(waypointColor);
	}
	
	/**
	 * Create the Commands menu
	 */
	protected void createCommandMenu() {
		commandsMenu = new JMenu("Commands");
		menuBar.add(commandsMenu);
		
		calculatePath = new JMenuItem("Calculate Path");
		commandsMenu.add(calculatePath);
		calculatePath.addActionListener(this);
		followPath = new JMenuItem("Follow Path");
		commandsMenu.add(followPath);
		followPath.addActionListener(this);
		getPose = new JMenuItem("Get Pose");
		commandsMenu.add(getPose);
		getPose.addActionListener(this);
		localize = new JMenuItem("Localize");
		commandsMenu.add(localize);
		localize.addActionListener(this);
		randomMove = new JMenuItem("Random Move");
		commandsMenu.add(randomMove);
		randomMove.addActionListener(this);
		stop = new JMenuItem("Stop");
		commandsMenu.add(stop);
		stop.addActionListener(this);
	}
	
	/**
	 * Create the Configure menu
	 */
	protected void createConfigureMenu() {
		configureMenu = new JMenu("Configure");
		menuBar.add(configureMenu);
		pilot = new JMenuItem("Pilot ...");
		configureMenu.add(pilot);
		pilot.addActionListener(this);
		finder = new JMenuItem("Path Finder ...");
		configureMenu.add(finder);
		finder.addActionListener(this);
		detector = new JMenuItem("Feature Detector ...");
		configureMenu.add(detector);
		detector.addActionListener(this);
		scanner = new JMenuItem("Range Scanner ...");
		configureMenu.add(scanner);
		scanner.addActionListener(this);
		random = new JMenuItem("Random Move ...");
		configureMenu.add(random);
		random.addActionListener(this);
		mcl = new JMenuItem("MCL ...");
		configureMenu.add(mcl);
		mcl.addActionListener(this);
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
	 * Show a pop-up error message
	 */
	public void error(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
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
    		@Override
			public void windowClosing(WindowEvent event) {
    			System.exit(0);
    		}
    	});
    	if (menuBar != null) frame.setJMenuBar(menuBar);
    	frame.setVisible(true);
    	return (frame);
	}
	
	/**
	 * Version without a menu
	 */
	public static JFrame openInJFrame(NavigationPanel content, int width, int height,
            String title, Color bgColor) {
		return NavigationPanel.openInJFrame(content, width, height, title, bgColor, null);
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
		connectedLabel.setText("Connected");
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
	 * Called when a menu item is selected or button clicked
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
	            model.loadMap(file.getPath(),0);
	            if (model.getMap() != null) mapLabel.setText("Map: " + file.getPath());
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
			if (nxtName != null) {
				try {
					model.connectAndUpload(nxtName, new File(program));
				} catch (FileNotFoundException e1) {
					return;
				}
				model.connect(nxtName);
			}
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
		} else if (e.getSource() == viewEventForm) {
			eventPanel.setVisible(viewEventForm.isSelected());
			repaint();
		} else if (e.getSource() == viewMousePosition) {
			statusPanel.setVisible(viewMousePosition.isSelected());
			repaint();
		} else if (e.getSource() == viewControls) {
			controlPanel.setVisible(viewControls.isSelected());
		} else if (e.getSource() == viewCommands) {
			commandPanel.setVisible(viewCommands.isSelected());
		} else if (e.getSource() == viewConnect) {
			connectPanel.setVisible(viewConnect.isSelected());
		} else if (e.getSource() == viewLoadMap) {
			loadPanel.setVisible(viewLoadMap.isSelected());
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
		} else if (e.getSource() == defaultColors) {
			mapPanel.setDefaults();
			saveProperties();
			repaint();
		} else if (e.getSource() == loadMapButton) {
			model.loadMap(mapFileField.getText(),pfBox.getSelectedIndex());
			if (model.getMap() != null) {
				mapLabel.setText("Map: " + mapFileField.getText());
				props.setProperty(KEY_DEFAULT_MAP, mapFileField.getText());
				saveProperties();
			}
			repaint();
		} else if (e.getSource() == pilot) {
			configurePilot = new JDialog(frame, "Configure Pilot", true);
			configurePilot.setContentPane(pilotForm);
			configurePilot.setLocation(200, 100);
			configurePilot.pack();
			configurePilot.setVisible(true);
		} else if (e.getSource() == finder) {
			configureMesh = new JDialog(frame, "Configure Path Finder", true);
			configureMesh.setContentPane(finderForm);
			configureMesh.setLocation(200, 100);
			configureMesh.pack();
			configureMesh.setVisible(true);
		} else if (e.getSource() == detector) {
			configureDetector = new JDialog(frame, "Configure Range Feature Detector", true);
			configureDetector.setContentPane(detectorForm);
			configureDetector.setLocation(200, 100);
			configureDetector.pack();
			configureDetector.setVisible(true);
		} else if (e.getSource() == scanner) {
			configureScanner = new JDialog(frame, "Configure Range Scanner", true);
			configureScanner.setContentPane(scannerForm);
			configureScanner.setLocation(200, 100);
			configureScanner.pack();
			configureScanner.setVisible(true);
		} else if (e.getSource() == random) {
			configureRandom = new JDialog(frame, "Configure Random Move", true);
			configureRandom.setContentPane(randomForm);
			configureRandom.setLocation(200, 100);
			configureRandom.pack();
			configureRandom.setVisible(true);
		} else if (e.getSource() == mcl) {
			configureMCL = new JDialog(frame, "Configure MCL", true);
			configureMCL.setContentPane(mclForm);
			configureMCL.setLocation(200, 100);
			configureMCL.pack();
			configureMCL.setVisible(true);
		}
	}
	
	/**
	 * Set the title use for the frame and the About popup
	 * 
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * Set the description used in the About popup
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Set the size of the map panel
	 * 
	 * @param size the size as a Dimension
	 */
	public void setMapPanelSize(Dimension size) {
		mapPaneSize = size;
		repaint();
	}
	
	public void setMapOrigin(Point origin) {
		mapPanel.viewStart = origin;
		repaint();
	}
	
	/**
	 * Choose a color and change the current value for the selected index
	 */
	private void chooseColor(String name, int index) {
		Color newColor = JColorChooser.showDialog(
                this,
                "Choose " + name + " Color",
                mapPanel.colors[index]);
		log("Setting color " + index + " to " + newColor);
		mapPanel.colors[index] = newColor;
		saveProperties();
		repaint();
	}
	
	/**
	 * Helper method to lay out forms that use SpringLayout
	 */	
	private static void makeCompactGrid(Container parent,int rows, int cols,int initialX, int initialY, int xPad, int yPad) {
		SpringLayout layout = (SpringLayout)parent.getLayout();

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
				getConstraintsForCell(r, c, parent, cols).getWidth());
			}
			
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
					getConstraintsForCell(r, c, parent, cols);
					constraints.setX(x);
					constraints.setWidth(width);
			}
			
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
				getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =	getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
	
	/**
	 * Helper method to layout forms that use SpringLayout
	 */
    private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
    	SpringLayout layout = (SpringLayout) parent.getLayout();
    	Component c = parent.getComponent(row * cols + col);
    	return layout.getConstraints(c);
    }
}
