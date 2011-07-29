package org.lejos.nxt.ldt.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class PipeThread extends Thread
{
	private Reader in;
	private Writer out;
	
	public PipeThread(Reader in, Writer out)
	{
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run()
	{
		try
		{
			char[] b = new char[4096];
			while (true)
			{
				int r = this.in.read(b);
				if (r < 0)
					break;
				
				this.out.write(b, 0, r);
			}		
		}
		catch (IOException e)
		{
			//TODO what to do?
			e.printStackTrace(System.err);
		}
	}
}
