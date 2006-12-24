import lejos.nxt.*;

public class TestButton
{
  public static void main (String[] aArg)
  throws Exception
  {
    String butt = "Buttons:";
    String b1 = "ENTER";
    String b2 = "LEFT";
    String b3 = "RIGHT";
    String b4 = "ESCAPE";
    String waiting = "Waiting for ENTER";
    String pressed = "ENTER Pressed";

    for(;;) {
       LCD.clear();
       LCD.drawString(butt,0,1);
       LCD.drawInt(Button.readButtons(),9,1);
       if (Button.ENTER.isPressed()) LCD.drawString(b1,0,2);
       if (Button.LEFT.isPressed()) LCD.drawString(b2,0,3);
       if (Button.RIGHT.isPressed()) LCD.drawString(b3,0,3);
       if (Button.ESCAPE.isPressed()) LCD.drawString(b4,0,3);
       LCD.drawString(waiting, 0, 4);
       LCD.refresh();
       Button.ENTER.waitForPressAndRelease();
       LCD.drawString(pressed, 0, 5);
       LCD.refresh();
       Thread.sleep(1000);
    }
  }
}
