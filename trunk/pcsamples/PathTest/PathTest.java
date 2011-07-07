import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationPanel;

/**
 * Used with the MapTest sample running on the PC.
 * 
 * This sample displays a map of a room and uses a 4-way grid mesh to plan a route
 * from a selected start point to a selected destination.
 * 
 * The robot can then be requested to follow the route.
 * 
 * @author Lawrie Griffiths
 *
 */
public class PathTest extends NavigationPanel {
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 1000;
  private static final int FRAME_HEIGHT = 800;
  
  private static int GRID_SPACE = 39;
  private static int CLEARANCE = 10;
  
  private JButton calculateButton = new JButton("Calculate path");
  private JButton followButton = new JButton("Follow Route");;
  
  /**
   * Create a PathTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new PathTest()).run();
  }
  
  public PathTest() {
	  buildGUI();
  }
  
  protected void buildGUI() {
	    showReadingsPanel = false;
	    showLastMovePanel = false;
	    showParticlePanel = false;
	    super.buildGUI();
		commandPanel.add(calculateButton);;
		commandPanel.add(followButton);
		
		meshCheck.setSelected(true);
		
		calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.calculatePath();
			}
		});
		
		followButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.followRoute();
			}
		});
  }
  
	protected void popupMenu(MouseEvent me) {
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    
	    JPopupMenu menu = new JPopupMenu(); 
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Set pose",me.getPoint(),model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target",me.getPoint(),model, this));
	
	    menu.show(this, pt.x, pt.y);
	}
  
  public void run() throws Exception {
	model.loadMap("Room.svg");
	model.setMeshParams(GRID_SPACE, CLEARANCE);
	
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Path Test", Color.white);
  }
 
}