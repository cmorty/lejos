package lejos.nxt.startup;

import lejos.nxt.LCD;

public class Config
{
	static final int ANIM_DELAY = 250;
	static final int ICON_DISABLE_X = -1;
	
	static final int ICON_BT_WIDTH = 7;
	static final int ICON_BT_HIDDEN_X = 11;
	static final int ICON_BT_VISIBLE_X = 18;
	static final int ICON_BT_POS = LCD.SCREEN_WIDTH - ICON_BT_WIDTH;
	
	static final int ICON_USB_X = 0;
	static final int ICON_USB_WIDTH = 11;
	static final int ICON_USB_POS = ICON_BT_POS - ICON_USB_WIDTH -1;
	
	static final int ICON_BATTERY_POS = 0;
	static final int ICON_BATTERY_WIDTH = 12;
    static final int ICON_BATTERY_BLINK = 4 * Config.ANIM_DELAY;
	
	static final int TEXT_POS = ICON_BATTERY_POS + ICON_BATTERY_WIDTH +2;
	static final int TEXT_WIDTH = ICON_USB_POS - TEXT_POS -2;
	static final int TEXT_SCROLL_DELAY = ANIM_DELAY;
	static final int TEXT_SCROLL_PAUSE = 5;
	
	static final byte[] ICON_DATA = Config.toBytes("\u003E\u0020\u003E\u0000\u002E\u002A\u003A\u0000\u003E\u002A\u0014\u0000\u0014\u0008\u003E\u002A\u0014\u0000\u007F\u006B\u0077\u0041\u0055\u006B\u007F");
	
	static byte[] toBytes (String str)
	{
		int len=str.length();
		byte[] r = new byte[len];
		for (int i=0; i<len; i++)
		  r[i] = (byte)str.charAt(i);
		return r;
	}
}
