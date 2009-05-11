package lejos.io;

import java.io.OutputStream;

/**
 * @author Sven Köhler
 */
public class Latin1OutputStreamWriter extends AbstractOutputStreamWriter
{
	public Latin1OutputStreamWriter(OutputStream os)
	{
		super(os);
	}
	
	@Override
	protected int getBytes(byte[] buf, int len, int cp)
	{
		buf[len] = (cp < 0 || cp > 0xFF) ? ERROR_CHAR : (byte)cp;
		return len+1;
	}	
}
