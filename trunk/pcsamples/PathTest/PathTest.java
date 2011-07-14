import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import lejos.robotics.mapping.MapPanel;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationModel.NavEvent;
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
  	private static final int FRAME_WIDTH = 1200;
  	private static final int FRAME_HEIGHT = 800;
  
  	private static final Dimension MAP_SIZE = new Dimension(700,600);
  	private static final int INITIAL_ZOOM = 110;
  	private static final String MAP_FILE = "floor.svg";
  	private static final String FRAME_TITLE = "Path Test";
  
  	private static int GRID_SPACE = 39;
  	private static int CLEARANCE = 10;
  
  	private JButton calculateButton = new JButton("Calculate path");
  	private JButton followButton = new JButton("Follow Route");
  	
  	private JPanel logPanel = new JPanel();
  	private JTextArea logArea = new JTextArea(35,23);
  	private JScrollPane log = new JScrollPane(logArea); 
  	private JButton clearButton = new JButton("Clear log");
  
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
  
  	/**
  	 * Build the GUI
  	 */
  	@Override
  	protected void buildGUI() {
  		// Set the map size and suppress unwanted panels
  		mapPaneSize = MAP_SIZE;
  		showReadingsPanel = false;
	    showLastMovePanel = false;
	    showParticlePanel = false;
	    
	    showMoves = true;
	    
	    super.buildGUI();
	    
	    slider.setValue(INITIAL_ZOOM);
	    
  		// Set the map color
  		mapPanel.colors[MapPanel.MAP_COLOR_INDEX] = Color.DARK_GRAY;
	    
	    // Add calculate and follow buttons
		commandPanel.add(calculateButton);;
		commandPanel.add(followButton);
		
		// Display mesh
		meshCheck.setSelected(true);
		
		calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.calculatePath();
				followButton.setEnabled(true);
			}
		});
		
		followButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.followRoute();
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				logArea.setText("");
			}
		});
		
		followButton.setEnabled(false);
		
		log.setBorder(BorderFactory.createTitledBorder("Log"));
		logPanel.add(log);
		logPanel.add(clearButton);
		logPanel.setPreferredSize(new Dimension(280,650));
		add(logPanel);
  	}
  
  	/**
  	 * Pop-up context menu
  	 */
  	@Override
	protected void popupMenu(MouseEvent me) {
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    JPopupMenu menu = new JPopupMenu();
	    
	    boolean inside = model.getMap().inside(new lejos.geom.Point((me.getX() / pixelsPerUnit + mapPanel.viewStart.x) , (mapPanel.getHeight() - me.getY())/ pixelsPerUnit + mapPanel.viewStart.y));  
	    if (!inside) return;
	    
	    // Include set pose and set target menu items
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Set pose",me.getPoint(),model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target",me.getPoint(),model, this));
	
	    menu.show(this, pt.x, pt.y);
	}
  	
  	@Override
  	protected void eventReceived(NavEvent navEvent) {
  		if (navEvent == NavEvent.PATH_COMPLETE) {
  			// Add the path to the mesh again, when the path has been complete
  			model.setPose(model.getRobotPose());
  		}
  	}

  	@Override
  	protected void whenConnected() {
  		connectButton.setEnabled(false);
  		connectPanel.setBorder(BorderFactory.createTitledBorder("Connected"));
  		model.setPose(model.getRobotPose());
  	}
  	
  	@Override
  	public void log(String message) {
  		logArea.append(message + "\n");
  	}
  
	/**
	 * Run the sample
	 */
	public void run() throws Exception {
		model.setDebug(true);
		model.loadMap(MAP_FILE);
		model.setMeshParams(GRID_SPACE, CLEARANCE);
	
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, Color.white);
	}
}