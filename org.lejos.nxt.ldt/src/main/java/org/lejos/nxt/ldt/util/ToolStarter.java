package org.lejos.nxt.ldt.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ToolStarter {

	int invokeTool(File nxjHome, String tool, List<String> args) throws Exception, InvocationTargetException;

	boolean isUp2Date();
	
	File getNxjHome();
}
