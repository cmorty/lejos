import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationPanel;
import lejos.robotics.mapping.PosePanel;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.navigation.Pose;

/**
 * MapCommand shows a mapped area and allow navigation commands to be sent to the NXT.
 * 
 * The NXT must run the MapTest sample.
 * 
 * @author Lawrie Griffiths
 */
public class MapCommand extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1200;
	private static final int FRAME_HEIGHT = 700;	
	private static final int INITIAL_ZOOM = 100;
	private static final Point INITIAL_VIEW_START = new Point(0,0);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,550);
	private static final String FRAME_TITLE = "Map Command";
	
	private PosePanel setHeading, rotate;
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
  
	/**
	 * Create a MapTest object and display it in a GUI frame.
	 * Then connect to the NXT.
	 */
	public static void main(String[] args) {
		(new MapCommand()).run();
	}
  
	public MapCommand() {
		buildGUI();
	}
  
	@Override
	protected void buildGUI() {
		setLayout(new BorderLayout());
		title = "Map Command";
		description = "MapCommand allows remote control of robots from the PC\n" +
		              "from a GUI application that displays a map of the area \n" +
		              "that the robot is moving in.\n\n" +
		              "It displays many types of navigation data such as paths \n" +
		              "calculate, paths followed, features detected etc.";
		
	    showMoves = true;    
	    showMesh = false;
	    showZoomLabels = true;
	    
	    mapPaneSize = MAP_AREA_SIZE;
	    initialViewStart = INITIAL_VIEW_START;
    
	    rightPanel.setLayout(new BorderLayout());
	    rightPanel.add(eventPanel, BorderLayout.NORTH);
	    createConnectPanel();
	    leftPanel.add(connectPanel);
	    createCommandPanel();
	    setHeading = new PosePanel(model, NavEvent.SET_POSE,"Set Heading:", "Set");
	    commandPanel.add(setHeading);
	    rotate = new PosePanel(model, NavEvent.ROTATE_TO, "Rotate To:", "Go");
	    commandPanel.add(rotate);
	    commandPanel.setPreferredSize(new Dimension(400,150));
	    leftPanel.add(commandPanel);
	    createMapPanel();
	    rightPanel.add(mapPanel, BorderLayout.CENTER);
	    createControlPanel();
	    leftPanel.add(controlPanel);
	    createStatusPanel();
	    rightPanel.add(statusPanel, BorderLayout.SOUTH);
	    leftPanel.setPreferredSize(new Dimension(400,600));
	    add(leftPanel, BorderLayout.WEST);
	    add(rightPanel, BorderLayout.CENTER);

	    //zoomSlider.setOrientation(JSlider.VERTICAL);
	    zoomSlider.setValue(INITIAL_ZOOM);
		createMenu();
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
		//model.loadMap(MAP_FILE);
		//model.setPose(INITIAL_ROBOT_POSE);
	
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, SystemColor.controlShadow, menuBar);
	}
}
