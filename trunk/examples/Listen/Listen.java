import lejos.nxt.*;

public class Listen implements PortListener
{
	String changed = "State changed";
	String val = "Value:";
	String oldVal = "old Value:";
	String free = "Free Mem:"; 
	SoundSensor sound = new SoundSensor(Port.S1);
	
	public static void main (String[] aArg)
	throws Exception
	{
		Listen listen = new Listen();
		listen.run();
		Button.ESCAPE.waitForPressAndRelease();
		LCD.clear();
		LCD.drawString("Finished", 3, 4);
		LCD.refresh();
	}
	
	public void stateChanged(Port port, int value, int oldValue)
    {
		if (port == Port.S1 && sound.readValue() > 50)
		{
	    	LCD.clear();
	    	LCD.drawString(changed,0,0);
	    	LCD.drawString(val, 0, 1);
	    	LCD.drawInt(value,7,1);
	    	LCD.drawInt(sound.readValue(), 12, 1);
	    	LCD.drawString(oldVal, 0, 2);
	    	LCD.drawInt(oldValue, 11, 2);
			LCD.drawString(free, 0, 4);
		    LCD.drawInt((int)(Runtime.getRuntime().freeMemory()),10,4);
	    	LCD.refresh();
		}
    }
	
	private void run()
	throws InterruptedException
	{
		Port.S1.addPortListener(this);
	}
}
