package lejos.robotics.mapping;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.JPanel;
import lejos.geom.Line;
import lejos.robotics.localization.*;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.*;

public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected static final Color MAP_COLOR = Color.BLACK;
	protected static final Color PARTICLE_COLOR = Color.RED;
	protected static final Color BACKGROUND_COLOR = Color.WHITE;
	protected static final Color GRID_COLOR = Color.GREEN;
	protected static final Color ESTIMATE_COLOR = Color.BLUE;
	protected static final Color CLOSEST_COLOR = Color.YELLOW;
	protected static final Color MESH_COLOR = Color.ORANGE;
	protected static final Color NEIGHBOR_COLOR = Color.ORANGE;
	protected static final Color TARGET_COLOR = Color.MAGENTA;
	protected static final Color PATH_COLOR = Color.BLUE;
	protected static final Color MOVE_COLOR = Color.PINK;
	protected static final Color FEATURE_COLOR = Color.CYAN;
	protected static final Color WAYPOINT_COLOR = Color.BLUE;
	
	protected static final float ARROW_LENGTH = 10f;
	protected static final int ROBOT_SIZE = 4;
	protected static final int TARGET_SIZE = 5;
	protected final int NODE_CIRC = 6; // Size of node circle to draw (diameter in pixels)
	
	protected PCNavigationModel model;
	protected NavigationPanel parent;
	protected Dimension size;;
	protected float arrowLength;
	protected int gridSize = 10;
	public Color[] colors = {MAP_COLOR, PARTICLE_COLOR, BACKGROUND_COLOR,
			                    GRID_COLOR, ESTIMATE_COLOR, CLOSEST_COLOR,
			                    MESH_COLOR, NEIGHBOR_COLOR, TARGET_COLOR,
			                    PATH_COLOR, MOVE_COLOR, FEATURE_COLOR,
			                    WAYPOINT_COLOR};
	
	public static final int MAP_COLOR_INDEX = 0;
	public static final int PARTICLE_COLOR_INDEX = 1;
	public static final int BACKGROUND_COLOR_INDEX = 2;
	public static final int GRID_COLOR_INDEX = 3;
	public static final int ESTIMATE_COLOR_INDEX = 4;
	public static final int CLOSEST_COLOR_INDEX = 5;
	public static final int MESH_COLOR_INDEX = 6;
	public static final int NEIGHBOR_COLOR_INDEX = 7;
	public static final int TARGET_COLOR_INDEX = 8;
	public static final int PATH_COLOR_INDEX = 9;
	public static final int MOVE_COLOR_INDEX = 10;
	public static final int FEATURE_COLOR_INDEX = 11;
	public static final int WAYPOINT_COLOR_INDEX = 12;
	
	// The maximum size of a cluster of particles for a located robot (in cm)
	protected static final int MAX_CLUSTER_SIZE = 50;
	
	protected Point viewStart = new Point(0,0);
	
	/**
	 * Create the panel, set its size, and associated it with the navigation model
	 * and navigation panel.
	 * 
	 * @param model the navigation model
	 * @param size the map panel size
	 * @param parent the navigation panel
	 */
	public MapPanel(PCNavigationModel model, Dimension size, NavigationPanel parent) {
		this.size = size;
		this.model = model;
		setPreferredSize(size);
		this.parent = parent;
		setBackground(colors[BACKGROUND_COLOR_INDEX]);
	}
	
	/**
	 * Draw the map using Line2D objects
	 * 
	 * @param g2d the Graphics2D object
	 */
	public void paintMap(Graphics2D g2d) {
		LineMap map = model.getMap();
		if (map == null) return;
		Line[] lines = map.getLines();
		g2d.setColor(colors[MAP_COLOR_INDEX]);
		g2d.setStroke(new BasicStroke(2));
		for (int i = 0; i < lines.length; i++) {
			Line2D line = new Line2D.Float(
    		  getX(lines[i].x1), 
    		  getY(lines[i].y1), 
    		  getX(lines[i].x2), 
    		  getY(lines[i].y2));
			g2d.draw(line);
		}
		g2d.setStroke(new BasicStroke(1));
	}
	
	/**
	 * Paint the navigation mesh
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintMesh(Graphics2D g2d) {
		Collection<Node> nodeSet = model.getNodes();
		if(nodeSet != null) {
			Iterator <Node> nodeIterator = nodeSet.iterator();
			while(nodeIterator.hasNext()) {
				Node cur = nodeIterator.next();
				g2d.setColor(colors[MESH_COLOR_INDEX]);
				Ellipse2D.Double circle = new Ellipse2D.Double(getX(cur.x-NODE_CIRC/2), getY(cur.y-NODE_CIRC/2), getDistance(NODE_CIRC), getDistance(NODE_CIRC));
				g2d.fill(circle);
				
				// TODO: This code will draw lines to every node neighbor and *repeat* connections but I don't care.
				Collection <Node> coll = cur.getNeighbors();
				Iterator <Node> iter = coll.iterator();
				while(iter.hasNext()) {
					Node neighbor = iter.next();
					g2d.setColor(colors[NEIGHBOR_COLOR_INDEX]);
					Line line = new Line(getX(cur.x), getY(cur.y), getX(neighbor.x), getY(neighbor.y));
					g2d.draw(line);
				}
			}
		}
	}
	
	/**
	 * Overrides JPanel paintComponent to paint all the navigation data
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintGrid((Graphics2D) g);
		paintMap((Graphics2D) g);
		if (parent.meshCheck.isSelected()) paintMesh((Graphics2D) g);
		paintParticles((Graphics2D) g);
		paintRobot((Graphics2D) g);
		paintTarget((Graphics2D) g);
		paintPath((Graphics2D) g);
		paintMoves((Graphics2D) g);
		paintFeatures((Graphics2D) g);
		paintWaypoints((Graphics2D) g);
	}
	
	/**
	 * If we are down to one small cluster show the
	 * location of the robot.
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintRobot(Graphics2D g2d) {
		MCLPoseProvider mcl = model.getMCL();
		if (mcl == null) {
			g2d.setColor(colors[PARTICLE_COLOR_INDEX]);
			paintPose(g2d, model.getRobotPose());
		} else {
			//parent.log("Checking estimate");
			float minX = mcl.getMinX();
			float maxX = mcl.getMaxX();
			float minY = mcl.getMinY();
			float maxY = mcl.getMaxY();
			Pose estimatedPose = mcl.getEstimatedPose();
			//parent.log("Estimate = " + minX + " , " + maxX + " , " + minY + " , " + maxY);
			float diffX = maxX - minX;
			float diffY = maxY - minY;
			
			//parent.log("DiffX = " + diffX +  ", Diff Y = " + diffY);
			if (diffX > 0 && diffX <= MAX_CLUSTER_SIZE && 
					diffY > 0 && diffY <= MAX_CLUSTER_SIZE) {
				//parent.log("Robot Located");
				Ellipse2D c = new Ellipse2D.Float(
	        					getX(minX), 
	        					getY(minY), 
	        					getDistance(maxX - minX), 
	        					getDistance(maxY - minY));
				g2d.setColor(colors[ESTIMATE_COLOR_INDEX]);
				g2d.draw(c);
				paintPose(g2d,estimatedPose);
			}
		}
	}
	
	/**
	 * Paint the pose using Ellipse2D
	 * 
	 * @param g2d the Graphics2D object
	 */
	public void paintPose(Graphics2D g2d, Pose pose) {
		Ellipse2D c = new Ellipse2D.Float(getX(pose.getX() - ROBOT_SIZE/2), getY(pose.getY() - ROBOT_SIZE/2), getDistance(ROBOT_SIZE), getDistance(ROBOT_SIZE));
		Line rl = getArrowLine(pose);
		Line2D l2d = new Line2D.Float(rl.x1, rl.y1, rl.x2, rl.y2);
		g2d.draw(l2d);
		g2d.fill(c);
	}
	
	/**
	 * Paint the grid
	 * 
	 * @param g2d the Graphics2D object
	 */
	public void paintGrid(Graphics2D g2d) {
		if (gridSize <= 0) return;
		if (!parent.gridCheck.isSelected()) return;
		g2d.setColor(colors[GRID_COLOR_INDEX]);
		for(float i=viewStart.x % getDistance(gridSize); i<this.getHeight(); i+=getDistance(gridSize)) {
			g2d.drawLine(0,(int) i, this.getWidth()-1, (int) i);		
		}
		for(float i=viewStart.y % getDistance(gridSize); i<this.getWidth(); i+=getDistance(gridSize)) {
			g2d.drawLine((int) i,0, (int) i,this.getHeight()-1);		
		}
	}
	
	/**
	 * Paint the particles
	 * @param g2d the Graphics2D object
	 */
	public void paintParticles(Graphics2D g2d) {
		MCLParticleSet particles = model.getParticles();
		if (particles == null) return;
		int numParticles = particles.numParticles();
		g2d.setColor(colors[PARTICLE_COLOR_INDEX]);
		for (int i = 0; i < numParticles; i++) {
			MCLParticle part = particles.getParticle(i);
			if (part != null) {
				if (i == model.closest) g2d.setColor(colors[CLOSEST_COLOR_INDEX]);
				else g2d.setColor(colors[PARTICLE_COLOR_INDEX]);
				paintPose(g2d, new Pose(part.getPose().getX(), part.getPose().getY(), part.getPose().getHeading()));
			}
		}	  
	}
	
	/**
	 * Paint the target
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintTarget(Graphics2D g2d) {
		Waypoint target = model.getTarget();
		if (target == null) return;
		g2d.setColor(colors[TARGET_COLOR_INDEX]);
		Ellipse2D c = new Ellipse2D.Float(getX((float) (target.getX() - TARGET_SIZE/2)), getY((float) ((target.getY() - TARGET_SIZE/2))), getDistance(TARGET_SIZE), getDistance(TARGET_SIZE));
		g2d.fill(c);		
	}
	
	/**
	 * Paint features detected
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintFeatures(Graphics2D g2d) {
		g2d.setColor(colors[FEATURE_COLOR_INDEX]);
		for(lejos.geom.Point pt:model.getFeatures()) {
			Ellipse2D c = new Ellipse2D.Float(getX((float) ((pt.x - TARGET_SIZE/2))), getY((float) ((pt.y - TARGET_SIZE/2))), getDistance(TARGET_SIZE), getDistance(TARGET_SIZE));
			g2d.fill(c);
		}
	}
	
	protected void paintWaypoints(Graphics2D g2d) {
		g2d.setColor(colors[WAYPOINT_COLOR_INDEX]);
		for(lejos.geom.Point pt:model.getWaypoints()) {
			Ellipse2D c = new Ellipse2D.Float(getX((float) ((pt.x - TARGET_SIZE/2))), getY((float) ((pt.y - TARGET_SIZE/2))), getDistance(TARGET_SIZE), getDistance(TARGET_SIZE));
			g2d.fill(c);
		}
	}
	
	/**
	 * Paint the moves made
	 * 
	 * @param g2d the Graphics2D object
	 */
	protected void paintMoves(Graphics2D g2d) {
		if (!parent.showMoves) return;
		ArrayList<Pose> poses = model.getPoses();
		if (poses == null || poses.size() < 2) return;
		Pose previousPose = null;
		
		g2d.setColor(colors[MOVE_COLOR_INDEX]);
		for(Pose pose: poses) {
			if (previousPose == null) previousPose = pose;
			else {
				//parent.log("Drawing line from " + previousPose.getX() + ","  + previousPose.getY() + " to " + pose.getX() + "," + pose.getY());
				g2d.drawLine((int) getX(previousPose.getX()), (int) getY(previousPose.getY()), (int) getX(pose.getX()), (int) getY(pose.getY()));
				previousPose = pose;
			}
		}	
	}
  
	/**
	 * Create a Line that represents the direction of the pose
	 * 
	 * @param pose the pose
	 * @return the arrow line
	 */
	protected Line getArrowLine(Pose pose) {
		return new Line(
				    getX(pose.getX()),
    		        getY(pose.getY()), 
    		        getX(pose.getX()) + getDistance(ARROW_LENGTH * (float) Math.cos(Math.toRadians(pose.getHeading()))), 
    		        getY(pose.getY()) + getDistance(ARROW_LENGTH * (float) Math.sin(Math.toRadians(pose.getHeading()))));
	}
	
	/**
	 * Paint the path
	 * 
	 * @param g2d the Graphics2d object
	 */
	protected void paintPath(Graphics2D g2d) {
		Path path = model.getPath();
		if(path != null) {
			Iterator <Waypoint> path_iter = path.iterator();
			Waypoint curWP = path_iter.next();
			g2d.setColor(colors[PATH_COLOR_INDEX]);
			while(path_iter.hasNext()) {
				Waypoint nextWP = path_iter.next();
				Line line = new Line(getX(curWP.x), getY(curWP.y), getX(nextWP.x), getY(nextWP.y));
				g2d.draw(line);
				curWP = nextWP;
			}	
		}
	}
	
	/**
	 * Get the screen X coordinate for a given map coordinate
	 */
	protected float getX(float x) {
		return ((viewStart.x + x) * parent.pixelsPerUnit);
	}
	
	/**
	 * Get the screen Y coordinate for a given map coordinate
	 */
	protected float getY(float y) {
		return ((viewStart.y + y) * parent.pixelsPerUnit);
	}
	
	/**
	 * Convert a distance in map coordinates to a screen distance
	 */
	protected float getDistance(float distance) {
		return distance * parent.pixelsPerUnit;
	}
}
