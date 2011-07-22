import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;

import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationPanel;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.navigation.Pose;

/**
 * NavTest shows a mapped area and allow navigation commands to be sent to the NXT.
 * 
 * The NXT must run the MapTest sample.
 * 
 * @author Lawrie Griffiths
 */
public class NavTest extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 950;
	private static final int FRAME_HEIGHT = 700;	
	private static final int INITIAL_ZOOM = 100;
	private static final Point INITIAL_VIEW_START = new Point(-80,-10);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,550);
	private static final String MAP_FILE = "floor.svg";
	private static final Pose INITIAL_ROBOT_POSE = new Pose(450,430,180);
	private static final String FRAME_TITLE = "Nav Test";
  
	/**
	 * Create a MapTest object and display it in a GUI frame.
	 * Then connect to the NXT.
	 */
	public static void main(String[] args) {
		(new NavTest()).run();
	}
  
	public NavTest() {
		buildGUI();
	}
  
	@Override
	protected void buildGUI() {
		title = "Nav Test";
		description = "NavTest demonstrates sending navigation events to the PC";
		
	    showMoves = true;    
	    showMesh = false;
	    showZoomLabels = true;
	    
	    mapPaneSize = MAP_AREA_SIZE;
	    initialViewStart = INITIAL_VIEW_START;

	    add(eventPanel);
	    createMapPanel();
	    createControlPanel();
	    createStatusPanel();

	    zoomSlider.setOrientation(JSlider.VERTICAL);
	    zoomSlider.setValue(INITIAL_ZOOM);
		createMenu();
	}
	
	@Override
	protected void eventReceived(NavEvent navEvent) {
	}
	
	/**
	 * Send the pose when connected
	 */
	@Override
	public void whenConnected() {
		model.setPose(model.getRobotPose());
	}
  
	@Override
	protected void popupMenuItems(Point p, JPopupMenu menu) {
	    menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Place robot", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.ADD_WAYPOINT, "Add Waypoint", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target", p, model, this));
	}
  
	public void run(){
		model.setDebug(true);
		model.loadMap(MAP_FILE);
		model.setPose(INITIAL_ROBOT_POSE);
	
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, SystemColor.controlShadow, menuBar);
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
	}
}