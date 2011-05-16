package lejos.pc.comm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

public class MapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected LineMap map;
	protected Line[] lines;
	protected float xOffset, yOffset;
	protected float pixelsPerUnit;
	protected Rectangle boundingRect;
	
	public MapPanel(LineMap map, float xOffset, float yOffset, float pixelsPerUnit) {
		this.map = map;
		this.lines = map.getLines();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.pixelsPerUnit = pixelsPerUnit;
		this.boundingRect = map.getBoundingRect();
		Dimension size = new Dimension((int) (boundingRect.width * pixelsPerUnit + 1), (int) (boundingRect.height * pixelsPerUnit + 1));
		this.setPreferredSize(size);
	}
	
	/**
	 * Draw the map using Line2D objects
	 * 
	 * @param g2d the Graphics2D object
	 */
	public void paintMap(Graphics2D g2d) {
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
	}
}
