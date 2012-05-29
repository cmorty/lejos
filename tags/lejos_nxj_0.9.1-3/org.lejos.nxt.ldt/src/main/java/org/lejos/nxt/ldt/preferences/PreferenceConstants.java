package org.lejos.nxt.ldt.preferences;

import org.eclipse.debug.core.ILaunchManager;

/**
 * Constant definitions for plug-in preferences
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class PreferenceConstants {

	public static final String KEY_NXJ_HOME = "nxjHome";
	public static final String KEY_SEPARATE_JVM = "useSeparateJVM";
	public static final String KEY_CONNECTION_TYPE = "connectionType";
	public static final String KEY_CONNECT_TO_NAMED_BRICK = "ConnectToName";
	public static final String KEY_CONNECT_TO_BRICK_ADDRESS = "ConnectToAddress";
	public static final String KEY_CONNECTION_BRICK_NAME = "ConnectionName";
	public static final String KEY_CONNECTION_BRICK_ADDRESS = "ConnectionAddress";
	
	public static final String KEY_NORMAL_LINK_VERBOSE = ILaunchManager.RUN_MODE+".linkVerbose";
	public static final String KEY_NORMAL_RUN_AFTER_UPLOAD = ILaunchManager.RUN_MODE+".runAfterDownload";
	public static final String KEY_NORMAL_START_CONSOLE = ILaunchManager.RUN_MODE+".startConsole";
	
	public static final String KEY_DEBUG_LINK_VERBOSE = ILaunchManager.DEBUG_MODE+".linkVerbose";
	public static final String KEY_DEBUG_RUN_AFTER_UPLOAD = ILaunchManager.DEBUG_MODE+".runAfterDownload";
	public static final String KEY_DEBUG_START_CONSOLE = ILaunchManager.DEBUG_MODE+".startConsole";
	public static final String KEY_DEBUG_MONITOR_TYPE = ILaunchManager.DEBUG_MODE+".monitorType";
	
	public static final String VAL_DEBUG_TYPE_NORMAL = "normal";
	public static final String VAL_DEBUG_TYPE_REMOTE = "remote";
	public static final String VAL_CONNECTION_TYPE_BOTH = "ub";
	public static final String VAL_CONNECTION_TYPE_USB = "u";
	public static final String VAL_CONNECTION_TYPE_BLUETOOTH = "b";
}
