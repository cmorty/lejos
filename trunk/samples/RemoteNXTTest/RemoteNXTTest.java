import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.nxt.remote.*;
import lejos.util.TextMenu;

import java.io.*;
/**
 * This program demonstrates the use of the RemoteNXT class to allow one NXT
 * to control another. It should be used in conjunction either with the standard
 * leJOS menu (for Bluetooth only), or with the NXTLCPRespond test program (for
 * Bluetooth and RS485). The two NXTs may be connected either via Bluetooth or
 * by RS485. The program retrieves a number of data items from the remote NXT,
 * and allows the motors on the remote device to be controlled using the keypad.
 * A light sensor should be plugged into port 1 of the remote NXT. If using
 * RS485 communications the two NXTs should be linked using a standard Lego
 * connector between port 4 on both devices.
 * NOTE: Be sure to change the name of the target brick below from "NXT" to 
 * the proper name. Also, as stated above, if using RS-485 make sure NXTLCPRespond 
 * is running on the target brick (port 4 to port 4) before running this program. 
 */
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

        // Get the type of communications to be used
        String[] connectionStrings = new String[]{"Bluetooth", "RS485"};
        TextMenu connectionMenu = new TextMenu(connectionStrings, 1, "Connection");
        NXTCommConnector[] connectors = {Bluetooth.getConnector(), RS485.getConnector()};

        int connectionType = connectionMenu.select();

        // Now connect
        try {
            LCD.clear();
            LCD.drawString("Connecting...",0,0);
        	nxt = new RemoteNXT("NXT", connectors[connectionType]);
        	LCD.clear();
            LCD.drawString("Type: " + connectionStrings[connectionType], 0, 0);
            LCD.drawString("Connected",0,1);
            Thread.sleep(2000);
        } catch (IOException ioe) {
        	LCD.clear();
            LCD.drawString("Conn Failed",0,0);
            Thread.sleep(2000);
            System.exit(1);
        }

        LCD.clear();
		RemoteMotor[] motors = {nxt.A, nxt.B, nxt.C};
		LightSensor light = new LightSensor(nxt.S2);
		while (true) {
			// Get data from the remote NXT and display it
			LCD.drawString(motorString,0,0);
			LCD.drawInt(motor, 3, 10, 0);
			LCD.drawString(powerString,0,1);
			LCD.drawInt(power, 3, 10, 1);
			LCD.drawString(modeString,0,2);
			LCD.drawInt(mode, 3, 10, 2);
			LCD.drawString(tachoString,0,3);
			LCD.drawInt(motors[motor].getTachoCount(), 6,  7, 3);
			LCD.drawString(batteryString,0,4);
			LCD.drawInt(nxt.Battery.getVoltageMilliVolt(), 6,  7, 4);
			LCD.drawString(lightString,0,5);
			LCD.drawInt(light.readValue(), 6,  7, 5);
			LCD.drawString(nxt.getBrickName(), 0, 6);
			LCD.drawString(nxt.getFirmwareVersion(), 0, 7);
			LCD.drawString(nxt.getProtocolVersion(), 4, 7);
			LCD.drawInt(nxt.getFlashMemory(), 6, 8, 7);

            // Do we have a button press?
			int key = Button.readButtons();
			if (key != 0)
            {
                // New command, work out what to do.
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
                    LCD.clear();
                    LCD.drawString("Closing...", 0, 0);
                    for(int i = 0; i < motors.length; i++)
                        motors[i].flt();
                    nxt.close();
                    Thread.sleep(2000);
                    System.exit(0);
                }

                LCD.clear();
                LCD.drawString("Setting power",0,0);
                motors[motor].setPower(power);
                LCD.drawString("Moving motor",0,1);
                if (mode == 1) motors[motor].forward();
                else if (mode == 2) motors[motor].backward();
                else if (mode == 3) motors[motor].flt();
                else if (mode == 4) motors[motor].stop();
                // Wait for the button to be released...
                while (Button.readButtons() != 0)
                    Thread.yield();
                LCD.clear();
            }
		}
	}
}


