import lejos.subsumption.*;
import lejos.nxt.*;

/**
 * Demonstration of the Behavior subsumption classes.
 * 
 * Requires a wheeled vehicle with two independently controlled
 * motors connected to motor ports A and C, and a touch
 * sensor connected to sensport port 2.
 * 
 * @author Brian Bagnall and Lawrie Griffiths
 *
 */
public class BumperCar {
   public static void main(String [] args) {
      Behavior b1 = new DriveForward();
      Behavior b2 = new HitWall();
      Behavior [] bArray = {b1, b2};
      Arbitrator arby = new Arbitrator(bArray);
      Motor.A.setSpeed(200);
      Motor.C.setSpeed(200);
      arby.start();
   }
}

