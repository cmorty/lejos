import lejos.subsumption.*;
import lejos.nxt.*;

public class DriveForward implements Behavior {

   public boolean takeControl() {
      return true;
   }

   public void suppress() {
      Motor.A.stop();
      Motor.C.stop();
   }

   public void action() {
      Motor.A.forward();
      Motor.C.forward();
   }
}
