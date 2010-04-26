package lejos.nxt.startup;

import lejos.nxt.LCD;

public class Utils
{
	public static byte[] stringToBytes(String str)
	{
		int len = str.length();
		byte[] r = new byte[len];
		for (int i = 0; i < len; i++)
			r[i] = (byte) str.charAt(i);
		return r;
	}

	public static byte[] textToBytes(String text)
	{
		int len = text.length();
		int blen = 0;
		if (len > 0)
			blen = len * (LCD.FONT_WIDTH + 1) - 1;

		byte[] b = new byte[blen];
		byte[] f = LCD.getSystemFont();

		for (int i = 0; i < len; i++)
		{
			char c = text.charAt(i);
			int i1 = c * LCD.FONT_WIDTH;
			int i2 = i * (LCD.FONT_WIDTH + 1);

			if (i1 < f.length)
				System.arraycopy(f, i1, b, i2, LCD.FONT_WIDTH);
		}

		return b;
	}

	public static String versionToString(int version)
	{
		//very short byte code
		return new StringBuilder().append(version >>> 16).append('.').append((version >>> 8) & 0xFF).append('.')
				.append(version & 0xFF).toString();
	}
}
