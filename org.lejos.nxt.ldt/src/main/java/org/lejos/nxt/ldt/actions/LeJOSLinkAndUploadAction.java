package org.lejos.nxt.ldt.actions;

import java.io.File;

import lejos.pc.tools.NXJLinkAndUpload;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * @see IWorkbenchWindowActionDelegate
 */
public class LeJOSLinkAndUploadAction implements IObjectActionDelegate {
	
	private ISelection _selection;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
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
						// download firmware
						// TODO internationalization
						pm
								.beginTask(
										"Linking and uploading program to the brick...",IProgressMonitor.UNKNOWN);
						linkAndUploadProgram();
						pm.done();
						// log
						LeJOSNXJUtil.message("Program has been successfully downloaded to the NXT brick");
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
			// create arguments
			String args[] = new String[6];
			int argsCounter = 0;
			// get selected project
			IJavaProject project = LeJOSNXJUtil
					.getJavaProjectFromSelection(_selection);
			if (project == null)
				throw new LeJOSNXJException("no leJOS project selected");
			// class name
			IJavaElement javaElement = LeJOSNXJUtil
					.getFirstJavaElementFromSelection(_selection);
			String className = LeJOSNXJUtil.getClassNameFromJavaFile(javaElement.getElementName());
			args[argsCounter++] = className;
			// classpath
			args[argsCounter++] = "--classpath";
			args[argsCounter++] = createClassPath(project);
			// writeorder
			args[argsCounter++] = "--writeorder";
			args[argsCounter++] = "LE";
			// connection type
			String connectionType = LeJOSNXJPlugin.getDefault().getPluginPreferences()
					.getString(PreferenceConstants.P_CONNECTION_TYPE);
			args[argsCounter++] = "-" + connectionType; 
			// run link and upload
			delegate.run(args);
		} catch(Throwable e) {
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
	 * TODO read jars from configuration
	 * @param project
	 * @return String classpath
	 * @throws JavaModelException
	 */
	private String createClassPath(IJavaProject project) throws JavaModelException {
		String classPath = null;
		// path separator
		String pathSeparator = System.getProperty("path.separator");
		// get NXJ_HOME
		String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_NXJ_HOME);
		// classes in target directory
		File projectTargetDir = LeJOSNXJUtil.getAbsoluteProjectTargetDir(project);
		classPath = projectTargetDir.getAbsolutePath();
		// leJOS NXJ jars
		classPath += pathSeparator + nxjHome + "/lib/classes.jar";
		// third party libs
		String thirdPartyLibs = nxjHome + "/3rdparty/lib";
		classPath += pathSeparator + thirdPartyLibs + "/bluecove.jar";
		return classPath;
	}
	
	// public static IJavaElement[] getSelectedJavaElements(ISelection
	// aSelection) {
	// IStructuredSelection structured = (IStructuredSelection) aSelection;
	// Object[] oElems = structured.toArray();
	// // get only java elements
	// int noOfJavaElements = 0;
	// for (int i = 0; i < oElems.length; i++) {
	// Object elem = oElems[i];
	// if (elem instanceof IJavaElement) {
	// noOfJavaElements++;
	// }
	// }
	// // copy into type safe array
	// IJavaElement[] elems = new IJavaElement[noOfJavaElements];
	// int counter = 0;
	// for (int i = 0; (i < oElems.length) && (counter < elems.length); i++) {
	// Object elem = oElems[i];
	// if (elem instanceof IJavaElement) {
	// elems[counter++] = (IJavaElement) elem;
	// }
	// }
	// return elems;
	// }

}