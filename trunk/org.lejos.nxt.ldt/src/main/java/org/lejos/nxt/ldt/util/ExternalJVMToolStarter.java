package org.lejos.nxt.ldt.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ExternalJVMToolStarter implements ToolStarter {

	private File nxjHome;

	public ExternalJVMToolStarter(File nxjHome)
	{
		this.nxjHome = nxjHome;
	}
	
	public int invokeTool(File nxjHome, String tool, List<String> args) throws Exception, InvocationTargetException
	{
		
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
