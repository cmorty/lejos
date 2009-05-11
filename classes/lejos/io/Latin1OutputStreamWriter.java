package lejos.io;

import java.io.OutputStream;

/**
 * @author Sven Köhler
 */
public class Latin1OutputStreamWriter extends AbstractSBCSOutputStreamWriter
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
