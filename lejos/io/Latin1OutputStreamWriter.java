package lejos.io;

import java.io.OutputStream;

public class Latin1OutputStreamWriter extends SingleByteOutputStreamWriter
{
	public Latin1OutputStreamWriter(OutputStream os)
	{
		super(os);
	}
	
	@Override
	protected byte getByte(int c)
	{
		if (c < 0 || c > 0xFF)
			c = '?';
		
		return (byte)c;
	}	
}
