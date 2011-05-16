package lejos.nxt;

/**
 * This class is designed for use by other lejos classes to
 * read persistent settings. User programs should use the Settings class
 * 
 * @author Lawrie Griffiths
 *
 */
public class SystemSettings {

	private static final int SETTINGS_PAGE = 0; 
	private static final int MAX_SETTING_SIZE = 16; 	
	private static byte[] buf = new byte[256];
	private static final String version = "NXJ Settings 1.0"; 
	private static final String versionName = "settings.version"; 
	
	// Add to this String array to define new persistent
	// settings. There is a maximum of 16 including the version.
	private static final String[] names = {
		versionName, "lejos.volume", "lejos.default_program", "lejos.keyclick_volume",
		"lejos.default_autoRun", "lejos.sleep_time", "lejos.usb_serno", "lejos.usb_name",
		"lejos.bluetooth_pin"
	};
	
	/**
	 * Read the settings page
	 */
	static {
		Flash.readPage(buf, SETTINGS_PAGE);
		// Intialize page to all zeros and set version in slot 0,
		// if settings not already set up.
		if (!getValue(0).equals(version)) {
			for(int i=0;i<Flash.BYTES_PER_PAGE;i++) buf[i] = 0;
			setSetting(versionName,version);
		};
	}
	
	/**
	 * Get the slot number where a setting is stored
	 * 
	 * @param key the setting name
	 * @return the slot number (0 - 15)
	 *
	 */
	static int getSlot(String key)
	{
		for(int i= 0;i<names.length;i++) {
			if (names[i].equals(key)) return i;
		}
		return -1;
	}
	
	/**
	 * Write the String value of a setting to a slot
	 * 
	 * @param slot the slot (0 - 15)
	 * @param value the String value
	 */
	static void setSlot(int slot, String value) {
		int len = value.length();
		if (len > MAX_SETTING_SIZE)
			throw new IllegalArgumentException("value too large");
		
		for(int i=0;i<MAX_SETTING_SIZE;i++)
			buf[slot*MAX_SETTING_SIZE+i] = 0;

		for(int i=0;i<len;i++)
			buf[slot*MAX_SETTING_SIZE+i] = (byte)value.charAt(i);
	}
	
	/**
	 * Get the String value from a slot
	 * 
	 * @param slot the slot number
	 * @return the contents of the slot as a String
	 */
	static String getValue(int slot) {
		int l = 0;
		for(int i=0;i<MAX_SETTING_SIZE && buf[slot*MAX_SETTING_SIZE+i] != 0;i++) l++;
		char[] chars = new char[l];
		for(int i=0;i<l;i++) chars[i] = (char) buf[slot*MAX_SETTING_SIZE+i];
		return new String(chars);
	}
	
	/**
	 * Get the value for a leJOS NXJ persistent setting as a String
	 * 
	 * @param key the name of the setting
	 * @param defaultValue the default value
	 * @return the value
	 */
	public static String getStringSetting(String key, String defaultValue) {
		int slot = getSlot(key);
		if (slot < 0) return defaultValue;
		else {
			String s = getValue(slot);
			if (s.length() == 0) return defaultValue;
			else return s;
		}

	}
	
	/**
	 * Get the value for a leJOS NXJ persistent setting as an Integer
	 * 
	 * @param key the name of the setting
	 * @param defaultValue the default value
	 * @return the value
	 */
	public static int getIntSetting(String key, int defaultValue) {
		String s = getStringSetting(key, null);
		if (s == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}
	
	/**
	 * Set a leJOS NXJ persistent setting. 
	 * 
	 * @param key the name of the setting
	 * @param value the value to set it to
	 */
	static void setSetting(String key, String value) {
		int slot = getSlot(key);
		if (slot < 0)
			throw new IllegalArgumentException("unsupported key");
		
		setSlot(slot, value);
		Flash.writePage(buf, SETTINGS_PAGE);
	}
	
	/**
	 * Get the names of the the leJOS NXJ Settings
	 * 
	 * @return a String array of the names
	 */
	static String[] getSettingNames() {
		return names;
	}
}
