
import lejos.nxt.*;
import lejos.navigation.*;

public class TestNavigator {
	
	public static void main (String[] aArg)
	throws Exception
	{
		CompassSensor compass = new CompassSensor(SensorPort.S1);
		CompassNavigator nav = new CompassNavigator(compass, 5.5f,11.2f,Motor.A, Motor.C);
		
		Motor.A.regulateSpeed(true);
		Motor.C.regulateSpeed(true);
		
		Motor.A.setSpeed(300);
		Motor.C.setSpeed(300);
		
		LCD.drawInt((int)Runtime.getRuntime().freeMemory(),0,0);
		LCD.refresh();
		
		Button.ENTER.waitForPressAndRelease();
		
		nav.rotate(270);
		nav.goTo(100, 0);
		nav.goTo(100, 100);
		nav.goTo(0, 100);
		nav.goTo(0, 0);
		nav.rotateTo(0);
		
		LCD.clear();
		LCD.drawString("Home", 6, 3);
		LCD.refresh();
		
		Button.ESCAPE.waitForPressAndRelease();
		
		LCD.clear();
		LCD.drawString("Finished", 6, 3);
		LCD.refresh();
	}
}
