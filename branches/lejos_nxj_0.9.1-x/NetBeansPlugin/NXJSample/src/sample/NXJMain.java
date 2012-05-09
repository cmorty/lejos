package sample;


import lejos.nxt.*;
import lejos.util.Delay;
// The  library for this project sould be set to include  classes.jar from  NXJ_HOME

/**
 * This class builds and runs a robot.
 * It should be the main class of this project, and probably should be renamed
 * Put in your name as the author.
 * @author owner
 */
public class NXJMain
{

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      LCD.drawString("Building Robot ", 0, 0);
      MyRobot robot = new MyRobot(); // Give you robot class another name
      robot.go();
      System.out.println("Bye");
      Delay.msDelay(1000);
   }
}
