import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import lejos.pc.remote.*;
import lejos.robotics.NavigationModel;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.MCLPoseProvider;

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
 * particle set cluster ariunds a few possible poses, and eventually find the correct pose.
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

  private static MCLPoseProvider mcl;
  private JButton randomButton = new JButton("Random move");
  private JButton getPoseButton = new JButton("Get Pose");
  private JButton loadMapButton = new JButton("Load Map");
  private String mapFileName = "Room.svg";
  private JLabel readingsLabel = new JLabel("readings");
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new MCLTest()).run();
  }
  
  public MCLTest() {
		commandPanel.add(loadMapButton);
		commandPanel.add(randomButton);
		commandPanel.add(getPoseButton);
	
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else {
					model.randomMove();
					model.getRemoteParticles();
				}
			}
		});
		
		getPoseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else {
					model.getPose();
					model.getRemoteParticles();
					model.getRemoteReadings();
					model.getEstimatedPose();
					System.out.println("Max weight:" + model.getParticles().getMaxWeight());
					RangeReadings readings = model.getReadings();
				
					String s = "";
					for(int i=0;i<readings.getNumReadings();i++) {
						s += readings.getRange(i) + " ";
						System.out.println(readings.getAngle(i)+ ":" + readings.getRange(i));
					}
					readingsLabel.setText(s);
				}
			}
		});
		
		loadMapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else {
					model.loadMap(mapFileName);
					model.generateParticles();
				}
			}
		});
		
		statusPanel.add(readingsLabel);
  }
  
	protected void popupMenu(MouseEvent me) {
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    JPopupMenu menu = new JPopupMenu(); 
	    menu.add(new MenuAction(NavigationModel.NavEvent.FIND_CLOSEST, "Find Closest", me.getPoint(), model, this));
	    menu.show(this, pt.x, pt.y);
	}
	
  
  public void run() throws Exception {
	// Create a stub version of the MCLPoseProvider
	mcl = new MCLPoseProvider(null,NUM_PARTICLES,0);
	// Associate the MCLPoseProvider with the model
	model.setMCL(mcl);
	
	// Open the MCLTest navigation panel in a JFrame window
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);;
  }

}