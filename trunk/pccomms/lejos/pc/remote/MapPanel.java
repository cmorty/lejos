package lejos.pc.remote;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import lejos.geom.Line;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;;
	protected static final float ARROW_LENGTH = 10f;
	protected static final int ROBOT_SIZE = 5;
	protected PCNavigationModel model;
	NavigationPanel parent;
	protected Dimension size;;
	protected int closest = -1;
	protected float arrowLength;
	protected int gridSize = 10;
	
	// The maximum size of a cluster of particles for a located robot (in cm)
	protected static final int MAX_CLUSTER_SIZE = 25;
	
	public MapPanel(PCNavigationModel model, Dimension size, NavigationPanel parent) {
		this.size = size;
		this.model = model;
		setPreferredSize(size);
		this.parent = parent;
		setBackground(Color.WHITE);
	}
	
	public void setSize(Dimension size) {
		this.size = size;
		repaint();
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
		g2d.setColor(Color.black);
		for (int i = 0; i < lines.length; i++) {
			Line2D line = new Line2D.Float(
    		  parent.xOffset + lines[i].x1 * parent.pixelsPerUnit, 
    		  parent.yOffset + lines[i].y1 * parent.pixelsPerUnit, 
    		  parent.xOffset + lines[i].x2 * parent.pixelsPerUnit, 
    		  parent.yOffset + lines[i].y2 * parent.pixelsPerUnit);
			g2d.draw(line);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintGrid((Graphics2D) g);
		paintMap((Graphics2D) g);
		MCLParticleSet particles = model.getParticles();
		if (particles != null) paintParticles((Graphics2D) g);
		paintRobot((Graphics2D) g);
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
			paintPose(g2d, model.getRobotPose());
		} else {
			float minX = mcl.getMinX();
			float maxX = mcl.getMaxX();
			float minY = mcl.getMinY();
			float maxY = mcl.getMaxY();
			Pose estimatedPose = mcl.getPose();
			//System.out.println("Estimate = " + minX + " , " + maxX + " , " + minY + " , " + maxY);
			if (maxX - minX > 0 && maxX - minX <= MAX_CLUSTER_SIZE && 
					maxY - minY > 0 && maxY - minY <= MAX_CLUSTER_SIZE) {
				Ellipse2D c = new Ellipse2D.Float(
	        					parent.xOffset + minX * parent.pixelsPerUnit, 
	        					parent.yOffset + minY * parent.pixelsPerUnit, (maxX - minX)  * parent.pixelsPerUnit, 
	        					(maxY - minY)  * parent.pixelsPerUnit);
				g2d.setColor(Color.blue);
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
		g2d.setColor(Color.RED);
		Ellipse2D c = new Ellipse2D.Float(parent.xOffset + pose.getX() * parent.pixelsPerUnit - 1, parent.yOffset + pose.getY() * parent.pixelsPerUnit - 1, ROBOT_SIZE * parent.pixelsPerUnit, ROBOT_SIZE * parent.pixelsPerUnit);
		//Line rl = getArrowLine(pose);
		//Line2D l2d = new Line2D.Float(rl.x1, rl.y1, rl.x2, rl.y2);
		//g2d.draw(l2d);
		g2d.fill(c);
	}
	
	public void paintGrid(Graphics2D g2d) {
		if (gridSize <= 0) return;
		if (!parent.gridCheck.isSelected()) return;
		g2d.setColor(Color.GREEN);
		for(int i=0; i<this.getHeight(); i+=gridSize*parent.pixelsPerUnit) {
			g2d.drawLine(0,i, this.getWidth()-1,i);		
		}
		for(int i=0; i<this.getWidth(); i+=gridSize*parent.pixelsPerUnit) {
			g2d.drawLine(i,0, i,this.getHeight()-1);		
		}
	}
	
	/**
	 * Paint the particles
	 * @param g2d the Graphics2D object
	 */
	public void paintParticles(Graphics2D g2d) {
		MCLParticleSet particles = model.getParticles();
		int numParticles = particles.numParticles();
		g2d.setColor(Color.red);
		for (int i = 0; i < numParticles; i++) {
			MCLParticle part = particles.getParticle(i);
			if (part != null) {
				if (i == closest) g2d.setColor(Color.green);
				paintPose(g2d, new Pose(part.getPose().getX(), part.getPose().getY(), part.getPose().getHeading()));
				g2d.setColor(Color.red);
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
		return new Line(parent.xOffset + pose.getX() * parent.pixelsPerUnit,
    		        parent.yOffset + pose.getY() * parent.pixelsPerUnit, 
    		        parent.xOffset + pose.getX() * parent.pixelsPerUnit + ARROW_LENGTH * (float) Math.cos(Math.toRadians(pose.getHeading())), 
    		        parent.yOffset + pose.getY() * parent.pixelsPerUnit + ARROW_LENGTH * (float) Math.sin(Math.toRadians(pose.getHeading())));
	}
	
	public void setClosest(int closest) {
		this.closest = closest;
	}
}
