package lejos.util;

public class EndianTools
{
	public static long beLong(byte[] b, int off)
	{
		return ((long) beInt(b, off) << 32) | (beInt(b, off + 4) & 0xFFFFFFFFL);
	}

	public static long beUInt(byte[] b, int off)
	{
		return beInt(b, off) & 0xFFFFFFFFL;
	}

	public static int beUShort(byte[] b, int off)
	{
		return beShort(b, off) & 0xFFFF;
	}

	public static int beInt(byte[] b, int off)
	{
		return (b[off] << 24) | ((b[off + 1] & 0xFF) << 16)
			| ((b[off + 2] & 0xFF) << 8) | (b[off + 3] & 0xFF);
	}

	public static short beShort(byte[] b, int off)
	{
		return (short) ((b[off] << 8) | (b[off + 1] & 0xFF));
	}

	public static long leLong(byte[] b, int off)
	{
		return (leInt(b, off) & 0xFFFFFFFFL) | ((long) leInt(b, off + 4) << 32);
	}

	public static long leUInt(byte[] b, int off)
	{
		return leInt(b, off) & 0xFFFFFFFFL;
	}

	public static int leUShort(byte[] b, int off)
	{
		return leShort(b, off) & 0xFFFF;
	}

	public static int leInt(byte[] b, int off)
	{
		return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8)
			| ((b[off + 2] & 0xFF) << 16) | (b[off + 3] << 24);
	}
	
	public static short leShort(byte[] b, int off)
	{
		return (short)((b[off] & 0xFF) | (b[off + 1] << 8));
	}
}
