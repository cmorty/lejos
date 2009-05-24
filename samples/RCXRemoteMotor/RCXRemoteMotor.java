
import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * Test of a remote RCX motor using a remote RCX as a controller.
 * 
 * Requires a Mindsensors NRLink adapter connected to sensor
 * port S1 on the NXT, and RCX motors connected to one or
 * more of the RCX motor ports.
 * 
 * Press the ENTER button to increase the power, LEFT to cycle through
 * the motor modes (forward, backward,float,stop) and RIGHT
 * to cycle through the motors.
 * 
 * ESCAPE terminates the program.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RCXRemoteMotor {

	public static void main(String[] args) throws Exception {
		RCXLink link = new RCXLink(SensorPort.S1);
		
		int power = 0;
		int mode = 1;
		int motor = 0;
		RCXMotor[] motors = {link.A, link.B, link.C};
		String motorString = "Motor:";
		String modeString = "Mode:";
		String powerString = "Power:";
		
		while (true) {
			
			int key = Button.waitForPress();
			
			if (key == 1) { // ENTER
				power += 20;
				if (power > 100) power = 0;
			} else if (key == 2) { // LEFT
				mode++;
				if (mode > 4) mode = 1;			
			} else if (key == 4) { // RIGHT
				motor++;
				if (motor > 2) motor = 0;
			} else if (key == 8) { // ESCAPE
				System.exit(0);
			}
			
			LCD.drawString(motorString,0,0);
			LCD.drawInt(motor, 10, 0);
			LCD.drawString(powerString,0,1);
			LCD.drawInt(power, 3, 10, 1);
			LCD.drawString(modeString,0,2);
			LCD.drawInt(mode, 10, 2);
			LCD.refresh();
			motors[motor].setPower(power);
			Thread.sleep(100);
			if (mode == 1) motors[motor].forward();
			else if (mode == 2) motors[motor].backward();
			else if (mode == 3) motors[motor].flt();
			else if (mode == 4) motors[motor].stop();
		}
	}
}
