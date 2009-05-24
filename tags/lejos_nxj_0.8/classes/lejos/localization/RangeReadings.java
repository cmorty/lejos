package lejos.localization;
import java.io.*;

/**
 * Represents a set of range readings.
 * 
 * @author Lawrie Griffiths
 * 
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
public class RangeReadings {
  public static final float INVALID_READING = -1f;
  
  private int numReadings = 3;
  private float[] ranges = new float[numReadings];
  
  public RangeReadings(int numReadings) {
    this.numReadings = numReadings;
  }

  /**
   * Set a range reading
   * 
   * @param i the reading index
   * @param range the range value
   */
  public void setRange(int i, float range) {
    ranges[i] = range;
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
   * Return true if the readings are incomplete
   * 
   * @return true iff one of the readings is not valid
   */
  public boolean incomplete() {
    for (int i = 0; i < numReadings; i++) {
      if (ranges[i] == INVALID_READING) return true;
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
      System.out.println("Range " + i + " = " + 
    		  (ranges[i] < 0 ? "Invalid" : ranges[i] + "cm"));
    }        
  }
}

