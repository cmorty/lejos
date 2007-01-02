
import lejos.nxt.*;
import lejos.robotics.*;

public class TestNavigator {
	
	public static void main (String[] aArg)
	throws Exception
	{
		TachoNavigator nav = new TachoNavigator(5.5f,11.2f);
		Motor.A.setSpeed(60);
		Motor.C.setSpeed(60);
		
		Button.ENTER.waitForPressAndRelease();
		
		nav.rotate(360);
		nav.gotoPoint(100, 0);
		nav.gotoPoint(100, 100);
		nav.gotoPoint(0, 100);
		nav.gotoPoint(0, 0);
		nav.gotoAngle(0);
		
		LCD.clear();
		LCD.drawString("Home", 6, 3);
		LCD.refresh();
		
		Button.ESCAPE.waitForPressAndRelease();
	}
}
