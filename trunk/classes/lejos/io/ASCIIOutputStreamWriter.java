package lejos.io;

import java.io.OutputStream;

public class ASCIIOutputStreamWriter extends SingleByteOutputStreamWriter
{
	public ASCIIOutputStreamWriter(OutputStream os)
	{
		super(os);
	}
	
	@Override
	protected byte getByte(int c)
	{
		if (c < 0 || c > 0x7F)
			c = '?';
		
		return (byte)c;
	}	
}
