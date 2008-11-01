import lejos.nxt.addon.*;
import lejos.nxt.*;

/**
 * Example designed to test Mindsensors NXT Servo
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class NXTServoTest{
	private static String appName = "NXTServo Test";
	private static String appVersion = "v0.2";

	private static MSC msc;
	
	public static void main(String[] args){
		LCD.drawString(appName, 0, 0);
		LCD.drawString("#################", 0, 2);
		LCD.drawString("#################", 0, 6);

		msc = new MSC(SensorPort.S1);
		msc.addServo(1,"Mindsensors RC Servo 9Gr");
		//Set to initial angle
		msc.getServo(0).setAngle(0);
		
		int angle = 0;
		int pulse = 0;
		int NXTServoBattery = 0;

		while(!Button.ESCAPE.isPressed()){
			NXTServoBattery = msc.getBattery();

			if (Button.LEFT.isPressed()){
				angle = 0;
				msc.getServo(0).setAngle(angle);
			}
			
			if (Button.ENTER.isPressed()){
				angle = 90;
				msc.getServo(0).setAngle(angle);
			}

			if (Button.RIGHT.isPressed()){
				angle = 180;
				msc.getServo(0).setAngle(angle);
			}
			
			clearRows();
			LCD.drawString("Battery: " + NXTServoBattery, 0, 3);
			LCD.drawString("Pulse:   " + msc.getServo(0).getPulse(), 0, 4);
			LCD.drawString("Angle:   " + msc.getServo(0).getAngle(), 0, 5);
			LCD.refresh();
		}

		//Set to initial angle
		msc.getServo(0).setAngle(0);

		LCD.drawString("Test finished",0,7);
		LCD.refresh();
		try {Thread.sleep(1000);} catch (Exception e) {}
		credits(3);
		System.exit(0);
	}
	
	/**
	 * Internal method used to clear some rows in User Interface
	 */
	private static void clearRows(){
		LCD.drawString("               ", 0, 3);
		LCD.drawString("               ", 0, 4);
		LCD.drawString("               ", 0, 5);
	}

	/**
	 * Final Message
	 * 
	 * @param seconds
	 */
	private static void credits(int seconds){
		LCD.clear();
		LCD.drawString("LEGO Mindstorms",0,1);
		LCD.drawString("NXT Robots  ",0,2);
		LCD.drawString("run better with",0,3);
		LCD.drawString("Java leJOS",0,4);
		LCD.drawString("www.lejos.org",0,6);
		LCD.refresh();
		try {Thread.sleep(seconds*1000);} catch (Exception e) {}
	}
}
