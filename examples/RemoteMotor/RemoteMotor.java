
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class RemoteMotor {

	public static void main(String[] args) throws Exception {

		RemoteNXT nxt = null;	
		int power = 0;
		int mode = 1;
		int motor = 0;
		String motorString = "Motor:";
		String modeString = "Mode:";
		String powerString = "Power:";
		
        try {
            LCD.drawString("Connecting...",0,0);
            LCD.refresh();
        	nxt = new RemoteNXT("NOISY");
        	LCD.clear();
            LCD.drawString("Connected",0,0);
            LCD.refresh();
        } catch (IOException ioe) {
        	LCD.clear();
            LCD.drawString("Conn Failed",0,0);
            LCD.refresh();
            Thread.sleep(2000);
            System.exit(1);
        }
        
        Thread.sleep(2000);
        LCD.clear();
        LCD.refresh();
        
		Motor[] motors = {nxt.A, nxt.B, nxt.C};
	
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
			LCD.drawInt(motors[motor].getTachoCount(), 6,  0, 3);
			LCD.refresh();
			motors[motor].setPower(power);
			if (mode == 1) motors[motor].forward();
			else if (mode == 2) motors[motor].backward();
			else if (mode == 3) motors[motor].flt();
			else if (mode == 4) motors[motor].stop();
		}
	}
}

