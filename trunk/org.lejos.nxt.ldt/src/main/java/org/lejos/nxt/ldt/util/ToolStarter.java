package org.lejos.nxt.ldt.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ToolStarter {

	int invokeTool(String tool, List<String> args) throws Exception, InvocationTargetException;
	int invokeSwingTool(String tool, List<String> args) throws Exception, InvocationTargetException;

	boolean isUp2Date();
	
	File getNxjHome();
}
