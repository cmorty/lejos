package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.ActionDelegate;
import org.lejos.nxt.ldt.LeJOSNature;
import org.lejos.nxt.ldt.container.LeJOSLibContainer;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * converts a Java project into a Java project with additional leJOS project
 * nature
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class ConvertToLeJOSProjectAction extends ActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		addLeJOSNature();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection newSelection) {
		this.selection = newSelection;
		ArrayList<IJavaProject> list = new ArrayList<IJavaProject>();
		LeJOSNXJUtil.getJavaProjectFromSelection(newSelection, list);
		action.setEnabled(!list.isEmpty());
	}

	/**
	 * set the leJOS nature on a project
	 * 
	 * @param project
	 */
	private void addLeJOSNature() {
		ArrayList<IJavaProject> list = new ArrayList<IJavaProject>();
		LeJOSNXJUtil.getJavaProjectFromSelection(selection, list);
		for (IJavaProject project : list)
		{
			IProject project2 = project.getProject();
			try {
				removeLeJOSNature(project2);
				addLeJOSNature(project2);
	
				// update classpath
				updateClasspath(project);
				// set "compliance 1.5" option
	//			project.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
	//					JavaCore.VERSION_1_5);
	//			project.setOption(JavaCore.COMPILER_COMPLIANCE,
	//					JavaCore.VERSION_1_5);
	//			project.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
				
				// log
				LeJOSNXJUtil.message("project " + project2.getName() + " now is a leJOS NXJ project");
			} catch (Throwable t) {
				// log
				LeJOSNXJUtil.error("project " + project2.getName()+" was not converted.", t);
			}
		}
	}

	public static void removeLeJOSNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();

		LinkedHashSet<String> newNatures = new LinkedHashSet<String>();
		newNatures.addAll(Arrays.asList(description.getNatureIds()));
		newNatures.remove(LeJOSNature.ID);
		
		String[] tmp = new String[newNatures.size()];
		newNatures.toArray(tmp);
		description.setNatureIds(tmp);
		project.setDescription(description, null);
	}

	public static void addLeJOSNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();

		LinkedHashSet<String> newNatures = new LinkedHashSet<String>();
		newNatures.add(LeJOSNature.ID);
		newNatures.addAll(Arrays.asList(description.getNatureIds()));
		
		String[] tmp = new String[newNatures.size()];
		newNatures.toArray(tmp);
		description.setNatureIds(tmp);
		project.setDescription(description, null);
	}

	/**
	 * update the project's classpath with additional leJOS libraries.
	 * 
	 * @param aProject
	 *            a java project
	 */
	private void updateClasspath(IJavaProject project) throws JavaModelException, LeJOSNXJException
	{
		File nxjHome = LeJOSNXJUtil.getNXJHome();
		ArrayList<File> tmp = new ArrayList<File>();
		LeJOSNXJUtil.buildNXTClasspath(nxjHome, tmp);
		LinkedHashSet<IPath> nxjFiles = new LinkedHashSet<IPath>();
		for (File e : tmp)
			nxjFiles.add(LeJOSNXJUtil.toPath(e));
		
		nxjFiles.add(LeJOSNXJUtil.toPath(new File(nxjHome, LeJOSNXJUtil.LIBDIR+"/classes.jar")));
		
		// create new classpath with additional leJOS libraries last
		ArrayList<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
		Path lcp = new Path(LeJOSLibContainer.ID+"/"+LeJOSNXJUtil.LIBSUBDIR_NXT);
		IClasspathEntry lc = JavaCore.newContainerEntry(lcp);
		
		// get existing classpath
		IClasspathEntry[] existingClasspath = project.getRawClasspath();
		for (IClasspathEntry cpEntry : existingClasspath) {
			boolean skip = false;
			boolean insertBefore = false;
			switch (cpEntry.getEntryKind())
			{
				case IClasspathEntry.CPE_CONTAINER:
					IPath p = cpEntry.getPath();
					if (p != null && p.segmentCount() > 0)
					{
						String s = p.segment(0);
						if (s.equals("org.eclipse.jdt.launching.JRE_CONTAINER")
								|| s.equals(LeJOSLibContainer.ID)) {
							// skip JRE/JDK and leJOS container
							skip = true;
						}
					}
					insertBefore = true;
					break;
				case IClasspathEntry.CPE_LIBRARY:
					if (nxjFiles.contains(cpEntry.getPath().makeAbsolute())) {
						skip = true;
					}
					insertBefore = true;
					break;
				case IClasspathEntry.CPE_PROJECT:
				case IClasspathEntry.CPE_VARIABLE:
					insertBefore = true;
				default:
					skip = false;
					
			}
			
			if (insertBefore && lc != null) {
				newClasspath.add(lc);
				lc = null;
			}
			if (!skip) {
				newClasspath.add(cpEntry);
			}
		}
		
		if (lc != null)
			newClasspath.add(lc);
		
		// set new classpath to project
		IClasspathEntry[] cpEntries = newClasspath.toArray(new IClasspathEntry[newClasspath.size()]);
		project.setRawClasspath(cpEntries, null);
	}
}
