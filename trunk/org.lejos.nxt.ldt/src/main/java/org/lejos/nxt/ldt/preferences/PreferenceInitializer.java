package org.lejos.nxt.ldt.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * Class used to initialize default preference values.
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		// new DefaultScope() is the preferred way for Eclipse 3.6, deprecated since Eclipse 3.7
		IEclipsePreferences store = new DefaultScope().getNode(LeJOSPlugin.ID);
		store.put(PreferenceConstants.KEY_TARGET_BUS, PreferenceConstants.VAL_TARGET_BUS_BOTH);
		store.putBoolean(PreferenceConstants.KEY_TARGET_CONNECT_BY_ADDR, false);
		store.putBoolean(PreferenceConstants.KEY_TARGET_CONNECT_BY_NAME, false);
		
		store.putBoolean(PreferenceConstants.KEY_NORMAL_RUN_AFTER_UPLOAD, true);
		store.putBoolean(PreferenceConstants.KEY_NORMAL_LINK_VERBOSE, false);
		store.putBoolean(PreferenceConstants.KEY_NORMAL_START_CONSOLE, false);
		
		store.putBoolean(PreferenceConstants.KEY_DEBUG_RUN_AFTER_UPLOAD, true);
		store.putBoolean(PreferenceConstants.KEY_DEBUG_LINK_VERBOSE, false);
		store.putBoolean(PreferenceConstants.KEY_DEBUG_START_CONSOLE, false);
		store.put(PreferenceConstants.KEY_DEBUG_MONITOR_TYPE, PreferenceConstants.VAL_DEBUG_TYPE_NORMAL);
		
		// use value of NXJ_HOME by default
		String nxjHome = System.getenv("NXJ_HOME");
		if (nxjHome != null)
			store.put(PreferenceConstants.KEY_NXJ_HOME, nxjHome);
		
		store.putBoolean(PreferenceConstants.KEY_SEPARATE_JVM, LeJOSNXJUtil.isWindows()
				|| LeJOSNXJUtil.isOSX());
	}

}
