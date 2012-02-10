package org.lejos.nxt.ldt.launch;

import org.eclipse.debug.core.ILaunchManager;
import org.lejos.nxt.ldt.LeJOSPlugin;

public class LaunchConstants
{
	public static final String PREFIX = LeJOSPlugin.ID+".";
	
	public static final String SUFFIX_USE_DEFAULT = ".useDefaults";
	public static final String SUFFIX_LINK_VERBOSE = ".linkVerbose";
	public static final String SUFFIX_RUN_AFTER_UPLOAD = ".runAfterDownload";
	public static final String SUFFIX_START_CONSOLE = ".startConsole";
	public static final String SUFFIX_MONITOR_TYPE = ".monitorType";
	
	public static final String KEY_NORMAL_USE_DEFAULTS = PREFIX+ILaunchManager.RUN_MODE+SUFFIX_USE_DEFAULT;
	public static final String KEY_NORMAL_LINK_VERBOSE = PREFIX+ILaunchManager.RUN_MODE+SUFFIX_LINK_VERBOSE;
	public static final String KEY_NORMAL_RUN_AFTER_UPLOAD = PREFIX+ILaunchManager.RUN_MODE+SUFFIX_RUN_AFTER_UPLOAD;
	public static final String KEY_NORMAL_START_CONSOLE = PREFIX+ILaunchManager.RUN_MODE+SUFFIX_START_CONSOLE;
	
	public static final String KEY_DEBUG_USE_DEFAULTS = PREFIX+ILaunchManager.DEBUG_MODE+SUFFIX_USE_DEFAULT;
	public static final String KEY_DEBUG_LINK_VERBOSE = PREFIX+ILaunchManager.DEBUG_MODE+SUFFIX_LINK_VERBOSE;
	public static final String KEY_DEBUG_RUN_AFTER_UPLOAD = PREFIX+ILaunchManager.DEBUG_MODE+SUFFIX_RUN_AFTER_UPLOAD;
	public static final String KEY_DEBUG_START_CONSOLE = PREFIX+ILaunchManager.DEBUG_MODE+SUFFIX_START_CONSOLE;
	public static final String KEY_DEBUG_MONITOR_TYPE = PREFIX+ILaunchManager.DEBUG_MODE+SUFFIX_MONITOR_TYPE;
}
