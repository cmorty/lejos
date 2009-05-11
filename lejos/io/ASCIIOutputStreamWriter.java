package lejos.io;

import java.io.OutputStream;

/**
 * @author Sven Köhler
 */
public class ASCIIOutputStreamWriter extends AbstractOutputStreamWriter
{
	public ASCIIOutputStreamWriter(OutputStream os)
	{
		super(os);
	}
	
	@Override
	protected int getBytes(byte[] buf, int len, int cp)
	{
		buf[len] = (cp < 0 || cp > 0x7F) ? ERROR_CHAR : (byte)cp;
		return len+1;
	}	
}
