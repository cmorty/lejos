
 
import lejos.subsumption.*;

/**
 * This interface works with Aribtrator2. 
* The Behavior2 interface represents an object embodying a specific
* behavior belonging to a robot. Each behavior must define three things: <BR>
* 1) The circumstances to make this behavior seize control of the robot.
* e.g. When the touch sensor determines the robot has collided with an object.<br>
* When this happens, the Behavior must inform the Arbitrator that is ready to take control.
* It does this by calling  arbitrator.wantControl(this) and also by returning  true when the
*  arbitrator calls takeControl(); <br>
* 2) The action to exhibit when this behavior takes control. 
* e.g. Back up and turn.  The  action() method does this. <br>
* 3) The actions to perform when another behavior has seized control from this
* behavior. 
* e.g. Stop the current movement and update coordinates.   The method suprews() does this. <BR>
* 4)establish a call back path the arbitrator by implementing the setArbitrator() method. <BR> 
* A behavior control system has one or more Behavior objects. When you have defined
* these objects, create an array of them and use that array to initialize an
* Arbitrator object.
*
* @see Arbitrator2
* @author Roger Glassey
* @version 2  3-Dec - 2007 
*/
public interface Behavior2 extends Behavior{
   

/**
 * call back path so  Behavior2 can call  arbitrator.wantControl(this) when its takeControl state changes to true; 
 * @param theArbitrator
 */
   public void setArbitrator(Arbitrator2 theArbitrator);
   
   
}