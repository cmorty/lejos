
import lejos.nxt.*;


/**
 * What this class does:
 * @author 
 */

// use Refactor to rename this class to work with NXJMain as you revised it
public class MyRobot
{

  public void go()
  {
    LCD.drawString("Press any button ",0,1);
    Button.waitForPress();
    LCD.drawString("x= "+x,0,2);
    x *= 100;
    LCD.drawInt(x, 4, 8, 2);
    Button.waitForPress();
  }
   //your on variabales here
  private int x = 1;

}
