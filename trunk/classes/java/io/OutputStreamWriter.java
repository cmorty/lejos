package java.io;

import lejos.io.ASCIIOutputStreamWriter;
import lejos.io.Latin1OutputStreamWriter;
import lejos.io.UTF8OutputStreamWriter;

public class OutputStreamWriter extends Writer
{
	private final Writer w;
	
	public OutputStreamWriter(OutputStream os)
	{
		this.w = new UTF8OutputStreamWriter(os);
	}
	
	public OutputStreamWriter(OutputStream os, String charset) throws UnsupportedEncodingException
	{
		//FIXME check must ignore case
		//TODO use constants or something else
		if (charset.equals("us-ascii") || charset.equals("ascii"))
			this.w = new ASCIIOutputStreamWriter(os);
		else if (charset.equals("iso-8859-1") || charset.equals("latin1"))			
			this.w = new Latin1OutputStreamWriter(os);
		else if (charset.equals("utf-8") || charset.equals("utf8"))			
			this.w = new UTF8OutputStreamWriter(os);
		else
			throw new UnsupportedEncodingException("unsupported encoding "+charset);
	}
	
	@Override
	public Writer append(char c) throws IOException
	{
		this.w.append(c);
		return this;
	}

	@Override
	public Writer append(CharSequence str, int start, int end) throws IOException
	{
		this.w.append(str, start, end);
		return this;
	}

	@Override
	public void write(int c) throws IOException
	{
		this.w.write(c);
	}

	@Override
	public void write(char[] c, int off, int len) throws IOException
	{
		this.w.write(c, off, len);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		super.write(str, off, len);
	}

	@Override
	public void close() throws IOException
	{
		this.w.close();
	}

	@Override
	public void flush() throws IOException
	{
		this.w.flush();
	}
}
