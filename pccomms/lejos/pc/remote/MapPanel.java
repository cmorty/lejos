package lejos.pc.remote;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;;
	protected float xOffset = 0, yOffset = 0;
	protected float pixelsPerUnit = 2;
	protected static final float ARROW_LENGTH = 10f;
	protected static final int ROBOT_SIZE = 2;
	protected PCNavigationModel model;
	protected Dimension size;
	
	public MapPanel(PCNavigationModel model, Dimension size) {
		this.size = size;
		this.model = model;
		setPreferredSize(size);
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
    		  xOffset + lines[i].x1 * pixelsPerUnit, 
    		  yOffset + lines[i].y1 * pixelsPerUnit, 
    		  xOffset + lines[i].x2 * pixelsPerUnit, 
    		  yOffset + lines[i].y2 * pixelsPerUnit);
			g2d.draw(line);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintMap((Graphics2D) g);
		paintRobot((Graphics2D) g);
	}
	
	protected void paintRobot(Graphics2D g) {
		//paintPose(g, pose);
	}
	
	/**
	 * Paint the pose using Ellipse2D
	 * 
	 * @param g2d the Graphics2D object
	 */
	public void paintPose(Graphics2D g2d, Pose pose) {
		Ellipse2D c = new Ellipse2D.Float(xOffset + pose.getX() * pixelsPerUnit - 1, yOffset + pose.getY() * pixelsPerUnit - 1, ROBOT_SIZE, ROBOT_SIZE);
		Line rl = getArrowLine(pose);
		Line2D l2d = new Line2D.Float(rl.x1, rl.y1, rl.x2, rl.y2);
		g2d.draw(l2d);
		g2d.draw(c);
	}
  
	/**
	 * Create a Line that represents the direction of the pose
	 * 
	 * @param pose the pose
	 * @return the arrow line
	 */
	protected Line getArrowLine(Pose pose) {
		return new Line(xOffset + pose.getX() * pixelsPerUnit,
    		        yOffset + pose.getY() * pixelsPerUnit, 
    		        xOffset + pose.getX() * pixelsPerUnit + ARROW_LENGTH * (float) Math.cos(Math.toRadians(pose.getHeading())), 
    		        yOffset + pose.getY() * pixelsPerUnit + ARROW_LENGTH * (float) Math.sin(Math.toRadians(pose.getHeading())));
	}
}
