/**
 * Draws an Icon and an activity indicator.
 */
package lejos.nxt.startup;

public class IconIndicator implements ActivityIndicator
{
	//not static to avoid decoding in static initializer
	private final byte[] icon_data = Utils.stringToBytes8(Config.ICON_DATA);
	
	private int lcdX;
	private int iconX;
	private int iconWidth;
	private int count;
	private long lastPulse;
	
	public IconIndicator(int lcdX, int iconX, int iconWidth)
	{
		this.lcdX = lcdX;
		this.iconX = iconX;
		this.iconWidth = iconWidth;
	}
	
	public synchronized void setIconX(int iconX)
	{
		this.iconX = iconX;
	}
	
	public synchronized void pulse()
	{
		this.lastPulse = System.currentTimeMillis();
	}
	
	public synchronized void incCount()
	{
		this.count++;
	}
	
	public synchronized void decCount()
	{
		this.count--;
	}
	
	public synchronized void draw(long time, byte[] buf)
	{
		if (this.iconX >= 0)
		{
			System.arraycopy(icon_data, iconX, buf, lcdX, iconWidth);
		}
		else
		{
			for (int i=0; i<iconWidth; i++)
				buf[lcdX + i] = 0;
		}
		
		if (time - this.lastPulse < Config.ACTIVITY_TIMEOUT || this.count > 0)
		{
			// ScanLine
			//int tick = (int)((time / Config.ANIM_DELAY) % 7);
			//int mask = ~(1 << tick);
			//for (int i=0; i<iconWidth; i++)
			//	buf[lcdX + i] &= mask; 
			
			// ScanLine 2
			//int tick = (int)((time / Config.ANIM_DELAY) % 12);
			//if (tick > 6)
			//	tick = 12 - tick;
			//int mask = ~(1 << tick);
			//for (int i=0; i<iconWidth; i++)
			//	buf[lcdX + i] &= mask; 
			
			// ScanLine 2 (thick)
			//int tick = (int)((time / Config.ANIM_DELAY) % 10);
			//if (tick > 5)
			//	tick = 10 - tick;
			//int mask = ~(3 << tick);
			//for (int i=0; i<iconWidth; i++)
			//	buf[lcdX + i] &= mask; 
			
			// ScanLine 3 (thick)
			//int tick = (int)((time / Config.ANIM_DELAY) % 12);
			//if (tick > 6)
			//	tick = 12 - tick;
			//int mask1 = 1 << tick;
			//int mask2 = ~(5 << tick) >> 1;
			//for (int i=0; i<iconWidth; i++)
			//{
			//	buf[lcdX + i] |= mask1;
			//	buf[lcdX + i] &= mask2;
			//}
			
			// Progress Bar
			//int tick = (int)((time / Config.ANIM_DELAY) % iconWidth);
			//for (int i=0; i<tick; i++)
			//	buf[lcdX + i] |= 0x80;
			
			// Token 
			int tw = iconWidth / 2;
			int tick = (int)((time / Config.ANIM_DELAY) % iconWidth);
			for (int i=0; i<tw; i++)
				buf[lcdX + (tick + i) % iconWidth] |= 0x80;
		}
	}
}
