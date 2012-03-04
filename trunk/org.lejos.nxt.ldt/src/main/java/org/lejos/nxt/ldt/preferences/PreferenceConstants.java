package org.lejos.nxt.ldt.preferences;

import org.eclipse.debug.core.ILaunchManager;
import org.lejos.nxt.ldt.launch.LaunchConstants;

/**
 * Constant definitions for plug-in preferences
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class PreferenceConstants {

	public static final String KEY_NXJ_HOME = "nxjHome";
	public static final String KEY_SEPARATE_JVM = "useSeparateJVM";
	
	public static final String KEY_TARGET_BUS = "connectionType";
	public static final String KEY_TARGET_CONNECT_BY_NAME = "ConnectToName";
	public static final String KEY_TARGET_CONNECT_BY_ADDR = "ConnectToAddress";
	public static final String KEY_TARGET_BRICK_NAME = "ConnectionName";
	public static final String KEY_TARGET_BRICK_ADDR = "ConnectionAddress";
	
	public static final String VAL_TARGET_BUS_BOTH = "ub";
	public static final String VAL_TARGET_BUS_USB = "u";
	public static final String VAL_TARGET_BUS_BT = "b";
	
	public static final String KEY_NORMAL_LINK_VERBOSE = ILaunchManager.RUN_MODE+LaunchConstants.SUFFIX_LINK_VERBOSE;
	public static final String KEY_NORMAL_RUN_AFTER_UPLOAD = ILaunchManager.RUN_MODE+LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD;
	public static final String KEY_NORMAL_START_CONSOLE = ILaunchManager.RUN_MODE+LaunchConstants.SUFFIX_START_CONSOLE;
	
	public static final String KEY_DEBUG_LINK_VERBOSE = ILaunchManager.DEBUG_MODE+LaunchConstants.SUFFIX_LINK_VERBOSE;
	public static final String KEY_DEBUG_RUN_AFTER_UPLOAD = ILaunchManager.DEBUG_MODE+LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD;
	public static final String KEY_DEBUG_START_CONSOLE = ILaunchManager.DEBUG_MODE+LaunchConstants.SUFFIX_START_CONSOLE;
	public static final String KEY_DEBUG_MONITOR_TYPE = ILaunchManager.DEBUG_MODE+LaunchConstants.SUFFIX_MONITOR_TYPE;
	
	public static final String VAL_DEBUG_TYPE_NORMAL = "normal";
	public static final String VAL_DEBUG_TYPE_RCONSOLE = "remote";
	public static final String VAL_DEBUG_TYPE_JDWP = "jdwp";
}
