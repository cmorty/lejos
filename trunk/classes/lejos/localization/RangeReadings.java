package lejos.localization;

/**
 * Represents a set of range readings.
 * 
 * @author Lawrie Griffiths
 */
public class RangeReadings {
  public static short numReadings = 3;
  private float[] ranges = new float[numReadings];

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
      if (ranges[i] < 0) return true;
    }
    return false;
  }
  
  /**
   * Set the number of readings
   * 
   * @param num the number of readings
   */
  public static void setNumReadings(short num) {
    numReadings = num;
  }
  
  /**
   * Get the number of readings in a set
   * 
   * @param num the number of readings
   */
  public static short getNumReadings() {
    return numReadings;
  }
}

