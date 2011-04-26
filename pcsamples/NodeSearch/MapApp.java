import javax.swing.*;
import java.awt.*;   
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.navigation.Pose;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.*;

public class MapApp extends JPanel implements WindowListener, ActionListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	private final int NODE_CIRC = 6; // Size of node circle to draw (diameter in pixels)
	private final String BUTTON_TEXT = "Redraw Grid";
	private final String CALC_TEXT = "Calculate Path";
	
	private Node startNode = null;
	private Node goalNode = null;
	
	private LineMap map = null;
	private FourWayGridMesh mesh = null;
	private PathFinder pf = null;
	
	private Collection <Node> nodeSet = null;
	private Collection <WayPoint> path = null;
	private TextField txtInterval = null;
	private TextField txtClearance = null;
	
	private Button butRefresh = null;
	private Button butCalculate = new Button(CALC_TEXT);
	private JRadioButton startButton = new JRadioButton("Start"  , true);
	private JRadioButton goalButton = new JRadioButton("Goal"   , false);
	private ButtonGroup bgroup = new ButtonGroup();
		
	public MapApp(LineMap map, FourWayGridMesh mesh, PathFinder pf){
		this((int)map.getBoundingRect().width + 100, (int)map.getBoundingRect().height + 200, "Node Map");
		this.map = map;
		this.mesh = mesh;
		this.pf = pf;
		
		Collection <Node> coll = mesh.getMesh();
		this.setNodeSet(coll);
		
	}
	
	public MapApp(int width, int height, String caption) {
		JFrame f = new JFrame(caption);
		f.addWindowListener(this);
		this.addMouseListener(this);
		f.setSize(width, height);
				
		txtInterval = new TextField("39");
		txtClearance = new TextField("10");
		butRefresh = new Button(BUTTON_TEXT);
		butRefresh.addActionListener(this);
		butCalculate.addActionListener(this);
		startButton.setBackground(Color.GREEN);
		goalButton.setBackground(Color.RED);
		bgroup.add(startButton);
		bgroup.add(goalButton);
		
		setLayout(new BorderLayout());
		
		Panel controls1 = new Panel();
		controls1.setLayout(new GridBagLayout());
		controls1.add(butCalculate);
		controls1.add(startButton);
	    controls1.add(goalButton);
		
		Panel controls2 = new Panel();
		controls2.setLayout(new GridBagLayout());
		controls2.add(butRefresh);
	    controls2.add(new JLabel("spacing"));
	    controls2.add(txtInterval);
	    controls2.add(new JLabel("clearance"));
	    controls2.add(txtClearance);
	    
	    Panel combined = new Panel();
	    combined.setLayout(new GridLayout(2,1));
	    combined.add(controls1);
	    combined.add(controls2);
	    add(combined, BorderLayout.SOUTH);
	    
		f.add(this);
		f.setVisible(true);
	}
	
	// TODO: This method is redundant. This class could just pull getMesh directly from mesh object during paintComponent() method.
	public void setNodeSet(Collection <Node> nodeSet) {
		this.nodeSet = nodeSet;
	}
	
	public void setPath(Collection <WayPoint> path) {
		this.path = path;
	}
	
	public void setStart(Node start){
		if(this.startNode != null) mesh.removeNode(this.startNode);
		this.startNode = start;
		mesh.addNode(start, 4);
	}
	
	public void setGoal(Node goal){
		if(this.goalNode != null) mesh.removeNode(this.goalNode);
		this.goalNode = goal;
		mesh.addNode(goal, 4);
	}
	
	public void paintComponent(Graphics g) {
		long startNanoT = System.nanoTime();
		
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw map bounds and map objects
		if(map != null) {
			g2d.setColor(Color.BLACK);
			Rectangle r = map.getBoundingRect();
			g2d.draw(r);
			
			g2d.setColor(Color.BLACK);
			Line [] l = map.getLines();
			for(int i=0;i<l.length;i++) {
				g2d.draw(l[i]);
			}
		}
		
		// Draw node set
		if(nodeSet != null) {
			Iterator <Node> nodeIterator = nodeSet.iterator();
			while(nodeIterator.hasNext()) {
				Node cur = nodeIterator.next();
				g2d.setColor(Color.ORANGE);
				Ellipse2D.Double circle = new Ellipse2D.Double(cur.x-NODE_CIRC/2, cur.y-NODE_CIRC/2, NODE_CIRC, NODE_CIRC);
				g2d.fill(circle);
				
				// TODO: This code will draw lines to every node neighbor and *repeat* connections but I don't care.
				Collection <Node> coll = cur.getNeighbors();
				Iterator <Node> iter = coll.iterator();
				while(iter.hasNext()) {
					Node neighbor = iter.next();
					g2d.setColor(Color.YELLOW);
					Line line = new Line(cur.x, cur.y, neighbor.x, neighbor.y);
					g2d.draw(line);
				}
			}
		}
		
		// Draw path
		if(this.path != null) {
			Iterator <WayPoint> path_iter = path.iterator();
			WayPoint curWP = path_iter.next();
			g2d.setColor(Color.BLUE);
			while(path_iter.hasNext()) {
				WayPoint nextWP = path_iter.next();
				Line line = new Line(curWP.x, curWP.y, nextWP.x, nextWP.y);
				g2d.draw(line);
				curWP = nextWP;
			}	
		}
		
		if(startNode != null) {
			Ellipse2D.Double startCirc = new Ellipse2D.Double(startNode.x-NODE_CIRC/2, startNode.y-NODE_CIRC/2, NODE_CIRC, NODE_CIRC);
			g2d.setColor(Color.GREEN);
			g2d.fill(startCirc);
		}
		
		if(goalNode != null) {
			g2d.setColor(Color.RED);
			Ellipse2D.Double goalCirc = new Ellipse2D.Double(goalNode.x-NODE_CIRC/2, goalNode.y-NODE_CIRC/2, NODE_CIRC, NODE_CIRC);
			g2d.fill(goalCirc);
		}
				
		long totalNanoT = System.nanoTime() - startNanoT;
		System.out.println("paintComponent() Ran in " + (totalNanoT/1000000D) + " ms"); // TODO Delete this
	}
	
	protected void clear(Graphics g) {
		super.paintComponent(g);
	}
	
	public static void main(String[] args) throws DestinationUnreachableException {
		Line [] lines = new Line[3];
		lines [0] = new Line(50, 125, 275, 125);
		lines [1] = new Line(350, 250, 125, 250);
		lines [2] = new Line(235, 165, 195, 210);
		lejos.geom.Rectangle bounds = new Rectangle(50, 60, 300, 300);
		LineMap myMap = new LineMap(lines, bounds);
		
		FourWayGridMesh grid = new FourWayGridMesh(myMap, 39, 10);
		AstarSearchAlgorithm alg = new AstarSearchAlgorithm();
		NodePathFinder pf = new NodePathFinder(alg, grid);
		
		MapApp mt = new MapApp(myMap, grid, pf);
	}
	
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}

	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals(CALC_TEXT)) {
			mesh.regenerate(); // TODO: Without this here it crashes with null pointer for some reason. Don't want this here!
			Pose startPose = new Pose(startNode.x, startNode.y, 0); // Todo: Start is always a Pose?
			WayPoint goalWP = new WayPoint(goalNode.x, goalNode.y);
			
			try {
				Collection <WayPoint> coll = pf.findRoute(startPose, goalWP);
				System.out.println("PATH OUTPUT: ");
				Iterator <WayPoint> iter = coll.iterator();
				while(iter.hasNext()) {
					System.out.println(iter.next().toString());
				}
				this.setPath(coll);
			} catch (DestinationUnreachableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.repaint();
		}
		
		if(action.getActionCommand().equals(BUTTON_TEXT)) {
			// Read value from txtInterval and txtClearance
			// recalculate grid based on new value
			int spacing = Integer.parseInt(txtInterval.getText());
			mesh.setGridSpacing(spacing);
			int clearance = Integer.parseInt(txtClearance.getText());
			mesh.setClearance(clearance);
						
			mesh.regenerate();
			this.setNodeSet(mesh.getMesh());
			this.repaint();
		}	
	}

	public void mouseClicked(MouseEvent me) {
		System.out.println("MOUSE " + me.getX() + ", " + me.getY());
		if(startButton.isSelected()) {
			// TODO Probably check if in bounding rect first
			setStart(new Node(me.getX(), me.getY()));
			goalButton.setSelected(true);
		} else if(goalButton.isSelected()) {
			// TODO Probably check if in bounding rect first
			setGoal(new Node(me.getX(), me.getY()));
			startButton.setSelected(true);
		}
		
		this.repaint();
	}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
} 