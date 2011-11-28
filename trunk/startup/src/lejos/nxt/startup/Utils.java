package lejos.nxt.startup;

import java.io.File;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.util.Delay;

public class Utils
{
	public static byte[] stringToBytes8(String str)
	{
		int len = str.length();
		byte[] r = new byte[len];
		for (int i = 0; i < len; i++)
			r[i] = (byte) str.charAt(i);
		return r;
	}

	public static byte[] stringToBytes16(String str, int off, int len)
	{
		byte[] r = new byte[len << 1];
		for (int i = 0; i < len; i++)
		{
			char c = str.charAt(off + i);
			int j = i << 1;
			r[j] = (byte)(c >> 8);
			r[j+1] = (byte)c;
		}
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

	/**
	 * Make the LCD display fade into view.
	 */
	public static void fadeIn()
	{
	    for(int i = Config.MIN_CONTRAST; i < Config.MAX_CONTRAST; )
	    {
	        Delay.msDelay(5);
	        LCD.setContrast(++i);
	    }
	}

	/**
	 * Make the LCD display fade out of view.
	 */
	public static void fadeOut()
	{
	    for(int i = Config.MAX_CONTRAST; i > Config.MIN_CONTRAST; )
	    {
	        Delay.msDelay(5);
	        LCD.setContrast(--i);
	    }
	}

	public static void drawRect(int x, int y, int width, int height)
	{
		byte[] buf = LCD.getDisplay();    	
		for (int i=0; i<=width; i++)
		{
			Utils.setPixel(buf, x+i, y);
			Utils.setPixel(buf, x+i, y+height);
		}
		for (int j=1; j<height; j++)
		{
			Utils.setPixel(buf, x, y+j);
			Utils.setPixel(buf, x+width, y+j);
		}
	}

	public static void defragFilesystem()
	{
		try
		{
	        // Defrag the file system
			File.defrag();
		}
		catch (IOException ioe)
		{
			File.reset();
		}
	}

	private static void setPixel(byte[] buf, int x, int y)
	{
		x += (y >> 3) * LCD.SCREEN_WIDTH;
	    buf[x] |= 1 << (y & 0x7);
	}

	/**
	 * Return the extension part of a filename
	 * @param fileName
	 * @return the file extension
	 */
	public static String getExtension(String fileName)
	{
	    int dot = fileName.lastIndexOf(".");
	    if (dot < 0)
	        return "";
	
	    return fileName.substring(dot + 1, fileName.length());
	}

	/**
	 * Return the base part (no extension) of a filename
	 * @param fileName
	 * @return the base part of the name
	 */
	public static String getBaseName(String fileName)
	{
	    int dot = fileName.lastIndexOf(".");
	    if (dot < 0)
	        return fileName;
	
	    return fileName.substring(0, dot);
	}
}
