import lejos.robotics.*;
import lejos.nxt.*;

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

