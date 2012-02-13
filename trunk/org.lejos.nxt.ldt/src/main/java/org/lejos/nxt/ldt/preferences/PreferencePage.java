package org.lejos.nxt.ldt.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.container.LeJOSLibContainer;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog.
 * 
 * @author Matthias Paul Scholz
 */

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(FLAT);
		setPreferenceStore(LeJOSPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for leJOS NXJ");
	}
	
	private Composite newParent(Composite g, int cols)
	{
		GridData gd = new GridData();
		gd.horizontalSpan = cols;
		Composite p = new Composite(g, SWT.NONE);
		p.setFont(g.getFont());
		p.setLayoutData(gd);
		return p;
	}

	private Composite newParentF(Composite g, int cols)
	{
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = cols;
		Composite p = new Composite(g, SWT.NONE);
		p.setFont(g.getFont());
		p.setLayoutData(gd);
		return p;
	}

	public Group createGroup(Composite parent, int cols, String text) {
		GridLayout gl = new GridLayout(cols, false);
		gl.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		gl.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		gl.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		gl.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		Group g = new Group(parent, SWT.NONE);		
		g.setLayoutData(gd);
		g.setLayout(gl);
		g.setText(text);
		return g;
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		initializeDialogUnits(parent);

		// NXJ_HOME
		addField(new DirectoryFieldEditor(PreferenceConstants.KEY_NXJ_HOME,
				"&NXJ_HOME:", parent));
		
		Composite p2 = newParent(parent, 3);
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_SEPARATE_JVM,
				"&Run Tools in separate JVM", p2));
		
		Group g = createGroup(parent, 2, "Defaults for run configurations:");
		
		//TODO add option for skipping the upload
		// connection type
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.KEY_TARGET_BUS, "Connection type", 3,
				new String[][] {
						{ "B&oth", PreferenceConstants.VAL_TARGET_BUS_BOTH },
						{ "&USB", PreferenceConstants.VAL_TARGET_BUS_USB },
						{ "&Bluetooth",	PreferenceConstants.VAL_TARGET_BUS_BT },
				}, newParent(g, 2)));
		
		// connect to NXT address?
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_TARGET_CONNECT_BY_ADDR,
				"&Connect to address", newParent(g, 1)));
		addField(new StringFieldEditor(PreferenceConstants.KEY_TARGET_BRICK_ADDR,
				"&Address",	newParentF(g, 1)));
		// connect to named NXT?
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_TARGET_CONNECT_BY_NAME,
				"Connect to &named brick", newParent(g, 1)));
		addField(new StringFieldEditor(PreferenceConstants.KEY_TARGET_BRICK_NAME,
				"&Name", newParentF(g, 1)));
		
		g = createGroup(parent, 2, "Defaults for run mode");
		
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_NORMAL_RUN_AFTER_UPLOAD,
				"&Run program after upload", newParent(g, 1)));
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_NORMAL_LINK_VERBOSE,
				"Link &verbose", newParent(g, 1)));
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_NORMAL_START_CONSOLE,
				"Start nxj&console after upload (not functional yet)", newParent(g, 2)));
		
		g = createGroup(parent, 2, "Defaults for debug mode");
		
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_DEBUG_RUN_AFTER_UPLOAD,
				"&Run program after upload", newParent(g, 1)));
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_DEBUG_LINK_VERBOSE,
				"Link &verbose", newParent(g, 1)));
		addField(new BooleanFieldEditor(PreferenceConstants.KEY_DEBUG_START_CONSOLE,
				"Start nxj&console after upload (not functional yet)", newParent(g, 2)));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.KEY_DEBUG_MONITOR_TYPE, 
				"Debug Monitor", 2,	new String[][] {
						{ "Normal Debug Monitor", PreferenceConstants.VAL_DEBUG_TYPE_NORMAL },
						{ "Remote Debug Monitor", PreferenceConstants.VAL_DEBUG_TYPE_REMOTE },
				}, newParent(g, 2)));
	}

	@Override
	public boolean performOk() {
		boolean b = super.performOk();
		
		ClasspathContainerInitializer init = JavaCore.getClasspathContainerInitializer(LeJOSLibContainer.ID);
		Path p1 = new Path(LeJOSLibContainer.ID+"/"+LeJOSNXJUtil.LIBSUBDIR_NXT);
		Path p2 = new Path(LeJOSLibContainer.ID+"/"+LeJOSNXJUtil.LIBSUBDIR_PC);
		
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot wsr = ws.getRoot();
		IProject[] projects = wsr.getProjects();
		for (IProject p : projects)
		{
			try
			{
				if (p.isOpen() && p.isNatureEnabled(JavaCore.NATURE_ID))
				{
					IJavaProject jp = JavaCore.create(p);
					if (JavaCore.getClasspathContainer(p1, jp) != null)
						init.initialize(p1, jp);
					if (JavaCore.getClasspathContainer(p2, jp) != null)
						init.initialize(p2, jp);
				}
			}
			catch (Exception e)
			{
				LeJOSNXJUtil.log(e);
			}
		}
		
		return b;
	}

	public void init(IWorkbench workbench) {
		// do nothing
	}

}