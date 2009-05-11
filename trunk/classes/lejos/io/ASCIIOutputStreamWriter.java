package lejos.io;

import java.io.OutputStream;

/**
 * @author Sven Köhler
 */
public class ASCIIOutputStreamWriter extends AbstractSBCSOutputStreamWriter
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
