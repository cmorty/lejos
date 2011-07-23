import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationPanel;
import lejos.robotics.mapping.SliderPanel;
import lejos.robotics.mapping.NavigationModel.NavEvent;

/**
 * MapCommand shows a mapped area and allow navigation commands to be sent to the NXT.
 * 
 * The NXT must run the MapTest sample.
 * 
 * @author Lawrie Griffiths
 */
public class MapCommand extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1150;
	private static final int FRAME_HEIGHT = 700;	
	private static final int INITIAL_ZOOM = 100;
	private static final Point INITIAL_VIEW_START = new Point(-10,-10);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,550);
	private static final String FRAME_TITLE = "Map Command";
	
	private SliderPanel setHeading, rotate, travelSpeed, rotateSpeed;
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
	    createLoadPanel();
	    loadPanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(loadPanel);
	    createConnectPanel();
	    connectPanel.setPreferredSize(new Dimension(300,90));
	    leftPanel.add(connectPanel);
	    createCommandPanel();
	    setHeading = new SliderPanel(model, NavEvent.SET_POSE,"Set Heading:", "Set", 360);
	    setHeading.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(setHeading);
	    rotate = new SliderPanel(model, NavEvent.ROTATE_TO, "Rotate To:", "Go", 360);
	    rotate.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(rotate);
	    travelSpeed = new SliderPanel(model, NavEvent.TRAVEL_SPEED, "Travel Speed", "Set", 60);
	    travelSpeed.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(travelSpeed);
	    rotateSpeed = new SliderPanel(model, NavEvent.ROTATE_SPEED, "Rotate Speed", "Set", 60);
	    rotateSpeed.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(rotateSpeed);	    
	    commandPanel.setPreferredSize(new Dimension(300,370));
	    leftPanel.add(commandPanel);
	    createMapPanel();
	    rightPanel.add(mapPanel, BorderLayout.CENTER);
	    createControlPanel();
	    leftPanel.add(controlPanel);
	    createStatusPanel();
	    rightPanel.add(statusPanel, BorderLayout.SOUTH);
	    leftPanel.setPreferredSize(new Dimension(320,600));
	    add(leftPanel, BorderLayout.WEST);
	    add(rightPanel, BorderLayout.CENTER);

	    controlPanel.setPreferredSize(new Dimension(300,80));
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
	public void eventReceived(NavEvent navEvent) {
		if (navEvent == NavEvent.SET_POSE) {
			int heading = (int) model.getRobotPose().getHeading();
			if (heading < 0) heading += 360;
			rotate.setValue(heading);
			setHeading.setValue(heading);
		}
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
