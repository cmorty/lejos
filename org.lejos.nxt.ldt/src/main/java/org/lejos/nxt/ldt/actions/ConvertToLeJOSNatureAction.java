package org.lejos.nxt.ldt.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.builder.leJOSNature;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * converts a Java project into a Java project with additional leJOS project
 * nature
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class ConvertToLeJOSNatureAction implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IJavaProject project = LeJOSNXJUtil
				.getJavaProjectFromSelection(selection);
		if (project != null) {
			setLeJOSNature(project);
		} else {
			// log
			LeJOSNXJUtil.message(new LeJOSNXJException(
					"no project selected or no Java project"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * set the leJOS nature on a project
	 * 
	 * @param project
	 */
	private void setLeJOSNature(IJavaProject project) {
		try {
			IProjectDescription description = project.getProject()
					.getDescription();
			String[] natures = description.getNatureIds();

			// nature already set?
			if (LeJOSNXJUtil.isLeJOSProject(project)) {
				LeJOSNXJUtil.message("project "
						+ project.getProject().getName()
						+ " already is a leJOS NXJ project");
				return;
			}

			// check setting of NXJ_HOME
			String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
					.getString(PreferenceConstants.P_NXJ_HOME);
			if ((nxjHome == null) || (nxjHome.isEmpty()))
				throw new LeJOSNXJException(
						"preference for NXJ_HOME is not set");
			// add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = leJOSNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.getProject().setDescription(description, null);
			// update classpath
			updateClasspath(project);
			// set "compliance 1.3" option
			project.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
					JavaCore.VERSION_1_1);
			project.setOption(JavaCore.COMPILER_COMPLIANCE,
					JavaCore.VERSION_1_3);
			project.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3);
			// log
			LeJOSNXJUtil.message("project " + project.getProject().getName()
					+ " now is a leJOS NXJ project");
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.message(t);
		}
	}

	/**
	 * update the project's classpath with additional leJOS libraries.
	 * 
	 * @param aProject
	 *            a java project
	 */
	private void updateClasspath(IJavaProject project)
			throws JavaModelException {
		// get existing classpath
		IClasspathEntry[] existingClasspath = project.getRawClasspath();
		// get NXJ classpath entries
		IClasspathEntry[] nxjEntries = getNXJClasspath();
		// create new classpath with additional leJOS libraries last
		List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>(
				existingClasspath.length + nxjEntries.length);
		for (int i = 0; i < existingClasspath.length; i++) {
			// filter out JRE_CONTAINER
			IClasspathEntry cpEntry = existingClasspath[i];
			if ((cpEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
					&& ((cpEntry.getPath().lastSegment())
							.indexOf("JRE_CONTAINER") >= 0)) {
				// skip JRE_CONTAINER, if container ends with JRE_CONTAINER
			} else {
				// e.g. source container
				newClasspath.add(existingClasspath[i]);
			}
		}
		// add the other cp entries
		for (int i = 0; i < nxjEntries.length; i++) {
			newClasspath.add(nxjEntries[i]);
		}
		IClasspathEntry[] cpEntries = (IClasspathEntry[]) newClasspath
				.toArray(new IClasspathEntry[0]);
		// set new classpath to project
		project.setRawClasspath(cpEntries, null);
	}

	private IClasspathEntry[] getNXJClasspath() {
		// TODO read classpath from preferences
		// get NXJ_HOME
		String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_NXJ_HOME);
		// create classpath entries
		IClasspathEntry[] entries = new IClasspathEntry[1];
		// classes jar
		IPath jar = new Path(nxjHome + "/lib/classes.jar");
		IClasspathEntry entry = JavaCore.newLibraryEntry(jar, null, new Path(
				"/"));
		entries[0] = entry;
		return entries;
	}
}
