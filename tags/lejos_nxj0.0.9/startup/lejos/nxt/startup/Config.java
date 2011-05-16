package lejos.nxt.startup;

import lejos.nxt.LCD;

public class Config
{
	public static final int MIN_CONTRAST = 20;
	public static final int MAX_CONTRAST = 0x60;
	
	public static final int ANIM_DELAY = 250;
	public static final int ACTIVITY_TIMEOUT = 1000;

	public static final int ICON_DISABLE_X = -1;

	public static final int ICON_BT_WIDTH = 7;
	public static final int ICON_BT_HIDDEN_X = 11;
	public static final int ICON_BT_VISIBLE_X = 18;
	public static final int ICON_BT_POS = LCD.SCREEN_WIDTH - ICON_BT_WIDTH;

	public static final int ICON_USB_X = 0;
	public static final int ICON_USB_WIDTH = 11;
	public static final int ICON_USB_POS = ICON_BT_POS - ICON_USB_WIDTH - 1;

	public static final int ICON_BATTERY_POS = 0;
	public static final int ICON_BATTERY_WIDTH = 12;
	public static final int ICON_BATTERY_BLINK = 4 * Config.ANIM_DELAY;

	public static final int TEXT_POS = ICON_BATTERY_POS + ICON_BATTERY_WIDTH + 2;
	public static final int TEXT_WIDTH = ICON_USB_POS - TEXT_POS - 2;
	public static final int TEXT_SCROLL_DELAY = ANIM_DELAY;
	public static final int TEXT_SCROLL_PAUSE = 5;

	public static final String ICON_DATA = "\u003E\u0020\u003E\0\u002E\u002A\u003A\0\u003E\u002A\u0014\0\u0014\u0008\u003E\u002A\u0014\0\u007F\u006B\u0077\u0041\u0055\u006B\u007F";
	
	public static final int LOGO_WIDTH = 26;
	public static final int LOGO_HEIGHT = 32;
	
	public static final int LOGO_TEXT_SEP = 4;
	
	public static final String LOGO_DATA = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\u003F\u003F\u003F\u003F\u003F\u003F\u00FF\u00FF\u00FF\u00FF\u00FE\u00FC"
			+ "\0\0\0\0\0\0\0\0\0\u00A0\u0040\u00A0\u0040\u00A0\u0040\u00A0\0\0\0\0\u00FF\u00FF\u00FF\u00FF\u00FF\u00FF"
			+ "\u00F0\u00F0\u00F0\u00F0\u00F0\u00F0\0\0\0\n\u0005\n\u0005\n\u0005\n\0\0\0\0\u00FF\u00FF\u00FF\u00FF\u00FF\u00FF"
			+ "\u000F\u001F\u003F\u003F\u007F\u007F\u00FC\u00F8\u00F0\u00F0\u00F0\u00F0\u00F0"
			+ "\u00F0\u00F0\u00F0\u00F0\u00F0\u00F8\u00FC\u007F\u007F\u003F\u003F\u001F\u000F";
}
