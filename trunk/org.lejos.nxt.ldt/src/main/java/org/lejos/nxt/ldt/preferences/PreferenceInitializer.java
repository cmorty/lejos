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
		IPreferenceStore store = LeJOSNXJPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.KEY_IS_VERBOSE, false);
		store.setDefault(PreferenceConstants.KEY_RUN_AFTER_UPLOAD, false);
		store.setDefault(PreferenceConstants.KEY_CONNECTION_TYPE, "u");
	}

}
