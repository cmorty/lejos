import lejos.nxt.*;
import lejos.nxt.remote.*;
import java.io.*;

public class RemoteNXTTest {
	public static void main(String[] args) throws Exception {
		RemoteNXT nxt = null;	
		int power = 0;
		int mode = 1;
		int motor = 0;
		String motorString = "Motor:";
		String modeString = "Mode:";
		String powerString = "Power:";
		String batteryString = "Battery:";
		String lightString = "Light:";
		String tachoString = "Tacho:";
		
        try {
            LCD.drawString("Connecting...",0,0);
        	nxt = new RemoteNXT("NOISY");
        	LCD.clear();
            LCD.drawString("Connected",0,0);
            Thread.sleep(2000);
        } catch (IOException ioe) {
        	LCD.clear();
            LCD.drawString("Conn Failed",0,0);
            Thread.sleep(2000);
            System.exit(1);
        }
        
		Motor[] motors = {nxt.A, nxt.B, nxt.C};
		LightSensor light = new LightSensor(nxt.S1);
	
		while (true) {
			LCD.clear();
			LCD.drawString(motorString,0,0);
			LCD.drawInt(motor, 10, 0);
			LCD.drawString(powerString,0,1);
			LCD.drawInt(power, 3, 10, 1);
			LCD.drawString(modeString,0,2);
			LCD.drawInt(mode, 10, 2);
			LCD.drawString(tachoString,0,3);
			LCD.drawInt(motors[motor].getTachoCount(), 6,  7, 3);
			LCD.drawString(batteryString,0,4);
			LCD.drawInt(nxt.Battery.getVoltageMilliVolt(), 6,  9, 4);
			LCD.drawString(lightString,0,5);
			LCD.drawInt(light.readValue(), 4,  7, 5);
			LCD.drawString(nxt.getBrickName(), 0, 6);
			LCD.drawString(nxt.getFirmwareVersion(), 0, 7);
			LCD.drawString(nxt.getProtocolVersion(), 4, 7);
			LCD.drawInt(nxt.getFlashMemory(), 6, 8, 7);
			
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
			
            LCD.clear();;
            LCD.drawString("Setting power",0,0);
			motors[motor].setPower(power);
            LCD.drawString("Moving motor",0,1);
			if (mode == 1) motors[motor].forward();
			else if (mode == 2) motors[motor].backward();
			else if (mode == 3) motors[motor].flt();
			else if (mode == 4) motors[motor].stop();
		}
	}
}


