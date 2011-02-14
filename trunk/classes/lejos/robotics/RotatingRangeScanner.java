package lejos.robotics;

import lejos.util.Delay;

/**
 * Implementation of RangeScanner with a rotating ultrasonic sensor or other range finder
 * @author Roger Glassey
 */
public class RotatingRangeScanner implements RangeScanner
{

  /**
   * The constructor specifies the motor and range finder used
   * @param head the motor that rotates the sensor
   * @param range the range finder
   */
  public RotatingRangeScanner(RegulatedMotor head, RangeFinder rangeFinder)
  {
    this.head = head;
    this.rangeFinder = rangeFinder;
  }
  
  /**
   * Returns a set of Range Readings taken the angles specified.
   * @return the set of range values
   */
  public RangeReadings getRangeValues()
  {

    if (readings == null || readings.getNumReadings() != angles.length)
    {
      readings = new RangeReadings(angles.length);
    }

    for (int i = 0; i < angles.length; i++)
    {
      head.rotateTo((int) angles[i]);
      Delay.msDelay(50);
      float range = rangeFinder.getRange() + ZERO;
      if (range > MAX_RELIABLE_RANGE_READING)
      {
        range = -1;
      }
      readings.setRange(i, angles[i], range);
    }
    head.rotateTo(0);
    return readings;
  }

  /**
   * set the angles to be used by the getRangeValues() method
   * @param angles
   */
  public void setAngles(float[] angles)
  {
    this.angles = angles.clone();
  }
  
  protected final int MAX_RELIABLE_RANGE_READING = 180;
  protected final int ZERO = 2;// correction of sensor zero
  protected RangeReadings readings;;
  protected RangeFinder rangeFinder;
  protected RegulatedMotor head;
  protected float[] angles ={0,90};// default
}
