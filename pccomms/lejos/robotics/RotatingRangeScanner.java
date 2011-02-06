package lejos.robotics;

import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Implementation of RangeScanner with a rotating ultrasonic sensor
 * @author Roger Glassey
 */
public class RotatingRangeScanner implements RangeScanner
{

  /**
   * The constructor defines the wiring diagram - the motor port and sensor port used
   * @param head the motor that rotates the sensor
   * @param port the port to which the sensor is wired
   */
  public RotatingRangeScanner(RegulatedMotor head, SensorPort port)
  {
    this.head = head;
    sonar = new UltrasonicSensor(port);
    sonar.continuous();
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
//    RConsole.println("Scanner getRanges "+angles[0]+" "+angles[1]);
    for (int i = 0; i < angles.length; i++)
    {
      head.rotateTo((int) angles[i]);
      Delay.msDelay(50);
      float range = sonar.getRange() + ZERO;
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
  protected UltrasonicSensor sonar;
  protected RegulatedMotor head;
  protected float[] angles ={0,90};// default
}
