package lejos.robotics.subsumption;


/**
* The Behavior interface represents an object embodying a specific
* behavior belonging to a robot. Each behavior must define three things: <BR>
* 1) The circumstances to make this behavior seize control of the robot.
* e.g. When the touch sensor determines the robot has collided with an object.<BR>
* 2) The action to perform when this behavior takes control. 
* e.g. Back up and turn.<BR>
* 3) The tasks to perform when another behavior has seized control from this
* behavior, including interrupting it.  S
* e.g. Stop the current movement and update coordinates.<BR>
* These are represented by defining the methods takeControl(), action(),
* and suppress() respectively. <BR>
* A behavior control system has one or more Behavior objects. When you have defined
* these objects, create an array of them and use that array to initialize an
* Arbitrator object.
*
* @see Arbitrator
  
* @version 0.7  28 Dec-2008
*/
public interface Behavior {
   
   /**
   * The boolean return  indicates  if this behavior should seize control of the robot.
   * For example, a robot that reacts if a touch sensor is pressed: <BR>
   * public boolean takeControl() { <BR>
   *    return touch.isPressed(); <BR>
   * } <BR>
   * @return boolean  Indicates if this Behavior should seize control.
   */
   public boolean takeControl();
   
   /**
   * The code in action() represents the tasks  the robot performs when this
   * behavior becomes active. It can be as complex as navigating around a
   * room, or as simple as playing a tune.<BR>
   * <B>The contract for implementing this method is:</B><BR>
   * Any action can be started in this method. If the action is complete, the
    * method should return.  It <B> must </B> return when the suppress() method 
    * is called, even if it runs  a  separate thread. <br>   
   */
   public void action();
   
   /**
   * The code in suppress() should stop the current behavior. This can include
   * stopping motors, or even calling methods to update internal data (such
   * as navigational coordinates). <BR>
   * <B>The contract for implementing this method is:</B><BR>
   * This method will stop the action running in this Behavior class and cause action()
    * to exit promptly. If action() is not running, this method should leave the robot
    * in a safe state for any other Behavior to run.
   */
   public void suppress();
   
}