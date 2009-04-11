import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.util.*;

/**
* This Example show how to use the sensor PFMate.
*
* Developer Note: I discover that it is necessary to use a delay between 2 commands to use both motors.
*
 * @author Juan Antonio Brenha Moral
 *
 */
public class PFMateTest {

	private static PFMate pfObj;
	private static DebugMessages dm;
	private static final int PFDelayCMD = 50; //ms.

	public static void main(String[] args){
		pfObj = new PFMate(SensorPort.S1,1);

		dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.setDelayEnabled(true);
		
		int i = 7;
		pfObj.A.setSpeed(i);
		try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
		pfObj.B.setSpeed(i);
		
		boolean flag = false;

		
		while(!Button.ESCAPE.isPressed()){
			dm.echo(i);
			
			while(Button.ENTER.isPressed()){
				pfObj.A.stop();
				try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
				pfObj.B.stop();
				try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
			
				if(!flag){
					pfObj.A.forward();
					try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
					pfObj.B.forward();

					Sound.beep();
					
					flag = true;
				}else{
					pfObj.A.backward();
					try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
					pfObj.B.backward();
				
					flag = true;
				}
				
				try {Thread.sleep(500);} catch (Exception e) {}
			}
			
			while(Button.LEFT.isPressed()){
				if(i >1){
					i--;
				}else{
					i = 1;
				}
				
				pfObj.A.setSpeed(i);
				try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
				pfObj.B.setSpeed(i);

				try {Thread.sleep(500);} catch (Exception e) {}
			}
			
			while(Button.RIGHT.isPressed()){
				if(i <7){
					i++;
				}else{
					i = 7;
				}

				pfObj.A.setSpeed(i);
				try {Thread.sleep(PFDelayCMD);} catch (Exception e) {}
				pfObj.B.setSpeed(i);

				try {Thread.sleep(500);} catch (Exception e) {}
			}
		}
		
		pfObj.A.stop();
		try {Thread.sleep(50);} catch (Exception e) {}
		pfObj.B.stop();
		
		credits(2);
		System.exit(0);
	}
	
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
