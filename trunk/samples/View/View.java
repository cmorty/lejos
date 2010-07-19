import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.util.TextMenu;

public class View {
	
	public static void main (String[] aArg)
	throws Exception
	{
		String sys = "System info";
		String batt = "Battery :";
		String tot = "Tot mem :";
		String free = "Free mem:";
		String sensVal = "Sensor Value";
		String mot = "Motors";
		String mots = "Motors:";
		String tach = "Tacho:";
		String port = "Port:";
		String type = "Type:";
		String val = "Value:";
		String pressed = "pressed ";
		String released = "released";
		
	    String[] viewItems = {"System", "Sensors", "Motors", "Exit"};
	    
		TextMenu main = new TextMenu(viewItems, 1, "View Example");
		
		String[] sensorItems = {"Touch","Light(Floodlit)","Light(Ambient)","Sound DB","Sound DBA","RCX Light","Ultrasonic"};
		
		TextMenu pickSensor = new TextMenu(sensorItems, 1, "Pick Sensor");
		
		String[] sensorPorts = {"S1","S2","S3","S4"};
		TextMenu pickSensorPort = new TextMenu(sensorPorts, 1, "Pick Port");

		String[] motors = {"A","B","C","A&B","A&C","B&C"};
		TextMenu pickMotor = new TextMenu(motors,1,"Pick Motor");
		
		String[] motorMethods = {"forward","backward","flt","stop"};
		TextMenu operation = new TextMenu(motorMethods, 1, "Pick option");
		
		int selection;
		
		for(;;)
		{
			LCD.clear();
			selection = main.select();
			
			if (selection == -1 || selection == 3)
			{
				LCD.clear();
				LCD.drawString("Finished",3,4);
				LCD.refresh();	
				return;
			}
			
			if (selection == 0) // System Info
			{
				LCD.clear();
				LCD.drawString(sys, 0, 0);
				LCD.drawString(batt, 0, 2);
				LCD.drawInt(Battery.getVoltageMilliVolt(), 4, 10, 2);
				LCD.drawString(tot, 0, 3);
			    LCD.drawInt((int)(Runtime.getRuntime().totalMemory()), 5, 10, 3);
				LCD.drawString(free, 0, 4);
			    LCD.drawInt((int)(Runtime.getRuntime().freeMemory()), 5, 10,4);
			    LCD.refresh();
			    
			    Button.ESCAPE.waitForPressAndRelease();
			}
			
			if (selection == 1) // Sensors				
			{
				LCD.clear();
				int portId = pickSensorPort.select();
				if (portId < 0) 
				{
					Button.ESCAPE.waitForPressAndRelease();
					continue;
				}

				LCD.clear();
				int sensor = pickSensor.select();				
				if (sensor < 0)
				{
					Button.ESCAPE.waitForPressAndRelease();
					continue;
				} 
				
				LCD.clear();
				LCD.drawString(sensVal, 0, 0);				
				LCD.drawString(port, 0, 2);
				LCD.drawString(sensorPorts[portId],6,2);
				LCD.drawString(type, 0, 3);
				LCD.drawString(sensorItems[sensor],6,3);
				LCD.drawString(val,0,4);
				
				SensorPort sp = SensorPort.getInstance(portId);
				
				if (sensor == 0)
				{
					TouchSensor touch = new TouchSensor(sp);
					
					while (!Button.ESCAPE.isPressed())
					{
						if (touch.isPressed()) LCD.drawString(pressed,7,4);
						else LCD.drawString(released, 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}
				
				if (sensor == 1 || sensor == 2)
				{
					LightSensor light = new LightSensor(sp, sensor == 1);
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawInt(light.readValue(), 3, 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}

				if (sensor == 3 || sensor == 4)
				{
					SoundSensor sound = new SoundSensor(sp, sensor == 4);
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawInt(sound.readValue(), 3, 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}	
				
				if (sensor == 5) // RCX Light Sensor
				{
					RCXLightSensor light = new RCXLightSensor(sp);
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawInt(light.readValue(), 3, 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}
				
				if (sensor == 6) // Ultrasonic
				{
					UltrasonicSensor sonar = new UltrasonicSensor(sp);
					sonar.continuous();
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawInt(sonar.getDistance(), 3, 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}
				Button.ESCAPE.waitForPressAndRelease();
			}
			
			if (selection == 2) // Motors
			{
				LCD.clear();
				int motor = pickMotor.select();				
				if (motor < 0)
				{
					Button.ESCAPE.waitForPressAndRelease();
					continue;
				}
				
				for(;;)
				{				
				  LCD.clear();
				  int op = operation.select();				  
				  if (op < 0)
				  {
					  Button.ESCAPE.waitForPressAndRelease();
					  break;
				  }
				
				  if (op == 0) // forwards
				  {
					if (motor == 0 || motor == 3 || motor == 4) Motor.A.forward();
					if (motor == 1 || motor == 3 || motor == 5) Motor.B.forward();
					if (motor == 2 || motor == 4 || motor == 5) Motor.C.forward();
				  }
				  
				  if (op == 1) // backwards
				  {
					if (motor == 0 || motor == 3 || motor == 4) Motor.A.backward();
					if (motor == 1 || motor == 3 || motor == 5) Motor.B.backward();
					if (motor == 2 || motor == 4 || motor == 5) Motor.C.backward();
				  }
				  
				  if (op == 2) // float
				  {
					if (motor == 0 || motor == 3 || motor == 4) Motor.A.flt();
					if (motor == 1 || motor == 3 || motor == 5) Motor.B.flt();
					if (motor == 2 || motor == 4 || motor == 5) Motor.C.flt();
				  }
				  
				  if (op == 3) // stop
				  {
					if (motor == 0 || motor == 3 || motor == 4) Motor.A.stop();
					if (motor == 1 || motor == 3 || motor == 5) Motor.B.stop();
					if (motor == 2 || motor == 4 || motor == 5) Motor.C.stop();
				  }
				  
				  // Display tach reading until Escape is pressed
				  while(!Button.ESCAPE.isPressed())
				  {
					  LCD.clear();
					  LCD.drawString(mot,0,0);
					  LCD.drawString(mots, 0, 2);
					  LCD.drawString(motors[motor], 8,2);
					  LCD.drawString(tach, 0, 3);
					  if (motor == 0 || motor == 3 || motor == 4) 
						  LCD.drawInt(Motor.A.getTachoCount(), 8, 7, 3);
					  if (motor == 1 || motor == 3 || motor == 5)
						  LCD.drawInt(Motor.B.getTachoCount(), 8, 7, 3);
					  if (motor == 2 || motor == 4 || motor == 5)
						  LCD.drawInt(Motor.C.getTachoCount(), 8, 7, 3);
					  LCD.refresh();
					  Thread.sleep(100);
				  }
				  
				  Button.ESCAPE.waitForPressAndRelease();
				}
			}
		}
	}
}
