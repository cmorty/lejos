package lejos.pc.comm;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;

import lejos.robotics.Pose;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.*;
import lejos.geom.*;
import lejos.robotics.mapping.*;

/**
 * A panel that can be opened in a frame to control a robot
 * that implements the MCL algorithm. The panel displays the particles and
 * has buttons to move the robot and take readings.
 * 
 * The line map, size and color of the frame is supplied by classes 
 * that extend this class.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MCLFrame extends RemoteFrame{
  private static final long serialVersionUID = 1L;
  public static final float PIXELS_PER_CM = 2f;
  public static final float X_OFFSET = 66f;
  public static final float Y_OFFSET = 56f;
  private static final float ARROW_LENGTH = 10f;

  // Commands sent to the NXT
  private static final byte GET_PARTICLES = 0;
  private static final byte READINGS = 1;
  private static final byte RANDOM_MOVE = 2;
  private static final byte STOP = 3;
  private static final byte FIND_CLOSEST = 4;
  
  // The maximum size of a cluster of particles for a located robot (in cm)
  private static final int MAX_CLUSTER_SIZE = 25;

  // Array of lines for the map
  private Line[] lines;
  private LineMap map; // the map
  private MCLParticleSet particles; // the particle set
  private RangeReadings readings;   
  private int closest = -1;
  private int numParticles;
  
  // GUI Buttons
  private JButton readingsButton, moveButton, stopButton;
  
  /**
   * Paint the map and particles
   */
  public void paintComponent(Graphics g) {
    clear(g);
    Graphics2D g2d = (Graphics2D) g;
    paintMap(g2d);
    paintParticles(g2d);
    paintLocation(g2d);
  }
  
  /**
   * Paint the particles
   * @param g2d the Graphics2D object
   */
  private void paintParticles(Graphics2D g2d) {
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
   * Draw the map using Line2D objects
   * 
   * @param g2d the Graphics2D object
   */
  private void paintMap(Graphics2D g2d) {
	g2d.setColor(Color.black);
    for (int i = 0; i < lines.length; i++) {
      Line2D line = new Line2D.Float(X_OFFSET + lines[i].x1 * PIXELS_PER_CM, Y_OFFSET + lines[i].y1 * PIXELS_PER_CM, 
    		  X_OFFSET + lines[i].x2 * PIXELS_PER_CM, Y_OFFSET + lines[i].y2 * PIXELS_PER_CM);
      g2d.draw(line);
    }
  }

  /**
   * Paint the pose using Ellipse2D
   * 
   * @param g2d the Graphics2D object
   */
  private void paintPose(Graphics2D g2d, Pose pose) {
    Ellipse2D c = new Ellipse2D.Float(X_OFFSET + pose.getX() * PIXELS_PER_CM - 1, Y_OFFSET + pose.getY() * PIXELS_PER_CM - 1, 2, 2);
    Line rl = getArrowLine(pose);
    Line2D l2d = new Line2D.Float(rl.x1, rl.y1, rl.x2, rl.y2);
    g2d.draw(l2d);
    g2d.draw(c);
  }
  
  /**
   * If we are down to one small cluster show the
   * location of the robot.
   * 
   * @param g2d the Graphics2D object
   */
  private void paintLocation(Graphics2D g2d) {
    float minX = particles.getMinX();
    float maxX = particles.getMaxX();
    float minY = particles.getMinY();
    float maxY = particles.getMaxY();
    Pose estimatedPose = particles.getEstimatedPose();
    if (maxX - minX > 0 && maxX - minX <= MAX_CLUSTER_SIZE && 
        maxY - minY > 0 && maxY - minY <= MAX_CLUSTER_SIZE) {
        Ellipse2D c = new Ellipse2D.Float(X_OFFSET + minX * PIXELS_PER_CM, Y_OFFSET + minY * PIXELS_PER_CM, (maxX - minX)  * PIXELS_PER_CM, (maxY - minY)  * PIXELS_PER_CM);
        g2d.setColor(Color.blue);
        g2d.draw(c);
        paintPose(g2d,estimatedPose);
    }
  }

  /**
   * Create a Line that represents the direction of the pose
   * 
   * @param pose the pose
   * @return the arrow line
   */
  private Line getArrowLine(Pose pose) {
    return new Line(X_OFFSET + pose.getX() * PIXELS_PER_CM,
    		        Y_OFFSET + pose.getY() * PIXELS_PER_CM, 
    		        X_OFFSET + pose.getX() * PIXELS_PER_CM + ARROW_LENGTH * (float) Math.cos(Math.toRadians(pose.getHeading())), 
    		        Y_OFFSET + pose.getY() * PIXELS_PER_CM + ARROW_LENGTH * (float) Math.sin(Math.toRadians(pose.getHeading())));
  }

  /**
   * Create the GUI elements the map and the particle set, connect to the
   * NXT and then process button events.
   */
  public MCLFrame(String nxt, Line[] lines, Rectangle bound, int numParticles, int numReadings) throws IOException {
	// Connect to the NXT
	super(nxt);
	this.lines = lines;
    // Create a map of the environment
    map = new LineMap(lines, bound);
    this.numParticles = numParticles;
    particles = new MCLParticleSet(map,numParticles, 10);

    // Create some buttons
    readingsButton = createButton("Readings");
    moveButton = createButton("Move");
    stopButton = createButton("Stop");
    
    //Send Map
    map.dumpMap(dos);
    
    // Send the number of particles  
    dos.writeInt(numParticles);
    dos.flush();

    // Retrieve the particles
    getParticles();
    
    readings = new RangeReadings(numReadings);
  }

  /**
   * Process buttons
   */
  public void actionPerformed(ActionEvent e) {
	try {
	    // Readings button; take readings and calculate weights
	    if (e.getSource() == readingsButton) {
	      sendCommand(READINGS);
	      // Get range readings
	      readings.loadReadings(dis);
	      readings.printReadings();
	      System.out.println("Max weight = " + dis.readFloat());
	      getParticles();
	      repaint();
	    }
	
	    // Move Button: apply a random move
	    if (e.getSource() == moveButton) {
	      sendCommand(RANDOM_MOVE);
	      getParticles();	    
	      repaint();
	    }
	
	    // Stop Button: shut down
	    if (e.getSource() == stopButton) {
	      close();
	    }
    } catch (IOException ioe) {
        error("IOException");
    }  
  }
  
  /**
   * Find the particle closest to the specified coordinates
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the index of the particle
   * 
   */
  private int findClosest(int x, int y) {
    sendCommand(FIND_CLOSEST);
    try {
      dos.writeFloat((float) x);
      dos.writeFloat((float) y);
      dos.flush();
      int closest = dis.readInt();
      for(int i=0;i<3;i++) {
    	float reading = dis.readFloat();
        System.out.println("Reading " + i + " = " + (reading < 0 ? "Invalid" : reading));
      }
      System.out.println("Weight = " + dis.readFloat());
      return closest;
    } catch (IOException ioe) {
      error("IOException");
      return -1;
    }
  }
  
  /**
   * Get current state of the particles from the NXT.
   */
  private void getParticles() throws IOException {
    sendCommand(GET_PARTICLES);
    particles.loadParticles(dis);
    particles.loadEstimation(dis);
  }
  
  /**
   * Send a command to the NXT
   */
  private void sendCommand(byte command) {
    try {
      dos.writeByte(command);
      dos.flush();
    } catch (IOException ioe) {
      error("IO Exception");
    }
  }

  /**
   * Close down the program and the NXT
   */
  private void close() {
    try {
      dos.writeByte(STOP);
      dos.flush();
      Thread.sleep(1000);
      System.exit(0);
    } catch (Exception ioe) {
      error("IO Exception");
    }
  }

  /**
   * Find the closest particle to the mouse click
   */
  public void mouseClicked(MouseEvent me) {
    int x = (int) ((me.getX()- X_OFFSET) /PIXELS_PER_CM );
    int y = (int) ((me.getY() - Y_OFFSET) / PIXELS_PER_CM);
    System.out.println("X = " + x + ", Y = " + y);
    closest = findClosest(x,y);
    repaint();
  }
}

