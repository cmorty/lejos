package sample;


import lejos.nxt.*;

/**
 * This robot displays some stuff on the screen
 * @author 
 */
// use Refactor to rename this class to work with NXJMain as you revised it
public class MyRobot {

  public void go() {
    LCD.drawString("Press any button ", 0, 1);
    Button.waitForPress();
    Motor.A.rotate(angle);
    System.out.println("tc " + Motor.A.getTachoCount());
    Button.waitForPress();
  }
  //your  variables here
  private int angle = 90;
}
