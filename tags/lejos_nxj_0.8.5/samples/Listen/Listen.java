import lejos.nxt.*;

/**
 * Test of sensor port listeners.
 * 
 * Note that these only work with Analog/Digital sensors
 * like the LEGO Touch, Sound and Light sensors.
 * 
 * They do not work with I2C sensors like the Ultrasonic sensor.
 * 
 * This test needs a LEGO Sound sensor connected to sensor port S1.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Listen implements SensorPortListener
{
	String changed = "State changed";
	String val = "Value:";
	String oldVal = "old Value:";
	String free = "Free Mem:"; 
	SoundSensor sound = new SoundSensor(SensorPort.S1);
	
	public static void main (String[] aArg)
	throws Exception
	{
		Listen listen = new Listen();
		listen.run();
		Button.ESCAPE.waitForPressAndRelease();
		LCD.clear();
		LCD.drawString("Finished", 3, 4);
		LCD.refresh();
		Thread.sleep(2000);
	}
	
	public void stateChanged(SensorPort port, int value, int oldValue)
    {
		if (port == SensorPort.S1 && sound.readValue() > 50)
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
		SensorPort.S1.addSensorPortListener(this);
	}
}
