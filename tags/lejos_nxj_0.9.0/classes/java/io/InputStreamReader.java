package java.io;

import lejos.charset.CharsetDecoder;
import lejos.charset.Latin1Decoder;
import lejos.charset.UTF8Decoder;
import lejos.io.LejosInputStreamReader;

public class InputStreamReader extends LejosInputStreamReader
{
	private static final int BUFFERSIZE = 32;
	
	public InputStreamReader(InputStream os)
	{
		super(os, new UTF8Decoder(), BUFFERSIZE);
	}
	
	public InputStreamReader(InputStream os, String charset) throws UnsupportedEncodingException
	{
		super(os, getCoder(charset), BUFFERSIZE);
	}
	
	private static CharsetDecoder getCoder(String charset) throws UnsupportedEncodingException
	{
		//TODO use constants or something else
		charset = charset.toLowerCase();		
		if (charset.equals("iso-8859-1") || charset.equals("latin1"))			
			return new Latin1Decoder();
		if (charset.equals("utf-8") || charset.equals("utf8"))			
			return new UTF8Decoder();
		
		throw new UnsupportedEncodingException("unsupported encoding "+charset);
	}
}
