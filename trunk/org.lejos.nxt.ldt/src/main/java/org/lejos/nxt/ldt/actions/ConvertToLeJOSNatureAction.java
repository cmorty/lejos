package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.lejos.nxt.ldt.builder.leJOSNature;
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
		setLeJOSNature();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		ArrayList<IJavaProject> list = new ArrayList<IJavaProject>();
		LeJOSNXJUtil.getJavaProjectFromSelection(selection, list);
		action.setEnabled(!list.isEmpty());
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
	private void setLeJOSNature() {
		ArrayList<IJavaProject> list = new ArrayList<IJavaProject>();
		LeJOSNXJUtil.getJavaProjectFromSelection(selection, list);
		for (IJavaProject project : list)
		{
			try {
				IProjectDescription description = project.getProject().getDescription();
				String[] natures = description.getNatureIds();
	
				LinkedHashSet<String> newNatures = new LinkedHashSet<String>();
				for (String e : natures)
					newNatures.add(e);
				
				newNatures.add(leJOSNature.NATURE_ID);
				String[] tmp = new String[newNatures.size()];
				newNatures.toArray(tmp);
				description.setNatureIds(tmp);
				project.getProject().setDescription(description, null);
	
				// update classpath
				updateClasspath(project);
				// set "compliance 1.5" option
	//			project.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
	//					JavaCore.VERSION_1_5);
	//			project.setOption(JavaCore.COMPILER_COMPLIANCE,
	//					JavaCore.VERSION_1_5);
	//			project.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
				
				// log
				LeJOSNXJUtil.message("project " + project.getProject().getName()
						+ " now is a leJOS NXJ project");
			} catch (Throwable t) {
				// log
				LeJOSNXJUtil.message("project " + project.getProject().getName()+" was not converted.", t);
			}
		}
	}

	/**
	 * update the project's classpath with additional leJOS libraries.
	 * 
	 * @param aProject
	 *            a java project
	 */
	private void updateClasspath(IJavaProject project) throws JavaModelException {
		// TODO set source attachement of classes.jar
		
		try {
			File nxjHome = LeJOSNXJUtil.getNXJHome();
			ArrayList<File> tmp = new ArrayList<File>();
			LeJOSNXJUtil.buildNXTClasspath(nxjHome, tmp);
			LinkedHashSet<Path> nxjFiles = new LinkedHashSet<Path>();
			for (File e : tmp)
				nxjFiles.add(new Path(e.getAbsolutePath()));
			
			// get existing classpath
			IClasspathEntry[] existingClasspath = project.getRawClasspath();
			// create new classpath with additional leJOS libraries last
			ArrayList<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
			for (IClasspathEntry cpEntry : existingClasspath) {
				if (cpEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER
						&& cpEntry.getPath().segment(0).equals("org.eclipse.jdt.launching.JRE_CONTAINER")) {
					// skip JRE/JDK
				} else if (nxjFiles.contains(cpEntry.getPath().makeAbsolute())) {
					// skip
				} else {
					// e.g. source container
					newClasspath.add(cpEntry);
				}
			}
			
			// add the other cp entries
			for (Path e : nxjFiles)
				newClasspath.add(JavaCore.newLibraryEntry(e, null, null));
			
			// set new classpath to project
			IClasspathEntry[] cpEntries = new IClasspathEntry[newClasspath.size()];
			newClasspath.toArray(cpEntries);
			project.setRawClasspath(cpEntries, null);
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.log(t);
		}
	}
}
