package org.lejos.nxt.ldt.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. 
 */

public class leJOSNXJPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public leJOSNXJPreferencePage() {
		super(GRID);
		setPreferenceStore(LeJOSNXJPlugin.getDefault().getPreferenceStore());
		setDescription("preference page for leJOS NXJ");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.P_NXJ_HOME, 
				"&NXJ_HOME:", getFieldEditorParent()));
//		addField(
//			new BooleanFieldEditor(
//				PreferenceConstants.P_BOOLEAN,
//				"&An example of a boolean preference",
//				getFieldEditorParent()));
//
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_CONNECTION_TYPE,
			"&Connection type",
			1,
			new String[][] { { "&USB", "u" }, {
				"&Bluetooth", "b" }
		}, getFieldEditorParent()));
		
//		addField(
//			new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}