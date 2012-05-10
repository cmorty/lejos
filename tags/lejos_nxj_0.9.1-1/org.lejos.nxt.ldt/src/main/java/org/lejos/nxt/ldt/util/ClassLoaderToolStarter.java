package org.lejos.nxt.ldt.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.console.IOConsole;
import org.lejos.nxt.ldt.LeJOSPlugin;

public class ClassLoaderToolStarter implements ToolStarter
{
	private static void initializeSystemContext(ClassLoader cl, File nxjHome) throws LeJOSNXJException
	{
		LeJOSPlugin p = LeJOSPlugin.getDefault();
		IOConsole con = p.getConsole();
		Writer consw = p.getConsoleWriter();
//		OutputStream cons = con.newOutputStream();
//		OutputStreamWriter consw;
		InputStream cins = con.getInputStream();
		InputStreamReader cinsr;
		try
		{
//			consw = new OutputStreamWriter(cons, CONSOLE_CHARSET);
			cinsr = new InputStreamReader(cins, LeJOSPlugin.CONSOLE_CHARSET);
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new RuntimeException(e1);
		}
		
		try
		{
			Class<?> c = cl.loadClass("lejos.pc.comm.SystemContext");
			Method m;
			
			m = c.getDeclaredMethod("setNxjHome", String.class);
			m.invoke(null, nxjHome.getAbsolutePath());
			
			m = c.getDeclaredMethod("setOut", Writer.class);
			m.invoke(null, consw);
			
			m = c.getDeclaredMethod("setErr", Writer.class);
			m.invoke(null, consw);
			
			m = c.getDeclaredMethod("setIn", Reader.class);
			m.invoke(null, cinsr);
		}
		catch (Exception e)
		{
			Throwable t = e;
			if (t instanceof InvocationTargetException)
				t = ((InvocationTargetException)t).getTargetException();
			
			throw new LeJOSNXJException("unanble to initialize system context", t);
		}
	}

	private ClassLoader classloader;
	private File nxjHome;
	
	public ClassLoaderToolStarter(File nxjHome) throws LeJOSNXJException
	{
		LeJOSNXJUtil.message("Initializing LeJOS JDK at "+nxjHome);
		
		ArrayList<File> tmp = new ArrayList<File>();
		LeJOSNXJUtil.buildPCClasspath(nxjHome, tmp);
		URL[] urls = new URL[tmp.size()];
		int i = 0;
		for (File e : tmp)
		{
			try
			{
				urls[i++] = e.toURI().toURL();
			}
			catch (MalformedURLException e1)
			{
				throw new RuntimeException(e1);
			}
		}
		
		URLClassLoader cl = new URLClassLoader(urls);
		initializeSystemContext(cl, nxjHome);
		
		this.nxjHome = nxjHome;
		this.classloader = cl;		
	}

	public int invokeTool(String tool, List<String> args)	throws Exception, InvocationTargetException
	{
		String[] args2 = args.toArray(new String[args.size()]);
		
		Class<?> c = classloader.loadClass(tool);
		Method m = c.getDeclaredMethod("start", String[].class);
		Object r1 = m.invoke(null, (Object)args2);
		int r2 = ((Integer)r1).intValue();
		return r2;
	}

	public int invokeSwingTool(String tool, List<String> args) throws Exception, InvocationTargetException
	{
		//TODO move to swing thread
		return this.invokeTool(tool, args);
	}
	
	public boolean isUp2Date()
	{
		//TODO check timestamps of files
		return true;
	}

	public File getNxjHome()
	{
		return this.nxjHome;
	}

}
