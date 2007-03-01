package lejos.robotics;

import lejos.nxt.CompassSensor;
import lejos.nxt.*;

/**
 * Uses a compass to control rotation.
 * @author BB
 */
public class CompassPilot extends Pilot {
  private CompassSensor compass;
  //private SteeringControl sc;
  private boolean isCompassTravel = true;
  
  public CompassPilot(CompassSensor compass, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor) {
    this(compass, wheelDiameter, trackWidth, leftMotor, rightMotor, false);
  }

  public CompassPilot(CompassSensor compass, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) {
    super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
  	this.compass = compass;
  	try {Thread.sleep(100);} catch (Exception e) {} // Allow compass to stabalize
  	compass.resetCartesianZero();
  }
  
  /**
   * Rotates the specified number of degrees
   */
  public void rotate(int degrees) {
    int prevReading = (int)compass.getDegreesCartesian();
    int total = 0; // running total of degrees rotated
    rotate(degrees* 2); // Start rotating - will use stop() to end rotation
    degrees = Math.abs(degrees);
    while(total < degrees) {
      int curReading = (int)compass.getDegreesCartesian();
      total += getDifference(curReading, prevReading);
      prevReading = curReading;
      Thread.yield();
    }
    stop();
  }
  
  public int getAngle() {
  	return Math.round(compass.getDegreesCartesian());
  }
  
  /** Returns absolute value of difference */
  private int getDifference(int cur, int prev) {
    int diff = Math.abs(cur - prev);
    if(diff > 180) diff = 360 - diff;
    return diff;
  }
  
  /**
   *  Set to true if you want to use compass to maintain straigh line while traveling.
   *  Warning: If in house, metal objects cause local variations in magnetic field which
   *  cause it to weave slightly.
   */
  public void setCompassTravel(boolean useCompass) {
    isCompassTravel = useCompass;
  }
}
