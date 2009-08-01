package lejos.robotics;
import java.io.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Represents a set of range readings.
 * 
 * @author Lawrie Griffiths
 */
public class RangeReadings { 
  private int numReadings;
  private float[] ranges;
  private float[] angles;
  
  public RangeReadings(int numReadings) {
    this.numReadings = numReadings;
    ranges = new float[numReadings];
    angles = new float[numReadings];
  }

  /**
   * Set a range reading
   * 
   * @param i the reading index
   * @param range the range value
   */
  public void setRange(int i, float angle, float range) {
    ranges[i] = range;
    angles[i] = angle;
  }

  /**
   * Get a specific range reading
   * 
   * @param i the reading index
   * @return the range value
   */
  public float getRange(int i) {
    return ranges[i];
  }
  
  /**
   * Get a range reading for a specific angle
   * 
   * @param i the reading index
   * @return the range value
   */
  public float getRange(float angle) {
    for(int i=0;i<numReadings;i++) {
    	if (angle == angles[i]) return ranges[i];
    }
    return -1f;
  }
  
  /**
   * Get the angle of a specific reading
   * 
   * @param index the index of the reading
   * @return the angle in degrees
   */
  public float getAngle(int index) {
	  return  angles[index];
  }

  /**
   * Return true if the readings are incomplete
   * 
   * @return true iff one of the readings is not valid
   */
  public boolean incomplete() {
    for (int i = 0; i < numReadings; i++) {
      if (ranges[i] < 0) return true;
    }
    return false;
  }
  
  /**
   * Set the number of readings
   * 
   * @param num the number of readings
   */
  public void setNumReadings(short num) {
    numReadings = num;
    ranges = new float[numReadings];
    angles = new float[numReadings]; 
  }
  
  /**
   * Get the number of readings in a set
   */
  public int getNumReadings() {
    return numReadings;
  }
  
  /**
   * Dump the readings to a DataOutputStream
   * @param dos the stream
   * @throws IOException
   */
  public void dumpReadings(DataOutputStream dos) throws IOException {
    for (int i = 0; i < getNumReadings(); i++)
      dos.writeFloat(getRange(i));
    dos.flush();
  }
  
  /**
   * Load the readings from a DataInputStream
   * @param dis the stream
   * @throws IOException
   */
  public void loadReadings(DataInputStream dis) throws IOException {
    for (int i = 0; i < getNumReadings(); i++) {
      ranges[i] = dis.readFloat();
    }        
  }
  
  /**
   * Print the range readings on standard out
   * @param dis the stream
   * @throws IOException
   */
  public void printReadings() {
    for (int i = 0; i < getNumReadings(); i++) {
      System.out.println("Range " + i + " = " + 
    		  (ranges[i] < 0 ? "Invalid" : ranges[i]));
    }        
  }
}

