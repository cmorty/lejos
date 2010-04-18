package lejos.nxt.startup;

import lejos.nxt.Battery;
import lejos.nxt.LCD;

/**
 * Manage the top line of the display.
 * The top line of the display shows battery state, menu titles, and I/O
 * activity.
 */
public class BatteryIndicator
{
    // Battery state information
    private static final int STD_MIN = 6100;
    private static final int STD_OK = 6500;
    private static final int STD_MAX = 8000;
    private static final int RECHARGE_MIN = 7100;
    private static final int RECHARGE_OK = 7200;
    private static final int RECHARGE_MAX = 8200;
    
    private static final byte BATTERY_TOKEN_FULL = 0x3F;
    private static final byte BATTERY_TOKEN_EMPTY = 0x21;
    private static final byte BATTERY_TOKEN_NOB = 0x0C;
    
    private static final int BATTERY_REAL_WIDTH = Config.ICON_BATTERY_WIDTH -3;
    
    private int levelMin;
    private int levelOk;
    private int levelHigh;
    private boolean isOk;
    
	private volatile byte[] title;
	
    public BatteryIndicator()
    {
    	if (Battery.isRechargeable())
    	{
    		this.levelMin = RECHARGE_MIN;
    		this.levelOk = RECHARGE_OK;
    		this.levelHigh = RECHARGE_MAX;
    	}
    	else
    	{
    		this.levelMin = STD_MIN;
    		this.levelOk = STD_OK;
    		this.levelHigh = STD_MAX;
    	}
    }
    
    public void setTitle(String title)
    {
    	int len = title.length();        	
    	int blen = 0;
    	if (len > 0)
    		blen = len * (LCD.FONT_WIDTH + 1) - 1;
    	
    	byte[] b = new byte[blen];        	
    	byte[] f = LCD.getSystemFont();
    	
    	for (int i=0; i<len; i++)
    	{
    		char c = title.charAt(i);
    		int i1 = c * LCD.FONT_WIDTH;
    		int i2 = i * (LCD.FONT_WIDTH + 1);
    		
    		if (i1 < f.length)
    			System.arraycopy(f, i1, b, i2, LCD.FONT_WIDTH);
    	}

    	synchronized (this)
    	{
    		this.title = b;
    	}
    }

    /**
     * Display the battery icon.
     */
    public synchronized void draw(long time, byte[] buf)
    {
    	int level = Battery.getVoltageMilliVolt();
    	
        if (level <= levelMin)
        {
        	isOk = false;
        	level = levelMin;
        }
        if (level >= levelOk)
        	isOk = true;
        if (level > levelHigh)
        	level = levelHigh;

        int len = this.title.length;
        int x1, x2;
        if (len <= Config.TEXT_WIDTH)
        {
        	x1 = 0;
        	x2 = (Config.TEXT_WIDTH - len) / 2;
        }
        else
        {
        	int max = len - Config.TEXT_WIDTH;
        	int max2 = max + 2 * Config.TEXT_SCROLL_PAUSE;
        	x1 = (int)(time / Config.TEXT_SCROLL_DELAY % (2 * (max2 + 1)));
        	if (x1 > max2)
        		x1 = 2 * max2 + 1 - x1;
        	x1 -= Config.TEXT_SCROLL_PAUSE;
        	if (x1 > max)
        		x1 = max;
        	if (x1 < 0)
        		x1 = 0;
        	x2 = 0;
        	len = Config.TEXT_WIDTH;
        }            
        System.arraycopy(this.title, x1, buf, Config.TEXT_POS + x2, len);
        
        if (isOk || (time % (2*Config.ICON_BATTERY_BLINK)) < Config.ICON_BATTERY_BLINK)
        {
        	int width = (level - levelMin) * BATTERY_REAL_WIDTH / (levelHigh - levelMin);

        	int p = Config.ICON_BATTERY_POS;
        	buf[p++] = BATTERY_TOKEN_FULL;
        	for (int i=0; i<width; i++)
        		buf[p++] = BATTERY_TOKEN_FULL;
        	for (int i=width; i<BATTERY_REAL_WIDTH; i++)
        		buf[p++] = BATTERY_TOKEN_EMPTY;
        	buf[p++] = BATTERY_TOKEN_FULL;
        	buf[p] = BATTERY_TOKEN_NOB;
        }
    }
}