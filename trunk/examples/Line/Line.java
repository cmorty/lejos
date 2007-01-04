import lejos.nxt.*;

public class Line 
{	
	public static void main (String[] aArg)
	throws Exception
	{
		int sweep, tacho;
		final int BLACK = 40;
		boolean clockwise = true, white;
		String l = "Light:";
		String p = "Press ENTER";
		int db = 0;
    
		LightSensor light = new LightSensor(Port.S1);
		SoundSensor sound = new SoundSensor(Port.S2);
		
        LCD.clear();
        LCD.drawString(p, 0, 0);
        LCD.refresh();
        
        // wait for ENTER
		while (!Button.ENTER.isPressed());
		
		Motor.A.regulateSpeed(true);
		Motor.A.regulateSpeed(true);
		
        
        // Finish when ESCAPE is pressed or shouted at
        while(!Button.ESCAPE.isPressed() ||
        		sound.readValue() > 50)
        {
        	
            Motor.A.setSpeed(200);
            Motor.C.setSpeed(200);

        	// Go forward 30       	
      	
    		Motor.A.rotate(30);
    		Motor.C.rotate(30);     	
	        
    		while(Motor.A.isRotating() || Motor.C.isRotating());
	        
	        for(int i=0;i<200;i++)
	        {
	        	db = sound.readValue();
	        	if (db > 50) break;
	        	Thread.sleep(1);
	        }
	        
	        if (db > 50) break;

			clockwise = true;
			sweep = 10;
			white = false;
			
			do  
			{		 				
		        LCD.clear();
		        LCD.drawString(l, 0, 0);
			    LCD.drawInt(light.readValue(),7,0);
		        LCD.refresh();
		        
		        Motor.A.setSpeed(50);
		        Motor.C.setSpeed(50);
		        
				if (clockwise)
				{
					Motor.A.backward();
					tacho = Motor.C.getTachoCount();
					Motor.C.forward();
					
					// Turn by the amount specified by sweep
					while (Motor.C.getTachoCount() < tacho + sweep
							&& (white = (light.readValue() > BLACK)));
				}
				else
				{
					Motor.C.backward();
					tacho = Motor.A.getTachoCount();
					Motor.A.forward();
					
					while (Motor.A.getTachoCount() < tacho + sweep
							&& (white = (light.readValue() > BLACK)));
				}
				
				// Reverse the direction and double the sweep
				clockwise = !clockwise;
				sweep *= 2;
			}
			while(white && !Button.ESCAPE.isPressed());
			
		}
        // Tidy up
        Motor.A.stop();
        Motor.C.stop();
        LCD.clear();
        LCD.drawString("Finished",3,4);
        LCD.refresh();
	}
}
