package lejos.robotics.navigation;

import lejos.robotics.TachoMotor;

/**
 * This class exists only for backward compatibility with release 0.7 <br>
 * See the javadoc for SimpleNavigator if you don't know how this class works.
 *  
 * @deprecated in 0.8, use SimpleNavigator
 * 
 * @author Roger Glassey
 */
public class TachoNavigator extends SimpleNavigator
{
  /**
   * Allocates a TachoNavigator with the Pilot that you supply
   * The x and y coordinate values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
   * the x axis. <BR>
   * @param aPilot the pilot
   */
  public TachoNavigator(Pilot aPilot)
  {
    super(aPilot);
  }
  
  /**
   * Allocates a TachoNavigator object and initializes it with a TachoPilot
   * The x and y values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
   * the x axis. <BR>
   * @param wheelDiameter The diameter of the wheel, usually printed right on the
   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm = 1.95 in)
   * @param trackWidth The distance from the center of the left tire to the center
   * of the right tire, in units of your choice
   * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
   * @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
   * @param reverse  If motor.forward() drives the robot backwards, set this parameter true.
   */
  public TachoNavigator(float wheelDiameter, float trackWidth, TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse)
  {
    super(wheelDiameter,trackWidth,leftMotor, rightMotor,reverse);
  }
   
  /**
   * Allocates a TachoNavigator object and initializes it with a TachoPilot
   * The x and y values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
   * the x axis. <BR>
   * @param wheelDiameter The diameter of the wheel, usually printed right on the
   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm = 1.95 in)
   * @param trackWidth The distance from the center of the left tire to the center
   * of the right tire, in units of your choice
   * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
   * @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
   */
  public TachoNavigator(float wheelDiameter, float trackWidth, TachoMotor leftMotor, TachoMotor rightMotor)
  {
    super(wheelDiameter,trackWidth,leftMotor, rightMotor);
  }
}
