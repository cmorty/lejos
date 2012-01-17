package org.lejos.pcsample.mclcommand;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.mapping.NavigationPanel;

/**
 * Test of the Monte Carlo Localization algorithm for global localization.
 * 
 * You should run MCLTest from the samples project on the NXT. See the comments in that sample for how 
 * to set your robot up.
 * 
 * You will need to set up a line map of your room (or other environment). This should be in the SVG file, Roon.svg.
 * 
 * You can use a tool like svg-edit to set up this map. Make sure you only use <line> tags.
 * 
 * To control your localization robot from the PC, run this sample and connect to your robot, by typing in 
 * the name of the brick and pressing Connect.
 * 
 * Then press Load Map, and your map will be loaded and displayed with a randomly generated particle set.
 * 
 * You then control the robot by pressing Get Pose and Random Move. Get Pose will cause the robot to take
 * range readings and update its pose estimate. This will update the particle set.
 * 
 * Random Move will cause the robot to make a random travel, followed by rotate move. Again the particle set will 
 * be updated.
 * 
 * Keep clicking Get Pose and Random Move until the robot has a good estimate of its pose. You should see the
 * particle set cluster around a few possible poses, and eventually find the correct pose.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MCLCommand extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1050;
	private static final int FRAME_HEIGHT = 700;
	private static final int NUM_PARTICLES = 200;
	private static final String TITLE = "MCL Command";
	private static final int INITIAL_ZOOM = 150;
	private static final Point INITIAL_MAP_ORIGIN = new Point(-150,-30);
	private static final int MCL_CLEARANCE = 20;
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,500);

	private final JPanel leftPanel = new JPanel();
	private final JPanel rightPanel = new JPanel();
	private final JButton randomButton = new JButton("Random move");
	private final JButton getPoseButton = new JButton("Get Pose");
	
	private static MCLPoseProvider mcl = new MCLPoseProvider(null,NUM_PARTICLES,MCL_CLEARANCE);
  
  	/**
   	* Create a MapTest object and display it in a GUI frame.
   	* Then connect to the NXT.
   	*/
  	public static void main(String[] args) throws Exception {
  		(new MCLCommand()).run();
  	}
  
  	public MCLCommand() {
  		setTitle(TITLE);
  		setDescription("MCLCommand shows the Monte Carlo Localization\nalgorithm in action");
  		
  		buildGUI();
  	}
  
  	/**
  	 * Build the application-specific GUI
  	 */
  	@Override
  	protected void buildGUI() {
  		setLayout(new BorderLayout());
  		// All panels required
	    showZoomLabels = true;
	    buildPanels();
	    
	    // Set the size of the map panel, and the viewport origin 
	    setMapPanelSize(MAP_AREA_SIZE);
	    setMapOrigin(INITIAL_MAP_ORIGIN);
	    
	    // Add the Get Pose and Random Move buttons
		commandPanel.add(getPoseButton);
		commandPanel.add(randomButton);
		
		// disable buttons until connected
		getPoseButton.setEnabled(false);
		randomButton.setEnabled(false);
	
		// When Get pose is pressed, invoke the MCL Pose provider
		// to take readings and get the pose. Then get the updated
		// particles, the details of the estimated pose and the range
		// readings.
		getPoseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.getPose();
				model.getRemoteParticles();
				model.getEstimatedPose();
				//System.out.println("Max weight:" + model.getParticles().getMaxWeight());
				model.getRemoteReadings();
				getPoseButton.setEnabled(false);
			}
		});
		
		// When the Random Move button is pressed, make a random move 
		// and get the updated particles
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.randomMove();
				model.getRemoteParticles();
			}
		});
		
		// Switch on tool tips for particle weights
		mapPanel.setToolTipText("");
		
	    // Add the required panels, configure them, and set their sizes
	    rightPanel.setLayout(new BorderLayout());    
	    loadPanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(loadPanel);
	    connectPanel.setPreferredSize(new Dimension(300,90));
	    leftPanel.add(connectPanel);	    
	    commandPanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(commandPanel);
	    rightPanel.add(mapPanel, BorderLayout.CENTER);
	    leftPanel.add(controlPanel);
	    leftPanel.add(readingsPanel);
	    readingsPanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(lastMovePanel);
	    lastMovePanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(particlePanel);
	    particlePanel.setPreferredSize(new Dimension(300,70));
	    rightPanel.add(statusPanel, BorderLayout.SOUTH);
	    leftPanel.setPreferredSize(new Dimension(320,650));
	    add(leftPanel, BorderLayout.WEST);
	    add(rightPanel, BorderLayout.CENTER);
	    controlPanel.setPreferredSize(new Dimension(300,80));
	    zoomSlider.setValue(INITIAL_ZOOM);
  	}
  
  	/**
  	 * Called when the mouse is clicked in the map area
  	 */
  	@Override
	protected void popupMenuItems(Point p, JPopupMenu menu) { 
	    // Get details of the particle closest to the mouse click
	    menu.add(new MenuAction(NavEvent.FIND_CLOSEST, "Particle Readings", p, model, this));
	}
	
	/**
	 * Called whenever an event is received from the NXT
	 */
	@Override
	public void eventReceived(NavEvent navEvent) {
		// Enable the Get Pose button when the estimated pose has been sent
		if (navEvent == NavEvent.ESTIMATED_POSE) {
			getPoseButton.setEnabled(true);
		} else if (navEvent == NavEvent.LOAD_MAP) {
			// Generate the particles
			model.generateParticles();			
		}
	}
	
	/**
	 * Called when the connection is established
	 */
	@Override
	public void whenConnected() {
		super.whenConnected();
		// If the map has been loaded, send it to the NXT
		if (model.getMap() != null) {
			model.sendMap();
			// Generate the particles
			model.generateParticles();
		}
		// Enable buttons
		getPoseButton.setEnabled(true);
		randomButton.setEnabled(true);
	}
	
	/**
	 * Run the sample 
	 */
	public void run() throws Exception {
		// Set the NXT program to MCLTest
		program = "../samples/MCLTest.nxj";
		
		// Set debugging on the model
		model.setDebug(true);
		// Associate the MCLPoseProvider with the model
		model.setMCL(mcl);
		
		// Open the MCLTest navigation panel in a JFrame window
	    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, TITLE, Color.WHITE, menuBar);
	}
}
