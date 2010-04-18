/**
 * 
 */
package lejos.nxt.startup;

class IconIndicator implements ActivityIndictaor
{
	private static final int PULSE_TIMEOUT = 1000;
	
	private int lcdX;
	private int iconX;
	private int iconWidth;
	private long lastPulse;
	private int count;
	
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
			System.arraycopy(Config.ICON_DATA, iconX, buf, lcdX, iconWidth);
		
		if (time - this.lastPulse < PULSE_TIMEOUT || this.count > 0)
		{
			// ScanLine
			// int tick = (int)((time / 250) % 7);
			// int mask = ~(1 << tick);
			// for (int i=0; i<iconWidth; i++)
			// 	buf[lcdX + i] &= mask; 
			
			// Progress Bar
			// int tick = (int)((time / 250) % iconWidth);
			// for (int i=0; i<tick; i++)
			// 	buf[lcdX + i] |= 0x80;
			
			// Token
			int tw = iconWidth / 2;
			int tick = (int)((time / 250) % iconWidth);
			for (int i=0; i<tw; i++)
				buf[lcdX + (tick + i) % iconWidth] |= 0x80;
		}
	}
}