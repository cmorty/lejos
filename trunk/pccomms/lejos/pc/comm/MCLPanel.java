package lejos.pc.comm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;
import lejos.robotics.localization.MCLParticle;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;

public class MCLPanel extends MapPanel {
	private static final long serialVersionUID = 1L;
	protected MCLParticleSet particles;
	protected int numParticles;
	protected int closest = -1;
	protected float arrowLength;
	protected MCLPoseProvider mcl;
	
	  // The maximum size of a cluster of particles for a located robot (in cm)
	  private static final int MAX_CLUSTER_SIZE = 25;

	public MCLPanel(LineMap map, float xOffset, float yOffset, float pixelsPerUnit, MCLPoseProvider mcl, float arrowLength) {
		super(map, xOffset, yOffset, pixelsPerUnit);
		this.mcl = mcl;
		this.particles = mcl.getParticles();
		numParticles = particles.numParticles();
		this.arrowLength = arrowLength;
	}
	
	  /**
	   * Paint the map and particles
	   */
	  public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    paintMap(g2d);
	    paintParticles(g2d);
	    paintLocation(g2d);
	  }
	
	  /**
	   * Paint the particles
	   * @param g2d the Graphics2D object
	   */
	  public void paintParticles(Graphics2D g2d) {
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
	   * Paint the pose using Ellipse2D
	   * 
	   * @param g2d the Graphics2D object
	   */
	  public void paintPose(Graphics2D g2d, Pose pose) {
	    Ellipse2D c = new Ellipse2D.Float(xOffset + pose.getX() * pixelsPerUnit - 1, 
	    		                          yOffset + pose.getY() * pixelsPerUnit - 1, 2, 2);
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
	  private Line getArrowLine(Pose pose) {
	    return new Line(xOffset + pose.getX() * pixelsPerUnit,
	    		        yOffset + pose.getY() * pixelsPerUnit, 
	    		        xOffset + pose.getX() * pixelsPerUnit + arrowLength * (float) Math.cos(Math.toRadians(pose.getHeading())), 
	    		        yOffset + pose.getY() * pixelsPerUnit + arrowLength * (float) Math.sin(Math.toRadians(pose.getHeading())));
	  }
	  
	  /**
	   * If we are down to one small cluster show the
	   * location of the robot.
	   * 
	   * @param g2d the Graphics2D object
	   */
	  private void paintLocation(Graphics2D g2d) {
	    float minX = mcl.getMinX();
	    float maxX = mcl.getMaxX();
	    float minY = mcl.getMinY();
	    float maxY = mcl.getMaxY();
	    Pose estimatedPose = mcl.getPose();
	    //System.out.println("Estimate = " + minX + " , " + maxX + " , " + minY + " , " + maxY);
	    if (maxX - minX > 0 && maxX - minX <= MAX_CLUSTER_SIZE && 
	        maxY - minY > 0 && maxY - minY <= MAX_CLUSTER_SIZE) {
	        Ellipse2D c = new Ellipse2D.Float(
	        					xOffset + minX * pixelsPerUnit, 
	        					yOffset + minY * pixelsPerUnit, (maxX - minX)  * pixelsPerUnit, 
	        					(maxY - minY)  * pixelsPerUnit);
	        g2d.setColor(Color.blue);
	        g2d.draw(c);
	        paintPose(g2d,estimatedPose);
	    }
	  }
	  
	  public void setClosest(int closest) {
		  this.closest = closest;
	  }

}
