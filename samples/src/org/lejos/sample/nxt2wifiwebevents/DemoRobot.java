package org.lejos.sample.nxt2wifiwebevents;

import lejos.nxt.*;
import lejos.nxt.addon.NXT2WIFI;
import lejos.nxt.addon.NXT2WiFiListener;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

/**
 * THIS IS A DEMO PROGRAM TO CONTROL 8547 NXT 2.0 SHOOTERBOT (Vehicle challenge 2)
 * FROM THE MANUAL and WEB.
 * MAKE SURE TO HAVE THE DEFAULT WEBSERVER IMAGE LOADED ON THE NXT2WIFI!!!
 * 
 * Build the robot exactly as in the instructions, including motor cables. 
 * 
 * @author BB based on NXC version by Daniele Benedettelli
 *
 */
public class DemoRobot implements NXT2WiFiListener {
	
	// CUSTOM WI-FI PROFILE SETTINGS
	boolean MY_ADHOC = false;
	String MY_SSID = "free_viruses";
	//static String MY_WPA2_KEY = "d4d3a089b20d91ef62bd6045467556a9294355bf63e936e0bb0e952f31071f55";
	String MY_WPA2_PASS = "geocaching";
	boolean MY_DHCP = true;
	
	private NXT2WIFI wifi;
	TouchSensor touch;
	ColorSensor color;
	UltrasonicSensor ultrasonic;
	
	DifferentialPilot pilot;	
	
	// sensorType represents the type of sensor for three sensor ports. 
	// 0 = none
	// 1 = touch
	// 2 = ultrasonic
	// 3 = color full
	// 4, 5, 6 = color single
	static byte sensorType[] = {1, 3, 2}; //Touch, color full, ultrasonic

	public DemoRobot() {
		wifi=new NXT2WIFI();
		pilot = new DifferentialPilot(3.2, 18, Motor.C, Motor.A);
		touch = new TouchSensor(SensorPort.S1);
		color = new ColorSensor(SensorPort.S2);
		ultrasonic = new UltrasonicSensor(SensorPort.S3);
			
	}
		
	public void profileSelectionMenu() {
	   LCD.clear();
	   LCD.drawString("****************",0,0);
	   LCD.drawString("* NXT2WIFI DEMO*",0,1);
	   LCD.drawString("****************",0,2);
	   LCD.drawString("SELECT PROFILE ",0,4);
	   LCD.drawString("TO START ",0,5);
	   LCD.drawString("CSTM  SKIP  DFLT ",0,7);
	   
	   int selection = Button.waitForAnyPress();
	         
	   // CONNECT USING CUSTOM PROFILE
		if (selection == Button.ID_LEFT) {
			// LCD.clear(); // I'm not entirely sure he means to clear the whole screen here.
			LCD.drawString("Disconnecting    ",0, 3);
			wifi.stopConnecting();
			wifi.disconnect();
			 
			if (!wifi.customExists()) {
				Delay.msDelay(100);
				LCD.drawString("Creating custom    ",0,3);
				createCustomWIFI();
			}
			LCD.drawString("Connect CUSTOM    ",0,3);
			wifi.connect(true);
		}
				
		// CONNECT USING DEFAULT PROFILE
		if (selection == Button.ID_RIGHT) {
			//LCD.clear(); // I'm not entirely sure he means to clear the whole screen here.
			LCD.drawString("Disconnecting    ",0,3);
			wifi.disconnect();
			Delay.msDelay(500);
			LCD.drawString("Connect DEFAULT   ",0,3);
			wifi.connect(false);
		}
					
		int trials = 0;
		boolean done = false;
		while(trials<20 && !done) {
			trials++;
			if (wifi.isConnected()) {
				done = true;
			}
			else
				Delay.msDelay(1000);
		}
		if(!done) {
			LCD.clear();
			LCD.drawString("Unable to connect", 0, 4);
			LCD.drawString("Hit ESC to Exit", 0, 5);
			Button.ESCAPE.waitForPressAndRelease();
			System.exit(0);
		}
		
		LCD.clear(); // I'm not entirely sure he means to clear the whole screen here.
		//LCD.clear(4); // clear line 5
		LCD.drawString("Connected!     ",0,3);
		Sound.beepSequenceUp();
		
		// TODO: This didn't return actual IP first time. Gave 192.168.0.100.
		// Ask Daniele about this.
		String ip = wifi.getIPAddress(); 
		LCD.drawString(ip, 0, 4);
		LCD.drawString("Hold ESC to Exit", 0, 5);
		
	}
	
	public void createCustomWIFI() {
		if(MY_ADHOC) wifi.setAdHoc();
		wifi.setSSID(MY_SSID);
		wifi.setSecurity(NXT2WIFI.WF_SEC_WPA2_PASSPHRASE, MY_WPA2_PASS);
		//wifi.setSecurity(NXT2WIFI.WF_SEC_WPA2_KEY, MY_WPA2_KEY);
		wifi.setDHCP(MY_DHCP);
		wifi.saveProfile();
	}

	byte sensorField[] = {0, 1, 2}; // id of webserver field where to show sensor data
	byte motorField[] = {3, 4, 5}; // id of webserver field where to show motor data
	byte batteryField = 6;
	int batteryCount = 0;
	
	/* this function is called periodically to update web page content. */
	private void updateWebComponents() {
		// 0 : sensor 1 (touch)
		// 1 : sensor 2 (color)
		// 2 : sensor 3 (ultrasonic)
		// 3 : motor A rotationCount
		// 4 : motor B rotationCount
		// 5 : motor C rotationCount
		// 6 : batteryLevel
		// 7 : keepMotorsOn
		int val = -1;
		String strVal;
		val = Battery.getVoltageMilliVolt();
		strVal = String.valueOf(val);
	
		// update battery level seldomly
		if (batteryCount>50) {
			wifi.updateWebLabel(batteryField, strVal, 0);
			batteryCount = 0;
		} else batteryCount++;
	
		for (int s=0; s<3; s++) {
			switch(sensorType[s]) {
				case 0: // NONE
					val = -1;
					break;
				case 1: // TOUCH
					val = touch.isPressed()?1:-1;
					break;
				case 2: // ULTRASONIC
					val = ultrasonic.getDistance();
					break;
				case 3: // COLOR FULL
					val = color.getColorID(); // color ID
					break;
				case 4: case 5: case 6: case 7: // COLOR SINGLE
					val = color.getLightValue(); // TODO: This should actually get color value. Need to examine ColorSensor
					break;
			}
			strVal = String.valueOf(val);
			if (sensorType[s]!=0) {
				wifi.updateWebLabel(sensorField[s], strVal, 0);
			}
			
			// Update motor tacho counts:
			int tachoNow = Motor.getInstance(s).getTachoCount();
			//if (oldTachoValue[s]!=tachoNow) {
			wifi.updateWebLabel(motorField[s], String.valueOf(tachoNow), 0);
			//}
			//oldTachoValue[s] = tachoNow;
		}
		
			
		// flush the command replies
		// flushRS485();
		//wifi.clearReadBuffer(); // TODO: Ask Daniele if this is comparable to flushRS485().
	}

	// Controls steering in webEventReveived() method.
	private int direction[] = {80,80,80,80,0,80,-80,-80,-80};
	private int turnRatio[] = {50,0,-50,-200,0,200,-50,0,50};
	
	public void webEventReceived(byte controlType, byte controlID, byte event, byte value) {
		//LCD.clear();
		//LCD.drawString("Web Event     ", 0, 2, true);
		//LCD.drawString("type: " + controlType, 0, 3);
		//LCD.drawString("ID: " + controlID, 0, 4);
		//LCD.drawString("Event: " + event, 0,5);
		//LCD.drawString("Val: " + value, 0,6);
		
		if (controlType !=NXT2WIFI.WEB_CTRL_BTN) return;
	
		if(controlID>=0 && controlID<=8) {
			if (event==0) {
				pilot.stop();
			} else {
				if (direction[controlID]==0) {
					Motor.A.flt();
					Motor.B.flt();
					Motor.C.flt();
				}
				else {
					if(direction[controlID] > 0) {
						pilot.steer(turnRatio[controlID]);
					} else {
						pilot.backward();
						pilot.steerBackward(turnRatio[controlID]);
					}
				} 
			}
		}
		else if(controlID==9) {
			if (event==0) {
				Motor.B.flt();
			} else {
				// Need to build gun to test this.
				Motor.B.rotate(360);
			}
		}
		// TODO: Ask Daniele about the code below.
		// Is this code for future controls on web page? It actually changes the 
		// global sensorType[] array to change what type of sensor is in each port.
		// BTN 10 11 12 control sensor type.
		else if (controlID>=10 && controlID<=12) { // set sensor 1,2,3
			sensorType[controlID-10] = event;
			switch(event) {
				case 0: // NONE
					// TODO: SetSensorType(ctrlID-10, SENSOR_TYPE_NONE); 
					// TODO: SetSensorMode(ctrlID-10,SENSOR_MODE_RAW);
					break;
				case 1: // TOUCH
					// TODO: SetSensorTouch(ctrlID-10); // TODO:
					break;
				case 2: // ULTRASONIC
					// TODO: SetSensorUltrasonic(ctrlID-10);
					break;
				case 3: // COLOR FULL
					// TODO: SetSensorColorFull(ctrlID-10);
					//color.setFloodlight(ColorSensor.);
					break;
					// TODO: kinda unsure if below is supposed to set color type or light
					// Currently does light. To change type need access to ColorSensor.setType().
				case 4: // COLOR RED
					color.setFloodlight(ColorSensor.RED_INDEX);
					break;
				case 5: // COLOR GREEN
					color.setFloodlight(ColorSensor.GREEN_INDEX);
					break;
				case 6: // COLOR BLUE
					color.setFloodlight(ColorSensor.BLUE_INDEX);
					break;
				case 7: // COLOR NONE
					color.setFloodlight(false);
					break;
			}
		}
	}
	
	public static void main(String [] args) {
		DemoRobot robot = new DemoRobot();
		robot.profileSelectionMenu(); // connect to WiFi
		robot.wifi.addListener(robot); // Monitor button events
		
		while(Button.ESCAPE.isUp()) { // && robot.wifi.isConnected()) { // Periodically says not connected?
			robot.updateWebComponents();
			Delay.msDelay(1000);
		}
		
		/* Connection is unreliable? Seems to say disconnected often
		if(!robot.wifi.isConnected()) {
			Sound.beepSequence();
			LCD.clear();
			LCD.drawString("WiFi terminated", 0, 4);
			LCD.drawString("Hit ESC to Exit", 0, 5);
			Button.ESCAPE.waitForPressAndRelease();
		} */
		
		Sound.beepSequence();
	}
}