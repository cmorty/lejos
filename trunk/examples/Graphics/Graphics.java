import lejos.nxt.*;

public class Graphics 
{
	static int[] buff;
	
	public static void main (String[] aArg)
	throws Exception
	{
		buff = new int[200];
		
		SoundSensor sound = new SoundSensor(Port.S2);
		
		for(int i = 0; !Button.ESCAPE.isPressed();i++)
		{
			setPixel(i % 100, 7 + (sound.readValue()/2));
			LCD.setDisplay(buff);
			LCD.refresh();
			Thread.sleep(10);
			if (i % 100 == 0) clear();
		}
		Button.ESCAPE.waitForPressAndRelease();
	}
	
	public static void setPixel(int x, int y)
	{
		y = 63 - y;
		buff[(x>>2) + ((y>>3) * 25)] |= 1 << ((x<<3) + (y&7));
	}
	
	public static void clear()
	{
		for(int i=0;i<200;i++) buff[i] = 0;
	}

}
