package org.lejos.nxt.ldt.actions;

import lejos.pc.tools.NXJLinkAndUpload;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJLogListener;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * links and uploads a leJOS NXJ program to the brick
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSLinkAndUploadAction implements IObjectActionDelegate {

	private ISelection _selection;
	private LeJOSNXJLogListener _logListener;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
		_logListener = new LeJOSNXJLogListener();
	}

	/**
	 * TODO present error in progress dialog
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		// open progress monitor
		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {
					try {
						// upload program
						pm
								.beginTask(
										"Linking and uploading program to the brick...",
										IProgressMonitor.UNKNOWN);
						linkAndUploadProgram();
						pm.done();
						// log
						LeJOSNXJUtil
								.message("Program has been successfully uploaded to the NXT brick");
					} catch (Throwable t) {
						// log
						LeJOSNXJUtil.message(t);
					}
				} // end run
			});
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.message(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
		enableDueToSelection(action);
	}

	private void linkAndUploadProgram() throws LeJOSNXJException {
		try {
			// instantiate link and upload delegate
			NXJLinkAndUpload delegate = new NXJLinkAndUpload();
			delegate.addToolsLogListener(_logListener);
			delegate.addJSToolsLogListener(_logListener);
			// create arguments
			String args[] = new String[7];
			int argsCounter = 0;
			// get selected project
			IJavaProject project = LeJOSNXJUtil
					.getJavaProjectFromSelection(_selection);
			if (project == null)
				throw new LeJOSNXJException("no leJOS project selected");
			// class name
			IJavaElement javaElement = LeJOSNXJUtil
					.getFirstJavaElementFromSelection(_selection);
			String className = LeJOSNXJUtil
					.getClassNameFromJavaFile(javaElement.getElementName());
			args[argsCounter++] = className;
			// classpath
			args[argsCounter++] = "--classpath";
			args[argsCounter++] = createClassPath(project);
			// writeorder
			args[argsCounter++] = "--writeorder";
			args[argsCounter++] = "LE";
			// connection type
			String connectionType = LeJOSNXJPlugin.getDefault()
					.getPluginPreferences().getString(
							PreferenceConstants.P_CONNECTION_TYPE);
			args[argsCounter++] = "-" + connectionType;
			// verbosity
			boolean isVerbose = LeJOSNXJPlugin.getDefault()
					.getPluginPreferences().getBoolean(
							PreferenceConstants.P_IS_VERBOSE);
			if(isVerbose)
				args[argsCounter++] = "--verbose";
			else
				args[argsCounter++] = "";
			// run link and upload
			delegate.run(args);
		} catch (Throwable e) {
			throw new LeJOSNXJException(e);
		}
	}

	private void enableDueToSelection(IAction action) {
		boolean isEnabled = false;
		// check if selected element is a java file or a leJOS NXJ project
		IJavaElement selectedElement = LeJOSNXJUtil
				.getFirstJavaElementFromSelection(_selection);
		if (selectedElement != null) {
			if (selectedElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				// check if selected project is a leJOS project
				IJavaProject project = LeJOSNXJUtil
						.getJavaProjectFromSelection(_selection);
				if (project != null) {
					try {
						if (LeJOSNXJUtil.isLeJOSProject(project)) {
							isEnabled = true;
						}
					} catch (CoreException e) {
						LeJOSNXJUtil.message(e);
					}
				}
			}
		}
		// set state
		action.setEnabled(isEnabled);
	}

	/**
	 * 
	 * build the classpath for the link and upload utility
	 * 
	 * @param project
	 * @return String classpath
	 * @throws JavaModelException
	 */
	private String createClassPath(IJavaProject project)
			throws JavaModelException {
		String classPath = LeJOSNXJUtil.getAbsoluteProjectTargetDir(project)
				.getAbsolutePath();
		// path separator
		String pathSeparator = System.getProperty("path.separator");
		// project's classpath
		IClasspathEntry[] entries = project.getResolvedClasspath(true);
		// build string
		for (IClasspathEntry classpathEntry : entries) {
			classPath += pathSeparator + classpathEntry.getPath().toOSString();
		}
		return classPath;
	}

}