import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lejos.robotics.navigation.*;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.mapping.NavigationPanel;

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
 * If obstacles are detected the robot stops and the onstacles are displayed on the map.
 * 
 * A future version will let you add waypoints so that you can define the path that the robot takes to the
 * target.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MapTest extends NavigationPanel {
	private static final long serialVersionUID = 1L;

	// GUI Window size
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 800;
  
	private static final int GRID_SPACE = 39;
	private static final int CLEARANCE = 10;
	
	private static final int INITIAL_ZOOM = 120;
	private static final Dimension MAP_AREA_SIZE = new Dimension(800,600);
	private static final String MAP_FILE = "floor.svg";
	private static final Pose INITIAL_ROBOT_POSE = new Pose(450,90,180);
	private static final String FRAME_TITLE = "Map Test";
  
	
	private JLabel setHeadingLabel = new JLabel("Set Heading:");
	private JSlider setHeadingSlider = new JSlider(0,360);
	private JButton setHeadingButton = new JButton("Set");
	
	private JLabel rotateLabel = new JLabel("Rotate To:");
	private JSlider rotateSlider = new JSlider(0,360);
	private JButton rotateButton = new JButton("Go");
  
	/**
	 * Create a MapTest object and display it in a GUI frame.
	 * Then connect to the NXT.
	 */
	public static void main(String[] args) throws Exception {
		(new MapTest()).run();
	}
  
	public MapTest() {
		buildGUI();
	}
  
	@Override
	protected void buildGUI() {
	    showReadingsPanel = false;
	    showLastMovePanel = false;
	    showParticlePanel = false;
	    showMoves = true;    
	    showMesh = false;
	    
	    mapSize = MAP_AREA_SIZE;
	    super.buildGUI();
	    
	    slider.setValue(INITIAL_ZOOM);

		commandPanel.add(setHeadingLabel);
		commandPanel.add(setHeadingSlider);
		commandPanel.add(setHeadingButton);
		
		setHeadingSlider.setValue((int) INITIAL_ROBOT_POSE.getHeading());
		setHeadingSlider.setMajorTickSpacing(90);
		setHeadingSlider.setPaintTicks(true);
		setHeadingSlider.setPaintLabels(true);
		
		setHeadingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Pose current = model.getRobotPose();
				model.setPose(new Pose(current.getX(), current.getY(), setHeadingSlider.getValue()));
			}
		});
				
		commandPanel.add(rotateLabel);
		commandPanel.add(rotateSlider);
		commandPanel.add(rotateButton);
		
		rotateSlider.setValue(0);
		rotateSlider.setMajorTickSpacing(90);
		rotateSlider.setPaintTicks(true);
		rotateSlider.setPaintLabels(true);
		
		rotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.rotate(rotateSlider.getValue());
			}
		});
	}
	
	@Override
	protected void eventReceived(NavEvent navEvent) {
		if (navEvent == NavEvent.SET_POSE) {
			int heading = (int) model.getRobotPose().getHeading();
			if (heading < 0) heading += 360;
			rotateSlider.setValue(heading);
			setHeadingSlider.setValue(heading);
		}
	}
	
	@Override
	public void whenConnected() {
		model.setPose(model.getRobotPose());
	}
  
	@Override
	protected void popupMenu(MouseEvent me) {
	    Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), this);
	    
	    JPopupMenu menu = new JPopupMenu(); 
	    menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To",me.getPoint(),model, this));
	    menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Place robot",me.getPoint(),model, this));
	
	    menu.show(this, pt.x, pt.y);
	}
  
	public void run() throws Exception {
		model.loadMap(MAP_FILE);
		model.setMeshParams(GRID_SPACE, CLEARANCE);
		model.setPose(INITIAL_ROBOT_POSE);
	
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, Color.white);
	}
}