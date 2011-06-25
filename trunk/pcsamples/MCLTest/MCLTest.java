import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import lejos.pc.remote.*;
import lejos.robotics.localization.MCLPoseProvider;

public class MCLTest extends NavigationPanel {
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 1000;
  private static final int FRAME_HEIGHT = 800;
  private static final int NUM_PARTICLES = 200;

  private static MCLPoseProvider mcl;
  private JButton randomButton = new JButton("Random move");
  private JButton getPoseButton = new JButton("Get Pose");
  private JButton generateButton = new JButton("Generate Particles");
  private JButton loadMapButton = new JButton("Load Map");
  private JButton getParticlesButton = new JButton("Get Particles");
  private String mapFileName = "Room.svg";
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new MCLTest()).run();
  }
  
  public MCLTest() {
		commandPanel.add(loadMapButton);
		commandPanel.add(generateButton);
		commandPanel.add(randomButton);
		commandPanel.add(getPoseButton);
		commandPanel.add(getParticlesButton);
	
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else model.randomMove();
			}
		});
		
		getPoseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else model.getPose();
			}
		});
		
		getParticlesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!model.isConnected()) error("Not Connected");
				else model.getParticles();
			}
		});
		
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.generateParticles();
			}
		});
		
		loadMapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.loadMap(mapFileName);
			}
		});
  }
  
  public void run() throws Exception {   
	model.setPanel(this);
	mcl = new MCLPoseProvider(null,NUM_PARTICLES,0);
	model.setParticleSet(mcl.getParticles());
	model.setMCL(mcl);
	
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);;
  }

}