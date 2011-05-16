package org.lejos.nxt.ldt.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog.
 * 
 * @author Matthias Paul Scholz
 */

public class leJOSNXJPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public leJOSNXJPreferencePage() {
		super(GRID);
		setPreferenceStore(LeJOSNXJPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for leJOS NXJ");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		// NXJ_HOME
		addField(new DirectoryFieldEditor(PreferenceConstants.KEY_NXJ_HOME,
				"&NXJ_HOME:", getFieldEditorParent()));
		// connection type
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.KEY_CONNECTION_TYPE, "&Connection type", 1,
				new String[][] {
						{ "B&oth", PreferenceConstants.VAL_PROTOCOL_BOTH },
						{ "&USB", PreferenceConstants.VAL_PROTOCOL_USB },
						{ "&Bluetooth",	PreferenceConstants.VAL_PROTOCOL_BLUETOOTH } },
				getFieldEditorParent()));
		// run after download?
		addField(new BooleanFieldEditor(
				PreferenceConstants.KEY_RUN_AFTER_UPLOAD,
				"&Run program after upload", getFieldEditorParent()));
		// verbose?
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_IS_VERBOSE,
				"&Verbose", getFieldEditorParent()));
		// connect to NXT address?
		addField(new BooleanFieldEditor(
				PreferenceConstants.KEY_CONNECT_TO_BRICK_ADDRESS,
				"&Connect to address", getFieldEditorParent()));
		addField(new StringFieldEditor(
				PreferenceConstants.KEY_CONNECTION_BRICK_ADDRESS, "&Address",
				getFieldEditorParent()));
		// connect to named NXT?
		addField(new BooleanFieldEditor(
				PreferenceConstants.KEY_CONNECT_TO_NAMED_BRICK,
				"Connect to &named brick", getFieldEditorParent()));
		addField(new StringFieldEditor(
				PreferenceConstants.KEY_CONNECTION_BRICK_NAME, "&Name",
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}