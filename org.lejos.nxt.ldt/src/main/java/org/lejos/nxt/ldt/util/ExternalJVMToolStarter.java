package org.lejos.nxt.ldt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.lejos.nxt.ldt.LeJOSPlugin;

public class ExternalJVMToolStarter implements ToolStarter {

	private File nxjHome;

	public ExternalJVMToolStarter(File nxjHome)
	{
		this.nxjHome = nxjHome;
	}
	
	public Process createProcess(String tool, List<String> args)
			throws LeJOSNXJException {
		ArrayList<File> pccp = new ArrayList<File>();
		LeJOSNXJUtil.buildPCClasspath(nxjHome, pccp);
		
		String javaHome = System.getProperty("java.home");
		if (javaHome == null)
			throw new LeJOSNXJException("java.home property is not set");
		File f = new File(javaHome);
		if (!f.isDirectory())
			throw new LeJOSNXJException("java.home property is not a directory");
		
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.startsWith("windows "))
			f = new File(f, "bin/java.exe");
		else
			f = new File(f, "bin/java");
		
		if (!f.isFile())
			throw new LeJOSNXJException(f.getAbsolutePath()+ " does not exist");
		
		ArrayList<String> args2 = new ArrayList<String>();
		args2.add(f.getAbsolutePath());
		if (osName.startsWith("mac os x"))
			args2.add("-d32");
		args2.add("-Dnxj.home="+nxjHome.getAbsolutePath());
		args2.add("-classpath");
		args2.add(LeJOSNXJUtil.getClasspathString(pccp));
		args2.add(tool);
		args2.addAll(args);
		
		try {
			return LeJOSNXJUtil.exec(args2);
		} catch (IOException e) {
			throw new LeJOSNXJException("Failed to start external JVM", e);
		}
	}

	public int invokeTool(String tool, List<String> args) throws Exception, InvocationTargetException
	{
		LeJOSPlugin p2 = LeJOSPlugin.getDefault();
		Writer consw = p2.getConsoleWriter();
		
		Process t = createProcess(tool, args);
		
		t.getOutputStream().close();		
		new PipeThread(new InputStreamReader(t.getInputStream()), consw).start();
		new PipeThread(new InputStreamReader(t.getErrorStream()), consw).start();
		//TODO join with threads
		
		try {
			return t.waitFor();
		} catch (InterruptedException e) {
			t.destroy();
			
			Thread.currentThread().interrupt();
			throw new LeJOSNXJException("interrupted while waiting for tool to end", e);
		}
	}

	public int invokeSwingTool(String tool, List<String> args) throws Exception, InvocationTargetException
	{
		Process t = createProcess(tool, args);
		t.getOutputStream().close();
		t.getInputStream().close();
		t.getErrorStream().close();
		
		//TODO what to return? How to check that program actually started?
		return 0;
	}
	
	public boolean isUp2Date()
	{
		return true;
	}

	public File getNxjHome()
	{
		return this.nxjHome;
	}

}
