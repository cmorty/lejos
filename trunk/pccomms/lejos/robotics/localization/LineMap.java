package lejos.robotics.localization;

import java.awt.Rectangle;
import lejos.geom.*;
import java.io.*;

/**
 * A map of a room or other closed environment, represented by line segments
 * 
 * @author Lawrie Griffiths
 * 
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 * 
 */
public class LineMap implements Map {
  private Line[] lines;
  private Rectangle boundingRect;

  /**
   * Calculate the range of a robot to the nearest wall
   * 
   * @param pose the pose of the robot
   * @return the range or -1 if not in range
   */
  public float range(Pose pose) {
    Line l = pose.getRangeLine();
    Line rl = null;

    for (int i = 0; i < lines.length; i++) {
      Point p = lines[i].intersectsAt(l);
      if (p == null) continue; // Does not intersect
      Line tl = new Line(pose.x, pose.y, p.x, p.y);

      // If the range line intersects more than one map line
      // then take the shortest distance.
      if (rl == null || tl.length() < rl.length()) rl = tl;
    }
    return (rl == null ? -1 : rl.length());
  }

  /**
   * Create a map from an array of line segments and a bounding rectangle
   * 
   * @param lines the line segments
   * @param boundingRect the bounding rectangle
   */
  public LineMap(Line[] lines, Rectangle boundingRect) {
    this.lines = lines;
    this.boundingRect = boundingRect;
  }
  
  /**
   * Constructor to use when map will be loaded from a data stream
   */
  public LineMap() {
  }

  /**
   * Check if a point is within the mapped area
   * 
   * @param p the Point
   * @return true iff the point is with the mapped area
   */
  public boolean inside(Point p) {
    if (p.x < boundingRect.x || p.y < boundingRect.y) return false;
    if (p.x > boundingRect.x + boundingRect.width
        || p.y > boundingRect.y + boundingRect.height) return false;

    // Create a line from the point to the left
    Line l = new Line(p.x, p.y, p.x - boundingRect.width, p.y);

    // Count intersections
    int count = 0;
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].intersectsAt(l) != null) count++;
    }
    // We are inside if the number of intersections is odd
    return count % 2 == 1;
  }

  /**
   * Return the bounding rectangle of the mapped area
   * 
   * @return the bounding rectangle
   */
  public Rectangle getBoundingRect() {
    return boundingRect;
  }
  
  /**
   * Dump the map to a DataOutputStream
   * @param dos the stream
   * @throws IOException
   */
  public void dumpMap(DataOutputStream dos) throws IOException {
      dos.writeInt(lines.length);
      for(int i=0;i<lines.length;i++) {
        dos.writeFloat(lines[i].x1);
        dos.writeFloat(lines[i].y1);
        dos.writeFloat(lines[i].x2);
        dos.writeFloat(lines[i].y2);
        dos.flush();
      }  
      dos.writeInt(boundingRect.x);
      dos.writeInt(boundingRect.y);
      dos.writeInt(boundingRect.width);
      dos.writeInt(boundingRect.height);
      dos.flush();
  }
  /**
   * Load a map from a DataInputStream
   * 
   * @param dis the stream
   * @throws IOException
   */
  public void loadMap(DataInputStream dis) throws IOException {
      lines = new Line[dis.readInt()];
      for(int i=0;i<lines.length;i++) {
        float x1 = dis.readFloat();
        float y1 = dis.readFloat(); 
        float x2 = dis.readFloat();
        float y2 = dis.readFloat();
        lines[i] = new Line(x1,y1,x2,y2);
      }     
      boundingRect = new Rectangle(dis.readInt(),dis.readInt(),dis.readInt(),dis.readInt());
  }
}

