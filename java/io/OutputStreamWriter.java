package java.io;

import lejos.charset.CharsetEncoder;
import lejos.charset.Latin1Encoder;
import lejos.charset.UTF8Encoder;
import lejos.io.LejosOutputStreamWriter;

public class OutputStreamWriter extends LejosOutputStreamWriter
{
	private static final int BUFFERSIZE = 32;
	
	public OutputStreamWriter(OutputStream os)
	{
		super(os, new UTF8Encoder(), BUFFERSIZE);
	}
	
	public OutputStreamWriter(OutputStream os, String charset) throws UnsupportedEncodingException
	{
		super(os, getCoder(charset), BUFFERSIZE);
	}
	
	private static CharsetEncoder getCoder(String charset) throws UnsupportedEncodingException
	{
		//TODO use constants or something else
		charset = charset.toLowerCase();		
		if (charset.equals("iso-8859-1") || charset.equals("latin1"))			
			return new Latin1Encoder();
		if (charset.equals("utf-8") || charset.equals("utf8"))			
			return new UTF8Encoder();
		
		throw new UnsupportedEncodingException("unsupported encoding "+charset);
	}
}
