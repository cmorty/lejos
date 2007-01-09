package lejos.robotics;

/**
* The Behavior interface represents an object embodying a specific
* behavior belonging to a robot. Each behavior must define three things: <BR>
* 1) The circumstances to make this behavior seize control of the robot.
* e.g. When the touch sensor determines the robot has collided with an object.<BR>
* 2) The action to exhibit when this behavior takes control. 
* e.g. Back up and turn.<BR>
* 3) The actions to perform when another behavior has seized control from this
* behavior. 
* e.g. Stop the current movement and update coordinates.<BR>
* These are represented by defining the methods takeControl(), action(),
* and suppress() respectively. <BR>
* A behavior control system has one or more Behavior objects. When you have defined
* these objects, create an array of them and use that array to initialize an
* Arbitrator object.
*
* @see Arbitrator
* @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
* @version 0.1  27-July-2001
*/
public interface Behavior {
   
   /**
   * Returns a boolean to indicate if this behavior should seize control of the robot.
   * For example, a robot that reacts if a touch sensor is pressed: <BR>
   * public boolean takeControl() { <BR>
   *    return touch.isPressed(); <BR>
   * } <BR>
   * @return boolean Indicates if this Behavior should seize control.
   */
   public boolean takeControl();
   
   /**
   * The code in action() represents the actual action of the robot when this
   * behavior becomes active. It can be as complex as navigating around a
   * room, or as simple as playing a tune.<BR>
   * <B>The contract for implementing this method is:</B><BR>
   * Any action can be started in this method. This method should not start a 
   * never ending loop. This method can return on its own, or when the suppress()
   * method is called; but it must return eventually. The action can run in
   * a seperate thread if the designer wishes it, and can therefore continue
   * running after this method call returns. 
   */
   public void action();
   
   /**
   * The code in suppress() should stop the current behavior. This can include
   * stopping motors, or even calling methods to update internal data (such
   * as navigational coordinates). <BR>
   * <B>The contract for implementing this method is:</B><BR>
   * This method will stop the action running in this Behavior class. This method
   * will <I>not</I> return until that action has been stopped. It is acceptable for a 
   * delay to occur while the action() method finishes up.
   */
   public void suppress();
   
}