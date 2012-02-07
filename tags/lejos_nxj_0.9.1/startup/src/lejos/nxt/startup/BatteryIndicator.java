package lejos.nxt.startup;

import lejos.nxt.Battery;

/**
 * Draws a battery and the name of the NXT.
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
    
	private static final int HISTORY_SIZE = 2000 / Config.ANIM_DELAY;
	private static final int WINDOW_TOLERANCE = 10;
	
    private static final byte BATTERY_TOKEN_FULL = 0x3F;
    private static final byte BATTERY_TOKEN_EMPTY = 0x21;
    private static final byte BATTERY_TOKEN_NOB = 0x0C;
    
    private static final int BATTERY_REAL_WIDTH = Config.ICON_BATTERY_WIDTH -3;
    
    private int levelMin;
    private int levelOk;
    private int levelHigh;
    private boolean isOk;
    
	private final int[] history = new int[HISTORY_SIZE];
	private int windowcenter;
	private int historyindex;
	private int historysum;
	
	private byte[] title;
	private byte[] default_title;
	private long title_time;
	
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
    	
    	int val = Battery.getVoltageMilliVolt();
		windowcenter = val;
		historysum = val * HISTORY_SIZE;
		for (int i = 0; i < HISTORY_SIZE; i++)
			history[i] = val;
    }
    
    public synchronized void setDefaultTitle(String title)
    {
    	byte[] o = this.default_title;
    	byte[] b = Utils.textToBytes(title);
    	this.default_title = b;
    	if (this.title == o)
    	{
    		this.title = b;
    		this.title_time = System.currentTimeMillis();
    	}
    }
    
    public synchronized void setTitle(String title)
    {
   		this.title = (title == null) ? default_title : Utils.textToBytes(title);
		this.title_time = System.currentTimeMillis();
    }
    
    private int getLevel()
    {
    	int val = Battery.getVoltageMilliVolt();
    	
		historysum += val - history[historyindex];
		history[historyindex] = val;
		historyindex = (historyindex + 1) % HISTORY_SIZE;
		int average = (historysum + HISTORY_SIZE/2) / HISTORY_SIZE;
		
		int diff = average - windowcenter;
		if (diff < -WINDOW_TOLERANCE || diff > WINDOW_TOLERANCE)
			windowcenter += diff / 2;
    	
		return windowcenter;
    }

    /**
     * Display the battery icon.
     */
    public synchronized void draw(long time, byte[] buf)
    {
    	int level = getLevel();
    	
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
        	
        	for (int i=0; i<x2; i++)
        		buf[Config.TEXT_POS + i]=0;
        	for (int i=x2+len; i<Config.TEXT_WIDTH; i++)
        		buf[Config.TEXT_POS + i]=0;
        }
        else
        {
        	int max = len - Config.TEXT_WIDTH;
        	int max2 = max + 2 * Config.TEXT_SCROLL_PAUSE;
        	x1 = (int)((time - title_time) / Config.TEXT_SCROLL_DELAY % (2 * max2 + 2));
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