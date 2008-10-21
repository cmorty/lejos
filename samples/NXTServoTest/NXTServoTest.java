import lejos.nxt.addon.*;
import lejos.nxt.*;

/**
 * Example designed to test Mindsensors NXT Servo
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class NXTServoTest{

	public static void main(String[] args){
		DebugMessages dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.echo("Testing NXT Servo");
		
		MSC msc = new MSC(SensorPort.S1);
		msc.addServo(1,"Mindsensors RC Servo 9Gr");

		while(!Button.ESCAPE.isPressed()){
			dm.echo(msc.getBattery());
			
			if (Button.LEFT.isPressed()){
				msc.getServo(0).setAngle(50);

				dm.echo("Goto Min");
			}
			
			if (Button.ENTER.isPressed()){
				msc.getServo(0).setAngle(180);
				dm.echo("Goto Middle");
			}

			if (Button.RIGHT.isPressed()){
				msc.getServo(0).setAngle(250);
				dm.echo("Goto Max");
			}
		}

		dm.echo("Test finished");
	}
}
