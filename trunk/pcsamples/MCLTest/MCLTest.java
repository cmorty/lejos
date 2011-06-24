import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lejos.pc.remote.*;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.WayPoint;

public class MCLTest extends NavigationPanel {
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 1000;
  private static final int FRAME_HEIGHT = 800;
  
  private static final int NUM_PARTICLES = 300;
  private static MCLPoseProvider mcl;
  
  private JButton randomButton = new JButton("Random move");
  private JButton getPoseButton = new JButton("Get Pose");
  
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new MCLTest()).run();
  }
  
  public MCLTest() {
		commandPanel.add(randomButton);
		commandPanel.add(getPoseButton);
		
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.randomMove();
			}
		});
		
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.getPose();
			}
		});
  }
  
  public void run() throws Exception {   
	model.setPanel(this);
	model.loadMap("Room.svg");
	mcl = new MCLPoseProvider(null,null, model.getMap(),NUM_PARTICLES,0);
	model.setParticleSet(mcl.getParticles());
	
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);;
  }
 
}