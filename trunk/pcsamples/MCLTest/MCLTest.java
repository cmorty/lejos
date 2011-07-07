import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;
import lejos.pc.remote.*;
import lejos.robotics.*;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.localization.*;

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
public class MCLTest extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	// GUI Window size
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 800;
	private static final int NUM_PARTICLES = 200;

	private static final JButton randomButton = new JButton("Random move");
	private static final JButton getPoseButton = new JButton("Get Pose");
	private static final String mapFileName = "Room.svg";
	
	private static MCLPoseProvider mcl;
  
  	/**
   	* Create a MapTest object and display it in a GUI frame.
   	* Then connect to the NXT.
   	*/
  	public static void main(String[] args) throws Exception {
  		(new MCLTest()).run();
  	}
  
  	public MCLTest() {
  		buildGUI();
  	}
  
  	/**
  	 * Build the GUI
  	 */
  	@Override
  	protected void buildGUI() {
  		// All panels required
	    super.buildGUI();
	    
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
  	}
  
  	/**
  	 * Called when the mouse is clicked in the map area
  	 */
  	@Override
	protected void popupMenu(MouseEvent me) {
  		// Calculate the screen point and create the pop-up the context menu
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    JPopupMenu menu = new JPopupMenu(); 
	    
	    // Get details of the particle closest to the mouse click
	    menu.add(new MenuAction(NavEvent.FIND_CLOSEST, "Find Closest", me.getPoint(), model, this));
	    
	    // Show the context menu 
	    menu.show(this, pt.x, pt.y);
	}
	
	/**
	 * Called whenever an event is received from the NXT
	 */
	@Override
	protected void eventReceived(NavEvent navEvent) {
		// Enable the Get Pose button when the estimated pose has been sent
		if (navEvent == NavEvent.ESTIMATED_POSE) {
			getPoseButton.setEnabled(true);
		}
	}
	
	/**
	 * Called when the connection is established
	 */
	@Override
	protected void whenConnected() {
		// Load the map and generate the particles and sends both to the NXT
		model.loadMap(mapFileName);
		model.generateParticles();
		
		// Enable buttons
		getPoseButton.setEnabled(true);
		randomButton.setEnabled(true);
	}
	
	/**
	 * Run the sample 
	 */
	public void run() throws Exception {
		// Create a stub version of the MCLPoseProvider
		mcl = new MCLPoseProvider(null,NUM_PARTICLES,0);
		
		// Associate the MCLPoseProvider with the model
		model.setMCL(mcl);
		
		// Open the MCLTest navigation panel in a JFrame window
	    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "MCL Test", Color.white);
	}
}