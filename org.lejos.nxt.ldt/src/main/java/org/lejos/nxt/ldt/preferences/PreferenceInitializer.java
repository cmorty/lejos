package org.lejos.nxt.ldt.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
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
		IPreferenceStore store = LeJOSNXJPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_IS_VERBOSE, false);
		store.setDefault(PreferenceConstants.P_RUN_AFTER_DOWNLOAD, false);
		// store.setDefault(PreferenceConstants.P_CONNECTION_TYPE, "u");
	}

}
