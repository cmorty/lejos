import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lejos.robotics.navigation.*;
import lejos.robotics.mapping.*;
import lejos.robotics.mapping.NavigationModel.NavEvent;

/**
 * Use with the MapTest sample running on the NXT.
 * 
 * This PC sample uses extends Navigation panel to implement a GUI that controls a robot in a mapped area.
 * 
 * The map should be in SVG format. Only <line> elements are processed. Tools such as svg-edit can produce 
 * suitable SVG files.
 * 
 * This sample lets you set the pose of a robot in a mapped area, and then goto a target position.
 * You can also rotate the robot to a desired heading.
 * 
 * If obstacles are detected the robot stops and the obstacles are displayed on the map.
 * 
 * A future version will let you add waypoints so that you can define the path that the robot takes to the
 * target.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MapTest extends NavigationPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 950;
	private static final int FRAME_HEIGHT = 800;	
	private static final int INITIAL_ZOOM = 100;
	private static final Point INITIAL_VIEW_START = new Point(-80,-10);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,550);
	private static final String MAP_FILE = "floor.svg";
	private static final Pose INITIAL_ROBOT_POSE = new Pose(450,430,180);
	private static final String FRAME_TITLE = "Map Test";
	
	private PosePanel setHeading, rotate;
  
	/**
	 * Create a MapTest object and display it in a GUI frame.
	 * Then connect to the NXT.
	 */
	public static void main(String[] args) {
		(new MapTest()).run();
	}
  
	public MapTest() {
		buildGUI();
	}
  
	@Override
	protected void buildGUI() {
		title = "Map Test";
		description = "MapTest demonstrates the leJOS NavigationPanel API\n\n" +
	    			  "It allows you to move a robot around a mapped area,\n" +
	                  "including plotting a course to a target position by adding waypoints.";
		
	    showMoves = true;    
	    showMesh = false;
	    showZoomLabels = true;
	    
	    mapPaneSize = MAP_AREA_SIZE;
	    initialViewStart = INITIAL_VIEW_START;
	    
	    //createConnectPanel();

	    createStatusPanel();
	    createCommandPanel();
	    createMapPanel();
	    createControlPanel();
	    
	    zoomSlider.setOrientation(JSlider.VERTICAL);
	    zoomSlider.setValue(INITIAL_ZOOM);

	    setHeading = new PosePanel(model, NavEvent.SET_POSE,"Set Heading:", "Set");
	    commandPanel.add(setHeading);
	    rotate = new PosePanel(model, NavEvent.ROTATE_TO, "Rotate To:", "Go");
	    commandPanel.add(rotate);

		createMenu();
	}
	
	@Override
	protected void eventReceived(NavEvent navEvent) {
		if (navEvent == NavEvent.SET_POSE) {
			int heading = (int) model.getRobotPose().getHeading();
			if (heading < 0) heading += 360;
			rotate.setValue(heading);
			setHeading.setValue(heading);
		}
	}
	
	/**
	 * Send the pose when connected
	 */
	@Override
	public void whenConnected() {
		model.setPose(model.getRobotPose());
	}
  
	@Override
	protected void popupMenuItems(Point p, JPopupMenu menu) {; 
	    menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Place robot", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.ADD_WAYPOINT, "Add Waypoint", p, model, this));
	}
  
	public void run(){
		model.setDebug(true);
		model.loadMap(MAP_FILE);
		model.setPose(INITIAL_ROBOT_POSE);
	
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, SystemColor.controlShadow, menuBar);
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
	}
}