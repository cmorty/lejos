package lejos.pc.tools;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import lejos.pc.comm.SystemContext;
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
public class NXJMapCommand extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1150;
	private static final int FRAME_HEIGHT = 700;	
	private static final int INITIAL_ZOOM = 100;
	private static final Point INITIAL_MAP_ORIGIN = new Point(-10,-10);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,550);
	private static final String FRAME_TITLE = "NXJ Map Command";
	
	private SliderPanel setHeading, rotate, travelSpeed, rotateSpeed;
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
  
	/**
	 * Create a MapTest object and display it in a GUI frame.
	 * Then connect to the NXT.
	 */
	public static void main(String[] args) {
		ToolStarter.startSwingTool(NXJMapCommand.class, args);
	}
	
	public static int start(String[] args)
	{
		return new NXJMapCommand().run();
	}
  
	public NXJMapCommand() {
		setTitle(FRAME_TITLE);
		setDescription("MapCommand allows remote control of robots from the PC\n" +
		               "from a GUI application that displays a map of the area \n" +
		               "that the robot is moving in.\n\n" +
		               "It displays many types of navigation data such as paths \n" +
		               "calculate, paths followed, features detected etc.");
		buildGUI();
	}
  
	/**
	 * Build the specific GUI for this application
	 */
	@Override
	protected void buildGUI() {
		setLayout(new BorderLayout());
		
		// Choose which features to show
	    showMoves = true;    
	    showMesh = false;
	    showZoomLabels = true;
	    
	    buildPanels();
	    
	    // Set the size of the map panel, and the viewport origin 
	    setMapPanelSize(MAP_AREA_SIZE);
	    setMapOrigin(INITIAL_MAP_ORIGIN);
    
	    // Add the required panels, configure them, and set their sizes
	    rightPanel.setLayout(new BorderLayout());
	    rightPanel.add(eventPanel, BorderLayout.NORTH);	    
	    loadPanel.setPreferredSize(new Dimension(300,70));
	    leftPanel.add(loadPanel);
	    connectPanel.setPreferredSize(new Dimension(300,90));
	    leftPanel.add(connectPanel);
	    setHeading = new SliderPanel(model, NavEvent.SET_POSE,"Set Heading:", "Set", 360);
	    setHeading.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(setHeading);
	    rotate = new SliderPanel(model, NavEvent.ROTATE_TO, "Rotate To:", "Go", 360);
	    rotate.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(rotate);
	    travelSpeed = new SliderPanel(model, NavEvent.TRAVEL_SPEED, "Travel Speed", "Set", Integer.parseInt(props.getProperty(KEY_MAX_TRAVEL_SPEED, "60")));
	    travelSpeed.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(travelSpeed);
	    rotateSpeed = new SliderPanel(model, NavEvent.ROTATE_SPEED, "Rotate Speed", "Set", Integer.parseInt(props.getProperty(KEY_MAX_ROTATE_SPEED, "360")));
	    rotateSpeed.setPreferredSize(new Dimension(280,80));
	    commandPanel.add(rotateSpeed);	    
	    commandPanel.setPreferredSize(new Dimension(300,370));
	    leftPanel.add(commandPanel);
	    rightPanel.add(mapPanel, BorderLayout.CENTER);
	    leftPanel.add(controlPanel);
	    rightPanel.add(statusPanel, BorderLayout.SOUTH);
	    leftPanel.setPreferredSize(new Dimension(320,600));
	    add(leftPanel, BorderLayout.WEST);
	    add(rightPanel, BorderLayout.CENTER);
	    controlPanel.setPreferredSize(new Dimension(300,80));
	    zoomSlider.setValue(INITIAL_ZOOM);
	}
	
	/**
	 * Send the pose when connected
	 */
	@Override
	public void whenConnected() {
		super.whenConnected();
		model.setPose(model.getRobotPose());
	}
	
	/**
	 * Set the sliders when the pose is  changed
	 */
	@Override
	public void eventReceived(NavEvent navEvent) {
		if (navEvent == NavEvent.SET_POSE) {
			int heading = (int) model.getRobotPose().getHeading();
			if (heading < 0) heading += 360;
			rotate.setValue(heading);
			setHeading.setValue(heading);
		}
	}
  
	/**
	 * Add the required context menu items
	 */
	@Override
	protected void popupMenuItems(Point p, JPopupMenu menu) {
	    menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Place robot", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.ADD_WAYPOINT, "Add Waypoint", p, model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target", p, model, this));
	}
  
	public int run() {
		// Set debugging on to get information of events being processed
		model.setDebug(true);
		
		// Use MapTest.nxj from the bin directory as the NXJ program
		String home = SystemContext.getNxjHome();
		File progFile = new File(home, "bin" + File.separator + "MapTest.nxj");
		try {
			program = progFile.getCanonicalPath();
		} catch (IOException e) {
			// leave as is
		}

	
		// Open the panel in a frame
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, SystemColor.controlShadow, menuBar);
		return 0;
	}
}
