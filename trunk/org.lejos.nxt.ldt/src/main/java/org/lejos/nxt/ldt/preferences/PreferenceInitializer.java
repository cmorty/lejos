package org.lejos.nxt.ldt.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;

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
	public void initializeDefaultPreferences() {
		IEclipsePreferences store = new DefaultScope().getNode(LeJOSNXJPlugin.ID);
		store.putBoolean(PreferenceConstants.KEY_IS_VERBOSE, false);
		store.putBoolean(PreferenceConstants.KEY_RUN_AFTER_UPLOAD, false);
		store.putBoolean(PreferenceConstants.KEY_CONNECT_TO_BRICK_ADDRESS, false);
		store.putBoolean(PreferenceConstants.KEY_CONNECT_TO_NAMED_BRICK, false);
		store.put(PreferenceConstants.KEY_CONNECTION_TYPE, PreferenceConstants.VAL_PROTOCOL_BOTH);
	}

}
