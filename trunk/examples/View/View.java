import lejos.nxt.*;

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
		String mots = "Motors:    ";
		String tach = "Tacho: ";
		String port = "Port:";
		String type = "Type:";
		String val = "Value:";
		String pressed = "pressed ";
		String released = "released";
		String blanks3 = "   ";
		
	
		Menu main = new Menu("View example");
		main.add("System");
		main.add("Sensors");
		main.add("Motors");
		main.add("Exit");
		
		Menu pickSensor = new Menu("Pick Sensor");
		pickSensor.add("Touch");
		pickSensor.add("Reflected");
		pickSensor.add("Ambient");
		pickSensor.add("Sound DB");
		pickSensor.add("Sound DBA");
		
		Menu pickSensorPort = new Menu("Pick Port");
		pickSensorPort.add("S1");
		pickSensorPort.add("S2");
		pickSensorPort.add("S3");
		pickSensorPort.add("S4");
		
		Menu pickMotor = new Menu("Pick Motor");
		pickMotor.add("A");
		pickMotor.add("B");
		pickMotor.add("C");
		pickMotor.add("A&B");
		pickMotor.add("A&C");
		pickMotor.add("B&C");
		
		Menu operation = new Menu("Pick option");
		operation.add("forward");
		operation.add("backward");
		operation.add("flt");
		operation.add("stop");
				
		int selection;
		
		for(;;)
		{
			selection = main.process();
			
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
				LCD.drawInt(Battery.getVoltageMilliVolt(), 10, 2);
				LCD.drawString(tot, 0, 3);
			    LCD.drawInt((int)(Runtime.getRuntime().totalMemory()),10, 3);
				LCD.drawString(free, 0, 4);
			    LCD.drawInt((int)(Runtime.getRuntime().freeMemory()),10,4);
			    LCD.refresh();
			    
			    Button.ESCAPE.waitForPressAndRelease();
			}
			
			if (selection == 1) // Sensors				
			{
				Thread.sleep(300);

				int portId = pickSensorPort.process();
				
				if (portId < 0) return;
				
				Thread.sleep(300);

				int sensor = pickSensor.process();
				
				if (sensor < 0) return; 
				
				LCD.clear();
				LCD.drawString(sensVal, 0, 0);
				
				LCD.drawString(port, 0, 2);
				LCD.drawString(pickSensorPort.getValue(),6,2);
				LCD.drawString(type, 0, 3);
				LCD.drawString(pickSensor.getValue(),6,3);
				LCD.drawString(val,0,4);
				
				if (sensor == 0)
				{
					TouchSensor touch = new TouchSensor(Port.PORTS[portId]);
					
					while (!Button.ESCAPE.isPressed())
					{
						if (touch.isPressed()) LCD.drawString(pressed,7,4);
						else LCD.drawString(released, 7, 4);
						
						LCD.refresh();
						Thread.sleep(500);
					}
				}
				
				if (sensor == 1 || sensor == 2)
				{
					LightSensor light = new LightSensor(Port.PORTS[portId], sensor == 1);
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawString(blanks3,7,4);
						LCD.drawInt(light.readValue(), 7, 4);
						
						LCD.refresh();
						Thread.sleep(500);
					}
				}

				if (sensor == 3 || sensor == 4)
				{
					SoundSensor sound = new SoundSensor(Port.PORTS[portId], sensor == 4);
					
					while (!Button.ESCAPE.isPressed())
					{
						LCD.drawString(blanks3,7,4);
						LCD.drawInt(sound.readValue(), 7, 4);
						
						LCD.refresh();
						Thread.sleep(100);
					}
				}	
				
				Thread.sleep(500);
			}
			
			if (selection == 2) // Motors
			{
				Thread.sleep(300);
				
				int motor = pickMotor.process();
				
				if (motor < 0) return;
				
				Thread.sleep(300);
				
				for(;;)
				{
				
				  int op = operation.process();
				  
				  if (op < 0)
				  {
					  Button.ESCAPE.waitForPressAndRelease();
					  break;
				  }
				
				  Motor.A.setSpeed(100);
				  Motor.B.setSpeed(100);
				  Motor.C.setSpeed(100);
				
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
				  
				  while(!Button.ESCAPE.isPressed())
				  {
					  LCD.clear();
					  LCD.drawString(mot,0,0);
					  LCD.drawString(mots, 0, 2);
					  LCD.drawString(pickMotor.getValue(), 8,2);
					  LCD.drawString(tach, 0, 3);
					  if (motor == 0 || motor == 3 || motor == 4) 
						  LCD.drawInt(Motor.A.getTachoCount(), 7, 3);
					  if (motor == 1 || motor == 3 || motor == 5)
						  LCD.drawInt(Motor.B.getTachoCount(), 7, 3);
					  if (motor == 2 || motor == 4 || motor == 5)
						  LCD.drawInt(Motor.C.getTachoCount(), 7, 3);
					  LCD.refresh();
					  Thread.sleep(500);
				  }
				  
				  Button.ESCAPE.waitForPressAndRelease();
				}
			}
		}
	}
}
