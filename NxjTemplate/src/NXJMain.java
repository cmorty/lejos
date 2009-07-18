import lejos.nxt.*;
// The  library for this project must include  classes.jar from  NXJ_HOME

/**
 * This class builds and runs a robot.
 * It should be the main class of this project, and probably should be renamed
 * @author owner
 */
// use this  class to build and run your robot
public class NXJMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
      LCD.drawString("Building Robot ",0,0);
      MyRobot  robot = new MyRobot(); // Give you robot class another name
      robot.go();
    }

}

